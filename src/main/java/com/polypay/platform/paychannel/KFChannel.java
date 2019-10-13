//package com.polypay.platform.paychannel;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.alibaba.fastjson.JSON;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.utils.DateUtil;
//import com.polypay.platform.utils.HttpClientUtil;
//import com.polypay.platform.utils.HttpRequestDetailVo;
//import com.polypay.platform.utils.MD5;
//
//public class KFChannel implements IPayChannel {
//
//	private final static Logger log = LoggerFactory.getLogger(KFChannel.class);
//
//	private static String key =  "aOFexMSbKZmHyAsdvRJPmxBAGAzjDnNk";
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//
//		String payUrl = "http://58.82.213.4/Pay";
//
//		String fxid = "2019128";
//
//		String fxnotifyurl = param.get("call_back").toString();
//
//		String fxbackurl = "http://www.x.com";
//
//		String fxattch = "test";
//
//		String fxdesc = "话费";
//
//		Object pay_amount = param.get("pay_amount");
//		BigDecimal total_amount = new BigDecimal(pay_amount.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
//		String fxfee = total_amount.toString();
//
//		// alipay_scan】【支付宝H5：alipay_wap】【网银支付：bank_pay
//		String fxpay = param.get("pay_channel").toString();
//
//		Object order_number = param.get("we_order_number");
//		String fxddh = order_number.toString(); // 订单号
//
//		String fxbankcode = param.get("bank_code").toString();
//
//		String fxip = "127.0.0.1";
//
//		// 订单签名
//		String fxsign = MD5.md5(fxid + fxddh + fxfee + fxnotifyurl + key);
//		fxsign = fxsign.toLowerCase();
//
//		Map<String, String> reqMap = new HashMap<String, String>();
//		reqMap.put("fxid", fxid);
//		reqMap.put("fxddh", fxddh);
//		reqMap.put("fxfee", fxfee);
//		reqMap.put("fxpay", fxpay);
//		reqMap.put("fxnotifyurl", fxnotifyurl);
//		reqMap.put("fxbackurl", fxbackurl);
//		reqMap.put("fxattch", fxattch);
//		reqMap.put("fxdesc", fxdesc);
//		reqMap.put("fxip", fxip);
//		reqMap.put("fxsign", fxsign);
//		reqMap.put("fxbankcode", fxbankcode);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(payUrl, null, reqMap);
//		Map result  = JSON.parseObject(httpPost.getResultAsString(),Map.class);
//		log.info("pay - > " + result);
//
//		Object object = result.get("status");
//
//		if ("1".equals(object.toString())) {
//			try {
//				response.sendRedirect(result.get("payurl").toString());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//
//		Map<String, Object> result = Maps.newHashMap();
//
//		String getid = request.getParameter("fxid");
//		String getddh = request.getParameter("fxddh");
//		String getorder = request.getParameter("fxorder");
//		String getattch = request.getParameter("fxattch");
//		String getdesc = request.getParameter("fxdesc");
//		String getfee = request.getParameter("fxfee");
//		String getstatus = request.getParameter("fxstatus");
//		String gettime = request.getParameter("fxtime");
//		String getsign = request.getParameter("fxsign");
//
//		String successstatus = "1";
//
//		// 订单签名 【md5(订单状态+商务号+商户订单号+支付金额+商户秘钥)】
//		String mysign = MD5.md5(getstatus + getid + getddh + getfee + key);
//		mysign = mysign.toLowerCase();
//
//		if (mysign.equals(getsign) != true) {
//			result.put("status", "-10");
//			return result;
//		}
//
//
//		if ("1".equals(getstatus)) // 成功
//		{
//			result.put("status", "1");
//			result.put("total_fee", new BigDecimal(getfee));
//			result.put("orderno", getddh);
//		} else // 失败
//		{
//			result.put("status", "-1");
//			result.put("orderno", getddh);
//		}
//		return result;
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
//
//		String setUrl = "http://58.82.213.4/Pay";
//
//		Map<String,String> param = Maps.newHashMap();
//
//		String fxid = "2019128";
//		param.put("fxid", fxid);
//
//		String fxaction = "repay";
//		param.put("fxaction", fxaction);
//
//		String fxnotifyurl = "http://www.xx.com";
//		param.put("fxnotifyurl", fxnotifyurl);
//
//		String fxnotifystyle = "2";
//		param.put("fxnotifystyle", fxnotifystyle);
//
//		List<Map<String,String>> fxbody = Lists.newArrayList();
//		Map<String,String> fxMap = Maps.newHashMap();
//		fxMap.put("fxddh", selectByPrimaryKey.getOrderNumber());
//		fxMap.put("fxdate", DateUtil.getCurrentDate1());//YYYYMMDDhhmmss
//		fxMap.put("fxfee", selectByPrimaryKey.getPostalAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString());
//		fxMap.put("fxbody", selectByPrimaryKey.getMerchantBindBank());
//		fxMap.put("fxname", selectByPrimaryKey.getAccountName());
//		fxMap.put("fxaddress", selectByPrimaryKey.getBankName());
//		fxMap.put("fxzhihang", selectByPrimaryKey.getBranchBankName());
//		fxMap.put("fxsheng", selectByPrimaryKey.getAccountProvice());
//		fxMap.put("fxshi", selectByPrimaryKey.getAccountCity());
//		fxMap.put("fxlhh", selectByPrimaryKey.getBankNo());
//		fxbody.add(fxMap);
//		param.put("fxbody", JSONUtils.toJSONString(fxbody));
//
//		String fxsign = MD5.md5(fxid+fxaction+JSONUtils.toJSONString(fxbody)+key).toLowerCase();
//		param.put("fxsign", fxsign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(setUrl, null, param);
//
//		Map result = (Map) JSONUtils.parse(httpPost.getResultAsString());
//
//
//		Object status = result.get("fxstatus").toString();
//
//		Map eresult = Maps.newHashMap();
//		if ("1".equals(status)) // 1表示成功
//		{
//			eresult.put("status", "1");
//		} else { // 0表示异常
//			eresult.put("status", "0");
//		}
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> placeOrder(MerchantPlaceOrder selectByPrimaryKey) {
//String setUrl = "http://58.82.213.4/Pay";
//
//		Map<String,String> param = Maps.newHashMap();
//
//		String fxid = "2019128";
//		param.put("fxid", fxid);
//
//		String fxaction = "repay";
//		param.put("fxaction", fxaction);
//
//		String fxnotifyurl = "http://www.xx.com";
//		param.put("fxnotifyurl", fxnotifyurl);
//
//		String fxnotifystyle = "2";
//		param.put("fxnotifystyle", fxnotifystyle);
//
//		List<Map<String,String>> fxbody = Lists.newArrayList();
//		Map<String,String> fxMap = Maps.newHashMap();
//		fxMap.put("fxddh", selectByPrimaryKey.getOrderNumber());
//		fxMap.put("fxdate", DateUtil.getCurrentDate1());//YYYYMMDDhhmmss
//		fxMap.put("fxfee", selectByPrimaryKey.getPayAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString());
//		fxMap.put("fxbody", selectByPrimaryKey.getBankNumber());
//		fxMap.put("fxname", selectByPrimaryKey.getAccountName());
//		fxMap.put("fxaddress", selectByPrimaryKey.getBankName());
//		fxMap.put("fxzhihang", selectByPrimaryKey.getBranchBankName());
//		fxMap.put("fxsheng", selectByPrimaryKey.getAccountProvice());
//		fxMap.put("fxshi", selectByPrimaryKey.getAccountCity());
//		fxMap.put("fxlhh", selectByPrimaryKey.getBankNo());
//		fxbody.add(fxMap);
//		param.put("fxbody", JSONUtils.toJSONString(fxbody));
//
//		String fxsign = MD5.md5(fxid+fxaction+JSONUtils.toJSONString(fxbody)+key).toLowerCase();
//		param.put("fxsign", fxsign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(setUrl, null, param);
//
//		Map result = (Map) JSONUtils.parse(httpPost.getResultAsString());
//
//
//		Object status = result.get("fxstatus").toString();
//
//		Map eresult = Maps.newHashMap();
//		if ("1".equals(status)) // 1表示成功
//		{
//			eresult.put("status", "1");
//		} else { // 0表示异常
//			eresult.put("status", "0");
//		}
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> taskPayOrderNumber(String orderNumber, Date date) {
//
//		String oUrl = "http://58.82.213.4/Pay";
//
//		Map<String,String> param = Maps.newHashMap();
//
//		String fxid = "2019128";
//		param.put("fxid", fxid);
//
//		String fxaction = "repayquery";
//		param.put("fxaction", fxaction);
//
//		List<Map<String,String>> fxbody = Lists.newArrayList();
//		Map<String,String> fm = Maps.newHashMap();
//		fm.put("fxddh", orderNumber);
//		fxbody.add(fm);
//		param.put("fxbody", JSONUtils.toJSONString(fxbody));
//
//		String fxsign = MD5.md5(fxid+fxaction+JSONUtils.toJSONString(fxbody)+key);
//		param.put("fxsign", fxsign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(oUrl, null, param);
//		Map resultAsString = (Map)JSONUtils.parse(httpPost.getResultAsString());
//
//		String t1 = resultAsString.get("fxid").toString();
//		String t2 =  resultAsString.get("fxstatus").toString();
//		String t3 =  resultAsString.get("fxmsg").toString();
//		String t4 =  resultAsString.get("fxbody").toString();
//		String t5 =  resultAsString.get("fxsign").toString();
//		//状态+商务号+订单信息域（json格式字符串）+商户秘钥)】
//		String signreturn = MD5.md5(t2+t1+t4+key).toLowerCase();
//
//		if(t5.equals(signreturn))
//		{
//			List<Map> resultAsString1 = (List<Map>)JSONUtils.parse(t4);
//			String s1 = resultAsString1.get(0).get("fxstatus").toString();
//			// 【-1订单不存在】【0正常申请】【1已打款】【2冻结】【3已取消】
//			if("1".equals(s1))
//			{
//				Map result = Maps.newHashMap();
//				result.put("status","1");
//				return result;
//			}
//
//
//		}
//
//
//		return null;
//	}
//
//}
