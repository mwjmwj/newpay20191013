package com.polypay.platform.managercontroller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.polypay.platform.Page;
import com.polypay.platform.ResponseUtils;
import com.polypay.platform.ServiceResponse;
import com.polypay.platform.bean.MerchantAccountInfo;
import com.polypay.platform.bean.MerchantFinance;
import com.polypay.platform.bean.MerchantVerify;
import com.polypay.platform.bean.PayType;
import com.polypay.platform.consts.MerchantAccountInfoStatusConsts;
import com.polypay.platform.consts.MerchantFinanceStatusConsts;
import com.polypay.platform.consts.MerchantHelpPayConsts;
import com.polypay.platform.consts.RequestStatus;
import com.polypay.platform.consts.RoleConsts;
import com.polypay.platform.consts.VerifyTypeEnum;
import com.polypay.platform.controller.BaseController;
import com.polypay.platform.exception.ServiceException;
import com.polypay.platform.service.IMerchantAccountInfoService;
import com.polypay.platform.service.IMerchantFinanceService;
import com.polypay.platform.service.IMerchantVerifyService;
import com.polypay.platform.service.IPayTypeService;
import com.polypay.platform.utils.DateUtils;
import com.polypay.platform.utils.HttpClientUtil;
import com.polypay.platform.utils.HttpRequestDetailVo;
import com.polypay.platform.utils.MD5;
import com.polypay.platform.utils.MerchantUtils;
import com.polypay.platform.utils.UUIDUtils;
import com.polypay.platform.vo.MerchantAccountInfoVO;

@Controller
public class ManagerMerchantAccountController extends BaseController<MerchantAccountInfoVO> {

	private Logger log = LoggerFactory.getLogger(ManagerMerchantAccountController.class);

	@Autowired
	private IMerchantAccountInfoService merchantAccountInfoService;

	@Autowired
	private IMerchantVerifyService merchantVerifyService;
	
	@Autowired
	private IPayTypeService payTypeService;
	
