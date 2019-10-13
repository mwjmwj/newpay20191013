package com.polypay.platform.managercontroller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.polypay.platform.bean.MerchantAccountInfo;
import com.polypay.platform.bean.MerchantFinance;
import com.polypay.platform.bean.PayType;
import com.polypay.platform.exception.ServiceException;
import com.polypay.platform.service.IMerchantFinanceService;
import com.polypay.platform.service.IMerchantRechargeOrderService;
import com.polypay.platform.service.IPayTypeService;
import com.polypay.platform.utils.DateUtils;
import com.polypay.platform.utils.MerchantUtils;
import com.polypay.platform.vo.MerchantMainDateVO;
import com.polypay.platform.vo.MerchantRechargeOrderVO;

@Controller
public class ProxyMainMenuController {

	@Autowired
	private IMerchantRechargeOrderService merchantRechargeOrderService;
	
	
	@Autowired
	private IMerchantFinanceService merchantFinanceService;
	
	@Autowired
	private IPayTypeService payTypeService;
	
	@RequestMapping("proxy/mainmenu")
	public String getMain(Map<String,Object> result) throws ServiceException
	{
		
		MerchantAccountInfo merchant = MerchantUtils.getMerchant();
		
		MerchantMainDateVO mainDate = merchantRechargeOrderService.sumProxyMerchantRechargeOrder(merchant.getUuid());
		
		MerchantRechargeOrderVO param = new MerchantRechargeOrderVO();
		param.setProxyId(merchant.getUuid());
		Date[] todayTime = DateUtils.getTodayTime();
		param.setBeginTime(todayTime[0]);
		param.setEndTime(todayTime[1]);
		
		MerchantMainDateVO todayMainDate = merchantRechargeOrderService.sumTodayProxyMerchantRechargeOrder(param);

		
		MerchantFinance allMerchantFinance = merchantFinanceService.allProxyMerchantMerchantFinance(merchant.getUuid());
		
		
		result.put("data", mainDate);
		result.put("todaydata", todayMainDate);
		result.put("finance", allMerchantFinance);
		
		
		MerchantFinance merchantFinanceByUUID = merchantFinanceService.getMerchantFinanceByUUID(merchant.getUuid());
		PayType payTypeChannel = payTypeService.getPayTypeChannel(merchant.getUuid(), "WY");
		
		
		result.put("wefinance", merchantFinanceByUUID);
		result.put("paytype", payTypeChannel);
		
		return "proxymanager/main";
	}
	
}
