//package com.polypay.platform.paychannel;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.utils.HttpClientUtil;
//import com.polypay.platform.utils.HttpRequestDetailVo;
//import com.polypay.platform.utils.MD5;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.math.BigDecimal;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.TreeMap;
//
//public class MNPayChannel implements IPayChannel {
//
//	private final static Logger log = LoggerFactory.getLogger(MNPayChannel.class);
//
//	private final static String API = "X0YABSYE15O1I91Z2IZ2UJRPOOEH3MSKOBF0ZRQS6MFVJACXCZR1NEMPZZWDY72MGWUSHPTSTMARPQHULQCNIBMBMH1T4SPIBVNQM3SFQGALYDMBMAQSOZ9DFJMGKNDR";
//
//	private final static String MER_ID = "8";
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//		String AuthorizationURL = "http://pay.meinuopay/api/pay/create_order";
//
//		Map<String, String> para = Maps.newTreeMap();
//
//		BigDecimal pay_amountt = new BigDecimal(param.get("pay_amount").toString()).multiply(new BigDecimal(100));
//		String Moneys = pay_amountt.toString(); // 金额
//
//		// 订单号 (订单号由自己系统提供)
//		Object order_number = param.get("we_order_number");
//		String mchOrderNo = order_number.toString();
//
//		// 回调
//		String pay_notifyurl = "http://39.109.4.205/getway/mn/recharge/back";//  回调地址
//
//		String Channelid = param.get("pay_channel").toString();
//
//		para.put("mchId",MER_ID);
//		para.put("appId","");
//		para.put("mchOrderNo",mchOrderNo);
//		para.put("currency","cny");
//		para.put("amount",Moneys);
//		para.put("notifyUrl",pay_notifyurl);
//		para.put("subject","商品");
//		para.put("body","手机");
//		para.put("extra","");
//
//
//		StringBuffer aP = new StringBuffer();
//		para.forEach((k,v)-> aP.append(k).append("=").append(v).append("&"));
//		String sPas = aP.append("key").append("=").append(API).toString();
//
//		String sign = MD5.md5(sPas).toUpperCase();
//		para.put("sign",sign);
//
//		HttpRequestDetailVo httpRequestDetailVo = HttpClientUtil.httpPost(AuthorizationURL, null, para);
//
//		System.out.println(httpRequestDetailVo.getResultAsString());
//
//
//		request.setAttribute("dataMap", para);
//		request.setAttribute("action", AuthorizationURL);
//		log.info("mn=>" + para);
//
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//	    String memberid=request.getParameter("memberid");
//	    String orderid=request.getParameter("orderid");
//	    String amount=request.getParameter("amount");
//	    String datetime=request.getParameter("datetime");
//	    String returncode=request.getParameter("returncode");
//	    String transaction_id = request.getParameter("transaction_id");
//	    String attach=request.getParameter("attach");
//	    String sign=request.getParameter("sign");
//	    String keyValue="q7o148nvzpc49iqhc4gx9thewazkmee5";
//	    String SignTemp="amount="+amount+"&datetime="+datetime+"&memberid="+memberid+"&orderid="+orderid+"&returncode="+returncode+"&transaction_id="+transaction_id+"&key="+keyValue;
//	    String md5sign=MD5.md5(SignTemp).toUpperCase();//MD5加密
//
//	    Map<String, Object> result = Maps.newHashMap();
//	    if (sign.equals(md5sign)){
//	        if(returncode.equals("00")){
//	            //支付成功，写返回数据逻辑
//	        	result.put("status", "1");
//				result.put("total_fee", new BigDecimal(amount));
//				result.put("orderno", orderid);
//	        }else{
//	        	result.put("status", "-1");
//				result.put("orderno", orderid);
//	        }
//	    }else{
//	    	result.put("status", "-10");
//	    }
//
//	    return result;
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
//		String url = "http://pay.zhekou366/api/agentpay/apply";
//		String money = selectByPrimaryKey.getPostalAmount().subtract(selectByPrimaryKey.getServiceAmount()).multiply(new BigDecimal(100)).toString();
//
//		TreeMap<String, String> par = Maps.newTreeMap();
//
//		par.put("mchId",MER_ID);
//		par.put("mchOrderNo",selectByPrimaryKey.getOrderNumber());
//		par.put("amount",money);
//		par.put("accountName",selectByPrimaryKey.getAccountName());
//		par.put("accountNo",selectByPrimaryKey.getMerchantBindBank());
//		par.put("remark","代付"+money+"分");
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		par.put("reqTime",sdf.format(new Date()));
//
//		StringBuffer aP = new StringBuffer();
//		par.forEach((k,v)-> aP.append(k).append("=").append(v).append("&"));
//		String sPas = aP.append("key").append("=").append(API).toString();
//		String sign = MD5.md5(sPas).toUpperCase();
//		par.put("sign",sign);
//
//		log.info("mn pay=>" + par);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, par);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//
//		///状态:0-待处理,1-处理中,2-成功,3-失败
//		String retCode = parse.get("retCode").toString();
//		log.info("mn pay=>" + parse);
//		Map eresult = Maps.newHashMap();
//		if ("SUCCESS".equals(retCode)) //  success
//		{
//			eresult.put("status", "1");
//		} else { //  其他表示 fail
//			eresult.put("status", "0");
//		}
//		eresult.put("retMsg", parse.get("retMsg"));
//
//		return eresult;
//	}
//
//
//	private String buildSign(TreeMap<String, String> data) {
//
//		StringBuffer sb = new StringBuffer();
//		for (Entry<String, String> entry : data.entrySet()) {
//			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//		}
//
//		sb.append("key=q7o148nvzpc49iqhc4gx9thewazkmee5");
//		return MD5.md5(sb.toString()).toUpperCase();
//	}
//
//	@Override
//	public Map<String, Object> placeOrder(MerchantPlaceOrder selectByPrimaryKey) {
//
//		String url = "http://pay.zhekou366/api/agentpay/apply";
//		String money = selectByPrimaryKey.getPayAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString();
//
//		TreeMap<String, String> par = Maps.newTreeMap();
//
//		par.put("mchId",MER_ID);
//		par.put("mchOrderNo",selectByPrimaryKey.getOrderNumber());
//		par.put("amount",money);
//		par.put("accountName",selectByPrimaryKey.getAccountName());
//		par.put("accountNo",selectByPrimaryKey.getBankNumber());
//		par.put("remark","代付"+money+"元");
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		par.put("reqTime",sdf.format(new Date()));
//
//		StringBuffer aP = new StringBuffer();
//		par.forEach((k,v)-> aP.append(k).append("=").append(v).append("&"));
//		String sPas = aP.append("key").append("=").append(API).toString();
//		String sign = MD5.md5(sPas).toUpperCase();
//		par.put("sign",sign);
//
//		log.info("mn pay=>" + par);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, par);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//
//		///状态:0-待处理,1-处理中,2-成功,3-失败
//		String retCode = parse.get("retCode").toString();
//		log.info("mn pay=>" + parse);
//		Map eresult = Maps.newHashMap();
//		if ("SUCCESS".equals(retCode)) //  success
//		{
//			eresult.put("status", "1");
//		} else { //  其他表示 fail
//			eresult.put("status", "0");
//		}
//		eresult.put("retMsg", parse.get("retMsg"));
//
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> taskPayOrderNumber(String orderNumber, Date date) {
//		Map para= Maps.newTreeMap();
//
//
//		String url = "http://pay.zhekou366/api/agentpay/query_order";
//		para.put("mchId",MER_ID);
//		para.put("mchOrderNo",orderNumber);
//
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		para.put("reqTime",sdf.format(date));
//
//		StringBuffer aP = new StringBuffer();
//		para.forEach((k,v)-> aP.append(k).append("=").append(v).append("&"));
//		String sPas = aP.append("key").append("=").append(API).toString();
//		String sign = MD5.md5(sPas).toUpperCase();
//		para.put("sign",sign);
//
//
//		log.info("mn task pay=>" + para);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, para);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//		String retCode = parse.get("retCode").toString();
//
//		Map eresult = Maps.newHashMap();
//		String status = null;
//		if ("SUCCESS".equals(retCode)) // 0表示成功
//		{
//			status = parse.get("status").toString();
//
//			/**
//			 *
//			 *状态:0-待处理,1-处理中,2-成功,3-失败
//			 */
//			if ("3".equals(status))// 失败
//			{
//				eresult.put("status", "0");
//			} else if("2".equals(status)){
//
//				eresult.put("status", "1");
//			}
//
//		}
//
//		return eresult;
//	}
//
//}