	@Autowired
	private IMerchantFinanceService merchantFinanceService;

	
	@RequestMapping("proxy/register/merchant")
	@ResponseBody
	public ServiceResponse registerMerchant(MerchantAccountInfoVO requestMerchantInfo) throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		MerchantVerify merchantVerify;
		boolean verifyFlag;
		MerchantAccountInfo merchantAccountInfo;
		try {
			// 根据用户邮箱或手机号获取用户
			merchantAccountInfo = merchantAccountInfoService.getMerchantInfo(requestMerchantInfo);

			
			// 用户存在返回
			if (null != merchantAccountInfo) {
				ResponseUtils.exception(response, "该用户已存在！", RequestStatus.FAILED.getStatus());
				return response;
			}

			String helpPay = getRequest().getParameter("switch");
			
			requestMerchantInfo.setHelppayStatus("on".equals(helpPay)?MerchantHelpPayConsts.OPEN_HELP_PAY:MerchantHelpPayConsts.CLOSE_HELP_PAY);
			
			// 获取用户验证码
			merchantVerify = new MerchantVerify();
			MerchantAccountInfo merchant = MerchantUtils.getMerchant();
			merchantVerify.setMobileNumber(merchant.getMobileNumber());
			merchantVerify.setType((VerifyTypeEnum.REGISTER_MERCHANT));
			

			merchantVerify = merchantVerifyService.queryMerchantVerifyCode(merchantVerify);

			// 没有验证码或者验证码不匹配 返回
			if (null == merchantVerify || !requestMerchantInfo.getVerifyCode().equals(merchantVerify.getCode())) {
				ResponseUtils.exception(response, null == merchantVerify ? "未获取验证码,获取有效验证码" : "验证码不正确,请重新输入",
						RequestStatus.FAILED.getStatus());
				return response;
			}

			// 验证码失效重新提示用户重新获取
			verifyFlag = DateUtils.comperDate(new Date(), merchantVerify.getAvaliableTime());
			if (!verifyFlag) {
				ResponseUtils.exception(response, "验证码已失效,请重新获取", RequestStatus.FAILED.getStatus());
				return response;
			}
			requestMerchantInfo.setPassWord("123456");
			requestMerchantInfo.setPayPassword("123456");
			requestMerchantInfo.setProxyId(StringUtils.isEmpty(requestMerchantInfo.getProxyId())?MerchantUtils.getMerchant().getUuid():requestMerchantInfo.getProxyId());
			
			try {
				// 用户注册
				merchantAccountInfoService.registerAndSave(requestMerchantInfo);
				
				//新建支付费率
				PayType payType = new PayType();
				payType.setMerchantId(requestMerchantInfo.getUuid());
				payType.setRate(new BigDecimal(requestMerchantInfo.getRate()));
				payType.setName("WY");
				payType.setStatus(0);
				payTypeService.insertSelective(payType);
				response.setMessage("注册成功!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 密码加密返回
			requestMerchantInfo.setPassWord(null);
			requestMerchantInfo.setPayPassword(null);

		} catch (ServiceException e) {
			// 注册 插入数据库异常返回数据库异常信息
			log.error(response.getRequestId() + " register Merchant fail ");
			ResponseUtils.exception(response, e.getMessage(), RequestStatus.FAILED.getStatus());
		} catch (Exception e) {
			// 其他异常 捕获返回异常msg
			log.error(response.getRequestId() + "get verifycode fail ");
			ResponseUtils.exception(response, e.getMessage(), RequestStatus.FAILED.getStatus());
		}

		return response;
	}
	
	@RequestMapping("managerregister/proxy")
	@ResponseBody
	public ServiceResponse registerProxy(MerchantAccountInfoVO requestMerchantInfo) throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		MerchantVerify merchantVerify;
		boolean verifyFlag;
		MerchantAccountInfo merchantAccountInfo;
		try {

			// 根据用户邮箱或手机号获取用户
			merchantAccountInfo = merchantAccountInfoService.getMerchantInfo(requestMerchantInfo);

			// 用户存在返回
			if (null != merchantAccountInfo) {
				ResponseUtils.exception(response, "该用户已存在！", RequestStatus.FAILED.getStatus());
				return response;
			}

			// 获取用户验证码
			merchantVerify = new MerchantVerify();
			MerchantAccountInfo merchant = MerchantUtils.getMerchant();
			merchantVerify.setMobileNumber(merchant.getMobileNumber());
			merchantVerify.setType(VerifyTypeEnum.REGISTER_PROXY);
			

			merchantVerify = merchantVerifyService.queryMerchantVerifyCode(merchantVerify);

			// 没有验证码或者验证码不匹配 返回
			if (null == merchantVerify || !requestMerchantInfo.getVerifyCode().equals(merchantVerify.getCode())) {
				ResponseUtils.exception(response, null == merchantVerify ? "未获取验证码,获取有效验证码" : "验证码不正确,请重新输入",
						RequestStatus.FAILED.getStatus());
				return response;
			}

			// 验证码失效重新提示用户重新获取
			verifyFlag = DateUtils.comperDate(new Date(), merchantVerify.getAvaliableTime());
			if (!verifyFlag) {
				ResponseUtils.exception(response, "验证码已失效,请重新获取", RequestStatus.FAILED.getStatus());
				return response;
			}
			
			// 代理商注册
			MerchantAccountInfo merchantAccount = new MerchantAccountInfo();
			String uuid = UUIDUtils.get32UUID();
			
			merchantAccount.setMobileNumber(requestMerchantInfo.getMobileNumber());
			merchantAccount.setAccountName(requestMerchantInfo.getAccountName());
			merchantAccount.setUuid(uuid);
			merchantAccount.setStatus(MerchantAccountInfoStatusConsts.SUCCESS);
			String passWord = "asdf@123";
			String md5Password = MD5.md5(passWord);
			merchantAccount.setPassWord(md5Password);
			merchantAccount.setRoleId(RoleConsts.PROXY);
			merchantAccount.setCreateTime(new Date());
			
			merchantAccount.setHandAmount(requestMerchantInfo.getHandAmount());
			merchantAccountInfoService.insertSelective(merchantAccount);

			response.setMessage("注册成功!");
			
			try {
				//新建支付费率
				PayType payType = new PayType();
				payType.setMerchantId(uuid);
				payType.setRate(new BigDecimal(requestMerchantInfo.getRate()));
				payType.setName("WY");
				payType.setStatus(0);
				payTypeService.insertSelective(payType);
				response.setMessage("注册成功!");
				
				
				// 保存财务信息
				MerchantFinance merchantFinance = new MerchantFinance();
				merchantFinance.setMerchantId(uuid);
				merchantFinance.setCreateTime(new Date());
				merchantFinance.setBlanceAmount(new BigDecimal(0.0));
				String payPassWord = "asdf@123";
				String md5PayPassword = MD5.md5(payPassWord);
				merchantFinance.setPayPassword(md5PayPassword);
				merchantFinance.setFronzeAmount(new BigDecimal(0.0));
				merchantFinance.setStatus(MerchantFinanceStatusConsts.FREEZE);
				merchantFinanceService.insertSelective(merchantFinance);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 密码加密返回
			requestMerchantInfo.setPassWord(null);;

		} catch (ServiceException e) {
			// 注册 插入数据库异常返回数据库异常信息
			log.error(response.getRequestId() + " register Merchant fail ");
			ResponseUtils.exception(response, e.getMessage(), RequestStatus.FAILED.getStatus());
		} catch (Exception e) {
			// 其他异常 捕获返回异常msg
			log.error(response.getRequestId() + "get verifycode fail ");
			ResponseUtils.exception(response, e.getMessage(), RequestStatus.FAILED.getStatus());
		}

		return response;
	}
	
	@RequestMapping("/proxy/merchantmanager/account/list")
	@ResponseBody
	public ServiceResponse listProxyMerchantAccountInfo() throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		try {
			PageBounds pageBounds = this.getPageBounds();
			PageList<MerchantAccountInfoVO> pageList = null;
			MerchantAccountInfoVO param = new MerchantAccountInfoVO();

			// 代理商
			String proxyid = MerchantUtils.getMerchant().getUuid();
			param.setProxyId(proxyid);
			// 商户状态
			String status = getRequest().getParameter("status");
			if (StringUtils.isNotEmpty(status)) {
				param.setStatus(Integer.parseInt(status));
			}

			// 代付等级
			String paylevel = getRequest().getParameter("paylevel");
			if (StringUtils.isNotEmpty(paylevel)) {
				param.setPayLevel(Integer.parseInt(paylevel));
			}

			// 商户手机号
			String mobileNumber = getRequest().getParameter("mobileNumber");
			if (StringUtils.isNotEmpty(mobileNumber)) {
				param.setMobileNumber(mobileNumber);
			}

			param.setRoleId(RoleConsts.MERCHANT);
			pageList = merchantAccountInfoService.listMerchantAccountInfo(pageBounds, param);

			Page<MerchantAccountInfoVO> pageData = getPageData(pageList);
			response = ResponseUtils.buildResult(pageData);
		} catch (ServiceException e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		} catch (Exception e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		}
		return response;

	}

