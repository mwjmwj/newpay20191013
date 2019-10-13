//package com.polypay.platform.paychannel;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.baomidou.mybatisplus.toolkit.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.utils.MD5;
//
//
///**
// *   双乾支付
// * @author Administrator
// *
// */
//public class SQChannel implements IPayChannel{
//
//	private final static Logger log = LoggerFactory.getLogger(SQChannel.class);
//
//	private static String merchantNo = "10955";
//
//	private static String apiKey = "4d34df360fc5ccc2f5345c065a73771f5239edb5";
//
//	private static String payUrl = "http://www.95epay.vip/apisubmit";
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//
//		StringBuffer signParam = new StringBuffer();
//		Map<String, String> transMap = Maps.newTreeMap();
//
//		String version = "1.0";
//		signParam.append("version="+version);
//		transMap.put("version", version);
//
//		String customerid = merchantNo;
//		signParam.append("&customerid="+customerid);
//		transMap.put("customerid", customerid);
//
//		Object pay_amount = param.get("pay_amount");
//		BigDecimal total_amount = new BigDecimal(pay_amount.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
//		String amount = total_amount.toString();
//		String total_fee = amount;
//		signParam.append("&total_fee="+total_fee);
//		transMap.put("total_fee", total_fee);
//
//
//		// 交易流水
//		Object order_number = param.get("we_order_number");
//		String tranSerialNum = order_number.toString();
//		String sdorderno = tranSerialNum;
//		signParam.append("&sdorderno="+sdorderno);
//		transMap.put("sdorderno", sdorderno);
//
//
//		String notifyurl = param.get("call_back").toString();
//		signParam.append("&notifyurl="+notifyurl);
//		transMap.put("notifyurl", notifyurl);
//
//		String returnurl = "www.xx.com";
//		signParam.append("&returnurl="+returnurl);
//		transMap.put("returnurl", returnurl);
//
//		String sign = MD5.md5(signParam.append("&"+apiKey).toString()).toLowerCase();
//		transMap.put("sign", sign);
//
//		String paytype="shuang";
//		transMap.put("paytype", paytype);
//
//		String bankcode= param.get("bank_code").toString();
//		transMap.put("bankcode", bankcode);
//
//
//		log.info("SQChannel :" + transMap);
//
//		request.setAttribute("action", payUrl);
//		request.setAttribute("dataMap", transMap);
//
//		/**
//		 * version={value}&customerid={value}&total_fee={value}
//		 * &sdorderno={value}
//		 * &notifyurl={value}&returnurl={value}&{apikey}
//		 */
//
//
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//
//		StringBuffer signParam = new StringBuffer();
//
//		String status = getParameter(request,"status");
//
//		String customerid = getParameter(request,"customerid");
//
//		String sdpayno = getParameter(request,"sdpayno");
//
//		String sdorderno = getParameter(request,"sdorderno");
//
//		String total_fee = getParameter(request,"total_fee");
//
//		String paytype = getParameter(request,"paytype");
//
//		String sign = getParameter(request,"sign");
//
//		/**
//		 *
//		 *  notify=customerid=10955&status=1&sdpayno=2019042516363366098&sdorderno=R20190425163636233214267&total_fee=1.00&paytype=shuang&4d34df360fc5ccc2f5345c065a73771f5239edb5
//		 */
//		signParam.append("customerid="+customerid)
//		.append("&status="+status)
//		.append("&sdpayno="+sdpayno)
//		.append("&sdorderno="+sdorderno)
//		.append("&total_fee="+total_fee)
//		.append("&paytype="+paytype)
//		.append("&"+apiKey);
//
//		String sign1= MD5.md5(signParam.toString()).toLowerCase();
//
//		Map<String, Object> result = Maps.newHashMap();
//		log.info("notify=" + signParam);
//		log.info("sign=" + sign);
//		log.info("sign1=" + sign1);
//		if (!sign.equals(sign1)) {
//			result.put("status", "-10");
//			return result;
//		}
//
//
//		if ("1".equals(status)) // 成功
//		{
//			result.put("status", "1");
//			result.put("total_fee", new BigDecimal(total_fee));
//			result.put("orderno", sdorderno);
//			result.put("sdpayno", sdpayno);
//
//		} else // 失败
//		{
//			result.put("status", "-1");
//			result.put("orderno", sdorderno);
//			result.put("sdpayno", sdpayno);
//		}
//
//		/**
//		 *  customerid={value}&status={value}&sdpayno={value}&sdorderno={value}&total_fee={value}&paytype={value}&{apikey}
//		 */
//		return result;
//	}
//
//	public String getParameter(HttpServletRequest request, String key) {
//		String parameter = request.getParameter(key);
//		return StringUtils.isEmpty(parameter) ? "" : parameter;
//	}
//
//	@Override
//	public Map<String, Object> getOrder(String orderNumber) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, Object> settleOrder(MerchantSettleOrder selectByPrimaryKey) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, Object> placeOrder(MerchantPlaceOrder selectByPrimaryKey) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, Object> taskPayOrderNumber(String orderNumber, Date date) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
