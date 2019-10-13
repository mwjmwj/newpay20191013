package com.polypay.platform.managercontroller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.polypay.platform.Page;
import com.polypay.platform.ResponseUtils;
import com.polypay.platform.ServiceResponse;
import com.polypay.platform.consts.RequestStatus;
import com.polypay.platform.controller.BaseController;
import com.polypay.platform.exception.ServiceException;
import com.polypay.platform.service.IMerchantLoginLogSerivce;
import com.polypay.platform.vo.MerchantLoginLogVO;

@Controller
public class ManagerMerchantLoginIpController extends BaseController<MerchantLoginLogVO> {

	private Logger log = LoggerFactory.getLogger(ManagerMerchantAccountController.class);

	@Autowired
	private IMerchantLoginLogSerivce merchantLoginLogSerivce;

	@RequestMapping("/merchantmanager/loginip/list")
	@ResponseBody
	public ServiceResponse listMerchantLoginIp() throws ServiceException {

		ServiceResponse response = new ServiceResponse();
		try {
			PageBounds pageBounds = this.getPageBounds();
			MerchantLoginLogVO merchantApi = new MerchantLoginLogVO();
			PageList<MerchantLoginLogVO> pageList = null;
			
			String merchantId = getRequest().getParameter("merchantId");
			if(!StringUtils.isEmpty(merchantId)) {
				merchantApi.setMerchantId(merchantId);
			}
			
			pageList = merchantLoginLogSerivce.listManagerMerchantLoginLog(pageBounds, merchantApi);
			Page<MerchantLoginLogVO> pageData = getPageData(pageList);
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