	@RequestMapping("/merchantmanager/account/list")
	@ResponseBody
	public ServiceResponse listMerchantAccountInfo() throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		try {
			PageBounds pageBounds = this.getPageBounds();
			PageList<MerchantAccountInfoVO> pageList = null;
			MerchantAccountInfoVO param = new MerchantAccountInfoVO();

			// 代理商
			String proxyid = getRequest().getParameter("proxyId");
			if (StringUtils.isNotEmpty(proxyid)) {
				param.setProxyId(proxyid);
			}

			// 商户状态
			String status = getRequest().getParameter("status");
			if (StringUtils.isNotEmpty(status)) {
				param.setStatus(Integer.parseInt(status));
			}

			// 代付等级
			String paylevel = getRequest().getParameter("paylevel");
			if (StringUtils.isNotEmpty(paylevel)) {
				param.setPayLevel(Integer.parseInt(paylevel));
			}

			// 商户手机号
			String mobileNumber = getRequest().getParameter("mobileNumber");
			if (StringUtils.isNotEmpty(mobileNumber)) {
				param.setMobileNumber(mobileNumber);
			}

			param.setRoleId(RoleConsts.MERCHANT);
			pageList = merchantAccountInfoService.listMerchantAccountInfo(pageBounds, param);

			Page<MerchantAccountInfoVO> pageData = getPageData(pageList);
			response = ResponseUtils.buildResult(pageData);
		} catch (ServiceException e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		} catch (Exception e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		}
		return response;

	}
	
	@RequestMapping("proxy/account/list")
	@ResponseBody
	public ServiceResponse listProxyAccountInfo() throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		try {
			PageBounds pageBounds = this.getPageBounds();
			PageList<MerchantAccountInfoVO> pageList = null;
			MerchantAccountInfoVO param = new MerchantAccountInfoVO();
			
			// 代理商状态
			String status = getRequest().getParameter("status");
			if (StringUtils.isNotEmpty(status)) {
				param.setStatus(Integer.parseInt(status));
			}

			// 代付等级
			String paylevel = getRequest().getParameter("paylevel");
			if (StringUtils.isNotEmpty(paylevel)) {
				param.setPayLevel(Integer.parseInt(paylevel));
			}

			// 代理商手机号
			String mobileNumber = getRequest().getParameter("mobileNumber");
			if (StringUtils.isNotEmpty(mobileNumber)) {
				param.setMobileNumber(mobileNumber);
			}

			param.setRoleId(RoleConsts.PROXY);
			pageList = merchantAccountInfoService.listMerchantAccountInfo(pageBounds, param);

			Page<MerchantAccountInfoVO> pageData = getPageData(pageList);
			response = ResponseUtils.buildResult(pageData);
		} catch (ServiceException e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		} catch (Exception e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			throw new ServiceException(e.getMessage(), RequestStatus.FAILED.getStatus());
		}
		return response;

	}

	@RequestMapping("merchantmanager/account/query")
	public String getMerchantAccountInfo(@RequestParam("id") String merchantUUId, Map<String, Object> result)
			throws ServiceException {

		MerchantAccountInfoVO param = new MerchantAccountInfoVO();
		param.setUuid(merchantUUId);
		MerchantAccountInfo merchantInfoByUUID = merchantAccountInfoService.getMerchantInfoByUUID(param);
		result.put("merchantAccount", merchantInfoByUUID);

		return "adminmanager/managermerchantaccountaudit";

	}
	
	@RequestMapping("merchantmanager/proxy/account/query")
	public String getProxyMerchantAccountInfo(@RequestParam("id") String merchantUUId, Map<String, Object> result)
			throws ServiceException {

		MerchantAccountInfoVO param = new MerchantAccountInfoVO();
		param.setUuid(merchantUUId);
		MerchantAccountInfo merchantInfoByUUID = merchantAccountInfoService.getMerchantInfoByUUID(param);
		result.put("merchantAccount", merchantInfoByUUID);

		return "adminmanager/proxyaccountedit";

	}

	@RequestMapping("merchantmanager/accountinfo/update")
	@ResponseBody
	public String updateMerchantAccountInfo(MerchantAccountInfo uMerchant) throws ServiceException {
		if ("on".equals(uMerchant.getHelppayoff())) {
			uMerchant.setHelppayStatus(MerchantHelpPayConsts.OPEN_HELP_PAY);
		} else {
			uMerchant.setHelppayStatus(MerchantHelpPayConsts.CLOSE_HELP_PAY);
		}
		
		MerchantAccountInfo updateMerchant = new MerchantAccountInfo();
		updateMerchant.setHelppayStatus(uMerchant.getHelppayStatus());
		updateMerchant.setAccountName(uMerchant.getAccountName());
		updateMerchant.setMobileNumber(uMerchant.getMobileNumber());
		updateMerchant.setPassWord(uMerchant.getPassWord());
		updateMerchant.setPayLevel(uMerchant.getPayLevel());
		updateMerchant.setStatus(uMerchant.getStatus());
		updateMerchant.setId(uMerchant.getId());
		
		merchantAccountInfoService.updateByPrimaryKeySelective(uMerchant);
		return "success";
	}

	@RequestMapping("merchantmanager/verifycode")
	@ResponseBody
	public ServiceResponse sendVerifyCode() throws ServiceException {
		ServiceResponse response = new ServiceResponse();
		MerchantVerify merchantVerify = new MerchantVerify();
		try {
			this.sendVerifyCode(response, merchantVerify);
			if (response.getStatus() != RequestStatus.SUCCESS.getStatus()) {
				return response;
			}

			// 发送成功 修改验证码有效期
			merchantVerify.setAvaliableTime(getFiveMinuteLater());
			merchantVerifyService.updateByPrimaryKeySelective(merchantVerify);

			return response;
		} catch (ServiceException e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			ResponseUtils.exception(response, "验证码获取失败,请重新获取" + e.getMessage(), RequestStatus.FAILED.getStatus());
			merchantVerify.setAvaliableTime(new Date());
			merchantVerifyService.updateByPrimaryKeySelective(merchantVerify);
		} catch (Exception e) {
			log.error(response.getRequestId() + " " + e.getMessage());
			ResponseUtils.exception(response, "验证码获取失败,请重新获取" + e.getMessage(), RequestStatus.FAILED.getStatus());
			merchantVerify.setAvaliableTime(new Date());
			merchantVerifyService.updateByPrimaryKeySelective(merchantVerify);
		}
		return response;
	}
	


	@SuppressWarnings("deprecation")
	private void sendVerifyCode(ServiceResponse response, MerchantVerify merchantVerify)
			throws ServiceException, IllegalAccessException, InvocationTargetException {
		MerchantVerify tbVerifycodeVO = new MerchantVerify();
		BeanUtils.copyProperties(tbVerifycodeVO, merchantVerify);
		if (null != merchantVerify.getMobileNumber()) {

			/**
			 * 发送手机验证码 成功直接return
			 */

			StringBuilder url = new StringBuilder();
			url.append("http://m.5c.com.cn/api/send/index.php?").append("username=zhang1")
					.append("&password_md5=1adbb3178591fd5bb0c248518f39bf6d")
					.append("&apikey=2cd1102e4b32661f0aadee35d9940985").append("&mobile=")
					.append(merchantVerify.getMobileNumber()).append("&encode=UTF-8").append("&content=")
					.append(URLEncoder
							.encode("【源盛丰】您的验证码是 :" + merchantVerify.getCode() + ",有效2分钟。请勿泄露验证码！如不是您本人操作，请忽略."));

			HttpRequestDetailVo httpGet = HttpClientUtil.httpGet(url.toString());

			if (StringUtils.isNotEmpty(httpGet.getResultAsString())
					&& httpGet.getResultAsString().contains("success")) {
				response.setMessage("发送成功");
				return;
			}

			// 设置验证码有效期为过期
			merchantVerify.setAvaliableTime(new Date());
			merchantVerifyService.updateByPrimaryKeySelective(merchantVerify);
			ResponseUtils.exception(response, "验证码发送失败,请重新获取", RequestStatus.FAILED.getStatus());
			return;
		}
	}

	private Date getFiveMinuteLater() {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.MINUTE, instance.get(Calendar.MINUTE) + 5);
		return instance.getTime();
	}

}
