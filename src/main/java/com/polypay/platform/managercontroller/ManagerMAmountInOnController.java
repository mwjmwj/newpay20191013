package com.polypay.platform.managercontroller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.polypay.platform.Page;
import com.polypay.platform.ResponseUtils;
import com.polypay.platform.ServiceResponse;
import com.polypay.platform.bean.MerchantFinance;
import com.polypay.platform.consts.RequestStatus;
import com.polypay.platform.controller.BaseController;
import com.polypay.platform.exception.ServiceException;
import com.polypay.platform.service.IMerchantAccountInfoService;
import com.polypay.platform.service.IMerchantFinanceService;
import com.polypay.platform.service.IMerchantRechargeOrderService;
import com.polypay.platform.vo.MerchantAllRechargeVO;
import com.polypay.platform.vo.MerchantRechargeOrderVO;

@Controller
public class ManagerMAmountInOnController extends BaseController {

	private Logger log = LoggerFactory.getLogger(ManagerMAmountInOnController.class);

	@Autowired
	private IMerchantRechargeOrderService merchantRechargeOrderService;

	@Autowired
	private IMerchantFinanceService merchantFinanceService;

	@Autowired
	private IMerchantAccountInfoService merchantAccountInfoService;
	
	@RequestMapping("/proxy/merchantdetaillist")
	@ResponseBody
	public ServiceResponse listMerchantrechargeall() throws ServiceException {

		ServiceResponse response = new ServiceResponse();

		try {

			PageBounds pageBounds = this.getPageBounds();
			PageList<MerchantAllRechargeVO> pageList = null;
			MerchantRechargeOrderVO merchantRechargeOrderVO = new MerchantRechargeOrderVO();

			// 代理人
			String proxyId = getRequest().getParameter("proxyId");
			if (!StringUtils.isEmpty(proxyId)) {
				merchantRechargeOrderVO.setProxyId(proxyId);
			}

			// 商户
			String merchantId = getRequest().getParameter("merchantId");
			if (!StringUtils.isEmpty(merchantId)) {
				merchantRechargeOrderVO.setMerchantId(merchantId);
				;
			}
			
			pageBounds.setLimit(50);
			// 订单信息
			pageList = merchantRechargeOrderService.listMerchantrechargeall(pageBounds, merchantRechargeOrderVO);
			Page<MerchantAllRechargeVO> pageData = getPageData(pageList);
			
			
			// 获取代理人
			List<String> mids = merchantAccountInfoService.listMerchantAccountInfoByProxy(proxyId);
			pageData.getRows().forEach(m -> {
				mids.add(m.getMerchantId());
			});

			List<MerchantFinance> listFindMerchantFinance = Lists.newArrayList();
			if(CollectionUtils.isNotEmpty(mids))
			{
				listFindMerchantFinance = merchantFinanceService.listFindMerchantFinance(mids);
			}

			Map<String, MerchantFinance> maps = Maps.newHashMap();
			listFindMerchantFinance.forEach(l -> {
				maps.put(l.getMerchantId(), l);
			});

			List<MerchantAllRechargeVO> rows = pageData.getRows();

			rows.forEach(r -> {
				MerchantFinance merchantFinance = maps.get(r.getMerchantId());
				if (null != merchantFinance) {
					r.setBalanceAmount(merchantFinance.getBlanceAmount());
					r.setFrezzAmount(merchantFinance.getFronzeAmount());
					r.setProxyId(proxyId);
				}
			});

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

}
