//package com.polypay.platform.paychannel;
//
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.TreeMap;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.utils.DateUtils;
//import com.polypay.platform.utils.HttpClientUtil;
//import com.polypay.platform.utils.HttpRequestDetailVo;
//import com.polypay.platform.utils.MD5;
//
//public class GZPayChannel implements IPayChannel {
//
//	private final static Logger log = LoggerFactory.getLogger(GZPayChannel.class);
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//
//		String AuthorizationURL = "http://xpz.ye16t.cn/Pay_Index.html";
//
//		Map<String, String> para = Maps.newHashMap();
//
//		String merchantId = "190383943";
//
//		String keyValue = "vx9m3510s0xsf3wm2bz6owc2dq5wr3pd";
//
//		String Channelid = param.get("pay_channel").toString();
//
//		BigDecimal pay_amountt = new BigDecimal(param.get("pay_amount").toString()).setScale(2,
//				BigDecimal.ROUND_HALF_UP);
//		String Moneys = pay_amountt.toString(); // 金额
//
//
//		para.put("pay_bankcode", Channelid);
//
//		String pay_memberid = merchantId;// 商户id
//		para.put("pay_memberid", pay_memberid);
//
//		// 订单号 (订单号由自己系统提供)
//		Object order_number = param.get("we_order_number");
//		String sdorderno = order_number.toString();
//		String pay_orderid = sdorderno;// 20位订单号 时间戳+6位随机字符串组成
//		para.put("pay_orderid", pay_orderid);
//
//		String pay_applydate = DateUtils.getTimeStr();// yyyy-MM-dd HH:mm:ss
//		para.put("pay_applydate", pay_applydate);
//
//		String pay_notifyurl = "http://39.109.4.205/getway/gz/recharge/back";//  回调地址
//		para.put("pay_notifyurl", pay_notifyurl);
//
//		String pay_callbackurl = "http://39.109.4.205/static/call.asp";// 通知地址
//		para.put("pay_callbackurl", pay_callbackurl);
//
//		String pay_amount = Moneys;
//		para.put("pay_amount", pay_amount);
//
//		String pay_attach = "";
//		String pay_productname = "100元话费充值";
//		para.put("pay_productname", pay_productname);
//
//		String pay_productnum = "";
//		String pay_productdesc = "";
//		String pay_producturl = "";
//		String stringSignTemp = "pay_amount=" + pay_amount + "&pay_applydate=" + pay_applydate + "&pay_bankcode="
//				+ Channelid + "&pay_callbackurl=" + pay_callbackurl + "&pay_memberid=" + pay_memberid
//				+ "&pay_notifyurl=" + pay_notifyurl + "&pay_orderid=" + pay_orderid + "&key=" + keyValue + "";
//		String pay_md5sign = MD5.md5(stringSignTemp).toUpperCase();
//		para.put("pay_md5sign", pay_md5sign);
//
//		request.setAttribute("dataMap", para);
//		request.setAttribute("action", AuthorizationURL);
//		log.info("gz=>" + para);
//
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//
//
//		/**
//		 *
//		 * {"memberid":"190383943"},
//		 * {"orderid":"R20190517180937740158281"},
//		 * {"transaction_id":"20190517180937491004"},
//		 * {"amount":"10.0000"},
//		 * {"datetime":"20190517181053"},
//		 * {"returncode":"00"},
//		 * {"sign":"6A284E64374E4E102ABA418CAFB13B8A"},
//		 * {"attach":""}]
//
//		 */
//	    String memberid=request.getParameter("memberid");
//	    String orderid=request.getParameter("orderid");
//	    String amount=request.getParameter("amount");
//	    String datetime=request.getParameter("datetime");
//	    String returncode=request.getParameter("returncode");
//	    String transaction_id = request.getParameter("transaction_id");
//	    String attach=request.getParameter("attach");
//	    String sign=request.getParameter("sign");
//	    String keyValue="vx9m3510s0xsf3wm2bz6owc2dq5wr3pd";
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
//		// mchid
//		// out_trade_no
//		// money
//		// bankname
//		// subbranch
//		// accountname
//		// cardnumber
//		// province
//		// city
//		// extends
//		// pay_md5sign
//
//		TreeMap<String, String> par = Maps.newTreeMap();
//		String mchid = "190383943";
//		par.put("mchid", mchid);
//
//		String out_trade_no = selectByPrimaryKey.getOrderNumber();
//		par.put("out_trade_no", out_trade_no);
//
//		String money = selectByPrimaryKey.getPostalAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString();
//		par.put("money", money);
//
//		String bankname = selectByPrimaryKey.getBankName();
//		par.put("bankname", bankname);
//
//		String subbranch = selectByPrimaryKey.getBranchBankName();
//		par.put("subbranch", subbranch);
//
//		String accountname = selectByPrimaryKey.getAccountName();
//		par.put("accountname", accountname);
//
//		String cardnumber = selectByPrimaryKey.getMerchantBindBank();
//		par.put("cardnumber", cardnumber);
//
//		String province = selectByPrimaryKey.getAccountProvice();
//		par.put("province", province);
//
//		String city = selectByPrimaryKey.getAccountCity();
//		par.put("city", city);
//
////		String extends_s = "";
////		par.put("extends", extends_s);
//
//		String pay_md5sign = buildSign(par);
//		par.put("pay_md5sign", pay_md5sign);
//
//		String url = "http://xpz.ye16t.cn/Payment_Dfpay_add.html";
//
//		log.info("gz pay=>" + par);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, par);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//		String status = parse.get("status").toString();
//
//		Map eresult = Maps.newHashMap();
//		String refCode = null;
//		if ("success".equals(status)) // 0表示成功
//		{
//			eresult.put("status", "1");
//		} else { // -1表示异常
//			eresult.put("status", "0");
//		}
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
//		sb.append("key=vx9m3510s0xsf3wm2bz6owc2dq5wr3pd");
//		return MD5.md5(sb.toString()).toUpperCase();
//	}
//
//	@Override
//	public Map<String, Object> placeOrder(MerchantPlaceOrder selectByPrimaryKey) {
//
//		// mchid
//		// out_trade_no
//		// money
//		// bankname
//		// subbranch
//		// accountname
//		// cardnumber
//		// province
//		// city
//		// extends
//		// pay_md5sign
//
//		TreeMap<String, String> par = Maps.newTreeMap();
//		String mchid = "10007";
//		par.put("mchid", mchid);
//
//		String out_trade_no = selectByPrimaryKey.getOrderNumber();
//		par.put("out_trade_no", out_trade_no);
//
//		String money = selectByPrimaryKey.getPayAmount().multiply(new BigDecimal(100)).toString();
//		par.put("money", money);
//
//		String bankname = selectByPrimaryKey.getBankName();
//		par.put("bankname", bankname);
//
//		String subbranch = selectByPrimaryKey.getBranchBankName();
//		par.put("subbranch", subbranch);
//
//		String accountname = selectByPrimaryKey.getAccountName();
//		par.put("accountname", accountname);
//
//		String cardnumber = selectByPrimaryKey.getBankNumber();
//		par.put("cardnumber", cardnumber);
//
//		String province = selectByPrimaryKey.getAccountProvice();
//		par.put("province", province);
//
//		String city = selectByPrimaryKey.getAccountCity();
//		par.put("city", city);
////
////		String extends_s = "";
////		par.put("extends", extends_s);
//
//		String pay_md5sign = buildSign(par);
//		par.put("pay_md5sign", pay_md5sign);
//
//		String url = "http://xpz.ye16t.cn/Payment_Dfpay_add.html";
//
//		log.info("gz pay=>" + par);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, par);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//		String status = parse.get("status").toString();
//
//		Map eresult = Maps.newHashMap();
//		if ("success".equals(status)) // 0表示成功
//		{
//			eresult.put("status", "1");
//
//		} else { // -1表示异常
//			eresult.put("status", "0");
//		}
//
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> taskPayOrderNumber(String orderNumber, Date date) {
//		String url = "http://xpz.ye16t.cn/Payment_Dfpay_query.html";
//
//		Map param = Maps.newHashMap();
//		String pay_memberid = "190383943";
//		param.put("mchid", pay_memberid);
//
//		String out_trade_no = orderNumber;
//		param.put("out_trade_no", out_trade_no);
//
//		String pay_md5sign = MD5.md5("mchid="+pay_memberid+"&out_trade_no="+out_trade_no+"&key=vx9m3510s0xsf3wm2bz6owc2dq5wr3pd").toUpperCase();
//		param.put("pay_md5sign", pay_md5sign);
//
//		log.info("gz task pay=>" + param);
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(url, null, param);
//		String resultAsString = httpPost.getResultAsString();
//		Map parse = (Map) JSON.parse(resultAsString);
//
//		String status = parse.get("status").toString();
//
//		Map eresult = Maps.newHashMap();
//		String refCode = null;
//		if ("success".equals(status)) // 0表示成功
//		{
//			refCode = parse.get("refCode").toString();
//
//			/**
//			 *
//			 * 1 成功 2 失败 3 处理中 4 待处理 5 审核驳回 6 待审核 7 交易不存在 8 未知状态
//			 */
//			if ("2".equals(refCode) || "5".equals(refCode) || "7".equals(refCode) || "8".equals(refCode))// 失败
//			{
//				eresult.put("status", "0");
//			} else if("1".equals(refCode)){
//
//				eresult.put("status", "1");
//			}
//
//		}
//
//
//		return eresult;
//	}
//
//}
