//package com.polypay.platform.paychannel;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.net.URLEncoder;
//import java.util.Date;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.TreeMap;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.baomidou.mybatisplus.toolkit.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.pay.EFBResponse;
//import com.polypay.platform.utils.HttpClientUtil;
//import com.polypay.platform.utils.HttpRequestDetailVo;
//import com.polypay.platform.utils.MD5;
//
///**
// * E 15 付宝
// *
// * @author Administrator
// *
// */
//public class EFiveFBChannel implements IPayChannel {
//
//	private final static Logger log = LoggerFactory.getLogger(EFiveFBChannel.class);
//
//	final static String URL = "http://pay.yaoyekj.com/server/api/doAction";
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//
//		TreeMap<String, String> data = new TreeMap<>();
//		data.put("pay_code", "GATEWAY_PAY_PC");
//		data.put("merchant_no", "746668721");
//
//		// 交易流水
//		Object order_number = param.get("we_order_number");
//		String tranSerialNum = order_number.toString();
//		data.put("order_no", tranSerialNum);
//
//		// 以分为单位计算
//		Object pay_amount = param.get("pay_amount");
//		Integer total_amount = new BigDecimal(pay_amount.toString()).setScale(2, BigDecimal.ROUND_HALF_UP)
//				.multiply(new BigDecimal(100)).intValue();
//		String amount = total_amount.toString();
//		data.put("amount", amount);
//
//		data.put("notify_url", "http://www.ysfpolypay.cn/getway/ezf5/recharge/back");
//
//		data.put("product_name", "testing");
//
//		/**
//		 *
//		 * amount=100&merchant_no=1800379226&notify_url=http://43.251.103.22:9105/notify/doAction&
//		 * order_no=2018112616254825941147&pay_code=WEIXINPAY_UNIFIEDORDER_SCAN&product_name=testing&key=vahr27sIZ233333ygF
//		 *
//		 */
//
//		String sign = buildSign(data);
//
//		data.put("sign", sign);
//
//		data.put("service", "unifiedorder");
//
//		log.debug("data= " + data);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(URL, null, data);
//		EFBResponse result = JSON.parseObject(httpPost.getResultAsString(), EFBResponse.class);
//
//		String code = result.getCode();
//
//		if ("0".equals(code)) {
//			try {
//				// 支付
//
//				String eurl = result.getData().get("url");
//
//				System.out.println(eurl);
//				String[] split = eurl.split("product_name=");
//
//				String str1 = split[1];
//
//				String[] split2 = str1.split("&notify_url");
//
//				String str2 = split2[0];
//
//				String endUrl = split[0] + "product_name=" + URLEncoder.encode(str2)+"&notify_url"+split2[1];
//
//				response.sendRedirect(endUrl);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		/**
//		 * "data": { "forwarding": "true", "merchant_no": "1234567890", "order_no":
//		 * "2018103101243372060328", "pay_code": "UNIONPAY_PAY_PC", "plat_orderid":
//		 * "PO2018103101243569952525", "sign": "608144fc517a15a577a36e649887099b",
//		 * "url":
//		 * "http://193.112.142.128:8981/pay/doAction?service=unifiedorder&pay_code=UNIONPAY_PAY_PC&merchants=test_channel_local_leon&merchant_no=1234567890&order_no=2018103101243372060328&amount=1&product_name=testing&notify_url=http://193.112.142.128:9305/notify/doAction&client_ip=127.0.0.1&bank_name=122&sign=d9d7908225091324fab7d4b425d54e71"
//		 * }
//		 */
//
//	}
//
//	private String buildSign(TreeMap<String, String> data) {
//
//		StringBuffer sb = new StringBuffer();
//		for (Entry<String, String> entry : data.entrySet()) {
//			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//		}
//
//		sb.append("key=FKnGLTL6752kSxKggdpI");
//		log.info(sb.toString());
//		return MD5.md5(sb.toString()).toLowerCase();
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//
////		service	是	string	接口标识（不参与md5签名），固定值：PAY_NOTIFY
////		pay_code	是	string	支付类型代码
////		merchant_no	是	string	商户号，支付平台系统方提供
////		amount	是	string	订单金额，单位分，整数类型
////		order_no	是	string	商户订单号，64位随机字符，唯一值
////		pay_state	是	Int	支付状态，0-待支付，1-支付成功，2-支付失败，3-已关闭，4-已撤销
////		sign	是	string	签名字符串
//
//		TreeMap<String, String> signMap = Maps.newTreeMap();
//
//		signMap.put("amount", getParameter(request, "amount"));
//		signMap.put("merchant_no", getParameter(request, "merchant_no"));
//		signMap.put("order_no", getParameter(request, "order_no"));
//		signMap.put("pay_code", getParameter(request, "pay_code"));
//		signMap.put("pay_state", getParameter(request, "pay_state"));
//
//		String sign = buildSign(signMap);
//
//		String notiSign = getParameter(request, "sign");
//
//		Map<String, Object> result = Maps.newHashMap();
//
//		log.debug("notify=" + signMap);
//		if (!sign.equals(notiSign)) {
//			result.put("status", "-10");
//			return result;
//		}
//
//		String paystatus = getParameter(request, "pay_state");
//		String amount = getParameter(request, "amount");
//		String order_no = getParameter(request, "order_no");
//
//		if ("1".equals(paystatus)) // 成功
//		{
//			result.put("status", "1");
//			result.put("total_fee", new BigDecimal(amount).divide(new BigDecimal(100)));
//			result.put("orderno", order_no);
//		} else // 失败
//		{
//			result.put("status", "-1");
//			result.put("orderno", order_no);
//		}
//
//		// amount=1&merchant_no=test&order_no=Test001&pay_code=1&pay_state=1&key=1
//
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
//	@SuppressWarnings("rawtypes")
//	@Override
//	public Map<String, Object> settleOrder(MerchantSettleOrder settleOrder) {
//
//		/**
//		 *
//		 * amount=0.01&bank_account_type=1&bank_card_name=李超人
//		 * &bank_card_no=6212263602013190000&bank_card_type=1&bank_name=中国工商银行
//		 * &city=广州市&merchant_no=73992167&notify_url=http://193.112.142.128:9305/notify/doAction
//		 * &order_no=2019010618333227052408&province=广东省&key=xEiCxvrKbSau0Wsdr
//		 */
//
//		TreeMap<String, String> signMap = Maps.newTreeMap();
//
//		signMap.put("amount", settleOrder.getPostalAmount().subtract(settleOrder.getServiceAmount()).setScale(2, BigDecimal.ROUND_DOWN).toString());
//
//		signMap.put("bank_account_type", "1");
//
//		signMap.put("bank_card_name", settleOrder.getAccountName());
//
//		signMap.put("bank_card_no", settleOrder.getMerchantBindBank());
//
//		signMap.put("bank_card_type", "1");
//
//		signMap.put("bank_name", settleOrder.getBankName());
//
//		signMap.put("city", settleOrder.getAccountCity());
//
//		signMap.put("merchant_no", "74666872");
//
//		signMap.put("notify_url", "http://www.ysfpolypay.cn/getway/ezf/callback");
//
//		signMap.put("order_no", settleOrder.getOrderNumber());
//
//		signMap.put("province", settleOrder.getAccountProvice());
//
//		signMap.put("pay_password", "FKnGLTL6752kSxKggdpI");
//
//		String buildSign = buildSign(signMap);
//
//		signMap.put("sign", buildSign);
//
//		signMap.put("service", "singlepay");
//
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost("http://pay.yaoyekj.com/payforanothers/api/doAction", null, signMap);
//
//		log.info("pay==>" + httpPost.getResultAsString());
//		EFBResponse result = JSON.parseObject(httpPost.getResultAsString(), EFBResponse.class);
//
//		String code = result.getCode();
//		Map eresult = Maps.newHashMap();
//		if ("0".equals(code)) // 0表示成功
//		{
//			eresult.put("status", "1");
//		} else { // -1表示异常
//			eresult.put("status", "0");
//		}
//		/**
//		 * { "msg": "", "code": 0, "data": { "merchant_no": "73992167", "order_no":
//		 * "2019010618333227052408", "service": "singlepay", "sign":
//		 * "8eea32a2ff87eb401efbb7b29664b4fa" } }
//		 */
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> placeOrder(MerchantPlaceOrder placeOrder) {
//
//		/**
//		 *
//		 * amount=0.01&bank_account_type=1&bank_card_name=李超人
//		 * &bank_card_no=6212263602013190000&bank_card_type=1&bank_name=中国工商银行
//		 * &city=广州市&merchant_no=73992167&notify_url=http://193.112.142.128:9305/notify/doAction
//		 * &order_no=2019010618333227052408&province=广东省&key=xEiCxvrKbSau0Wsdr
//		 */
//
//		TreeMap<String, String> signMap = Maps.newTreeMap();
//
//		signMap.put("amount", placeOrder.getPayAmount().subtract(placeOrder.getServiceAmount()).setScale(2, BigDecimal.ROUND_DOWN).toString());
//
//		signMap.put("bank_account_type", "1");
//
//		signMap.put("bank_card_name", placeOrder.getAccountName());
//
//		signMap.put("bank_card_no", placeOrder.getBankNumber());
//
//		signMap.put("bank_card_type", "1");
//
//		signMap.put("bank_name", placeOrder.getBankName());
//
//		signMap.put("city", placeOrder.getAccountCity());
//
//		signMap.put("merchant_no", "74666872");
//
//		signMap.put("notify_url", "http://www.ysfpolypay.cn/getway/ezf/callback");
//
//		signMap.put("order_no", placeOrder.getOrderNumber());
//
//		signMap.put("province", placeOrder.getAccountProvice());
//
//		String buildSign = buildSign(signMap);
//
//		signMap.put("sign", buildSign);
//
//		signMap.put("service", "singlepay");
//
//		signMap.put("pay_password", "FKnGLTL6752kSxKggdpI");
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(URL, null, signMap);
//
//		EFBResponse result = JSON.parseObject(httpPost.getResultAsString(), EFBResponse.class);
//
//		String code = result.getCode();
//
//		Map eresult = Maps.newHashMap();
//		if ("0".equals(code)) // 0表示成功
//		{
//			eresult.put("status", "1");
//		} else { // -1表示异常
//			eresult.put("status", "0");
//		}
//		/**
//		 * { "msg": "", "code": 0, "data": { "merchant_no": "73992167", "order_no":
//		 * "2019010618333227052408", "service": "singlepay", "sign":
//		 * "8eea32a2ff87eb401efbb7b29664b4fa" } }
//		 */
//		return eresult;
//	}
//
//	@Override
//	public Map<String, Object> taskPayOrderNumber(String orderNumber, Date date) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
