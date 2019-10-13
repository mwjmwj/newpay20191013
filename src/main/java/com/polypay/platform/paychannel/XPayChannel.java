//package com.polypay.platform.paychannel;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.Map.Entry;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.druid.support.json.JSONUtils;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.polypay.platform.bean.MerchantPlaceOrder;
//import com.polypay.platform.bean.MerchantSettleOrder;
//import com.polypay.platform.utils.DateUtil;
//import com.polypay.platform.utils.HttpClientUtil;
//import com.polypay.platform.utils.HttpRequestDetailVo;
//import com.polypay.platform.utils.MD5;
//
//public class XPayChannel implements IPayChannel {
//	private final static Logger log = LoggerFactory.getLogger(KFChannel.class);
//
//	private static String key = "860EDAEF8759239B227ZXCV298F49497";
//
//	private static String URL = "http://online.toxpay.com/xpay/xpayapi";
//
//	@Override
//	public void sendRedirect(Map<String, Object> param, HttpServletResponse response, HttpServletRequest request) {
//
//		TreeMap rparam = Maps.newTreeMap();
//
//		/**
//		 *
//		 * reqCmd 是 merchNo 是 charset 是 signType reqIp 是 payType 是 tradeNo 是 currency
//		 * amount 是 userId 是 notifyUrl returnUrl goodsName goodsDesc
//		 *
//		 */
//
//		String reqCmd = "req.trade.order";
//		rparam.put("reqCmd", reqCmd);
//
//		String merchNo = "999941000005";
//		rparam.put("merchNo", merchNo);
//
//		String charset = "UTF-8";
//		rparam.put("charset", charset);
//
//		String signType = "MD5";
//		rparam.put("signType", signType);
//
//		String reqIp = "127.0.0.1";
//		rparam.put("reqIp", reqIp);
//
//		String payType = param.get("pay_channel").toString();
//		rparam.put("payType", payType);
//
//		Object order_number = param.get("we_order_number");
//		String tradeNo = order_number.toString(); // 订单号
//		rparam.put("tradeNo", tradeNo);
//
//		String currency = "CNY";
//		rparam.put("currency", currency);
//
//		Object pay_amount = param.get("pay_amount");
//		BigDecimal total_amount = new BigDecimal(pay_amount.toString()).setScale(0, BigDecimal.ROUND_HALF_UP);
//		String amount = total_amount.toString();
//		rparam.put("amount", amount);
//
//		String userId = "456";
//		rparam.put("userId", userId);
//
//		String notifyUrl = param.get("call_back").toString();
//		rparam.put("notifyUrl", notifyUrl);
//
//		String returnUrl = "http://www.xx.com";
//		rparam.put("returnUrl", returnUrl);
//
//		String goodsName = "一条裤子";
//		rparam.put("goodsName", goodsName);
//
//		String goodsDesc = "灰色牛仔裤";
//		rparam.put("goodsDesc", goodsDesc);
//
//		String sign = buildSign(rparam).toLowerCase();
//		rparam.put("sign", sign);
//
//		String payUrl = URL;
//
//
//		request.setAttribute("dataMap", rparam);
//		request.setAttribute("action", payUrl);
//
////		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(payUrl, null, rparam);
////		Map result = (Map) JSONUtils.parse(httpPost.getResultAsString());
////		log.info("pay - > " + result);
////
////		Object bankUrl = result.get("bankUrl");
////
////		if (null != bankUrl) {
////			try {
////				response.sendRedirect(bankUrl.toString());
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
////		}
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
//		sb.append("key=" + key);
//		log.info(sb.toString());
//		return MD5.md5(sb.toString()).toLowerCase();
//	}
//
//	@Override
//	public Map<String, Object> checkOrder(HttpServletRequest request) {
//
//		/**
//		 *
//		 * code 是 是 32 业务响应码 10000 msg 是 是 350 业务响应描述 支付成功 merchNo 是 是 32 商户号
//		 * 222241000001 amount 是 是 32 金额(单位元) 18.66 tradeNo 是 是 32 商户订单号 1554717081228
//		 * orderNo 是 是 32 平台订单号 21554717081228 status 是 是 2 订单状态 4 sign 是 否 350 请求参数签名
//		 * [签名规则]
//		 *
//		 *
//		 */
//		TreeMap reparam = Maps.newTreeMap();
//
//		String code = request.getParameter("code");
//		reparam.put("code", code);
//
//		String msg = request.getParameter("msg");
//		reparam.put("msg", msg);
//
//		String merchNo = request.getParameter("merchNo");
//		reparam.put("merchNo", merchNo);
//
//		String amount = request.getParameter("amount");
//		reparam.put("amount", amount);
//
//		String tradeNo = request.getParameter("tradeNo");
//		reparam.put("tradeNo", tradeNo);
//
//		String orderNo = request.getParameter("orderNo");
//		reparam.put("orderNo", orderNo);
//
//		String status = request.getParameter("status");
//		reparam.put("status", status);
//
//		String signr = buildSign(reparam).toLowerCase();
//
//		String sign = request.getParameter("sign");
//
//		Map result = Maps.newHashMap();
//		if (!sign.equals(signr)) {
//			result.put("status", "-10");
//			return result;
//		}
//
//		if ("4".equals(status)) // 成功
//		{
//			result.put("status", "1");
//			result.put("total_fee", new BigDecimal(amount));
//			result.put("orderno", tradeNo);
//		} else // 失败
//		{
//			result.put("status", "-1");
//			result.put("orderno", tradeNo);
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
//		/**
//		 *
//		 * reqCmd 是 是 64 请求接口名称 req.transfer.order merchNo 是 是 32 请求商户号 222241000001
//		 * charset 是 是 10 请求编码格式 UTF-8 signType 是 是 10 签名算法类型 MD5 reqIp 是 是 32 请求公网地址
//		 * 127.0.0.1 tradeNo 是 是 32 商户订单号 1554717081228 amount 是 是 12 订单金额(元) 18.66
//		 * idCardNo 是 是 32 身份证号 111111111111111111 accountName 是 是 12 账户名称 孙中山 bankCard
//		 * 是 是 32 银行卡号 621111111111111111 bankName 是 是 32 银行名称 中国银行 bankSubName 是 是 32
//		 * 开户行名称 北京三环支行 province 是 是 32 省份名称 北京市 city 是 是 32 城市名称 北京市 bankLinked 是 是 32
//		 * 联行号 425584018689 mobile 是 是 32 手机号码 12111111111 sign 是 否 350 请求加密签名 [签名规则]
//		 */
//		TreeMap reqParam = Maps.newTreeMap();
//		String reqCmd = "req.transfer.order";
//		reqParam.put("reqCmd", reqCmd);
//
//		String merchNo = "999941000005";
//		reqParam.put("merchNo", merchNo);
//
//		String charset = "UTF-8";
//		reqParam.put("charset", charset);
//
//		String signType = "MD5";
//		reqParam.put("signType", signType);
//
//		String reqIp = "127.0.0.1";
//		reqParam.put("reqIp", reqIp);
//
//		String tradeNo = selectByPrimaryKey.getOrderNumber();
//		reqParam.put("tradeNo", tradeNo);
//
//		String amount = selectByPrimaryKey.getPostalAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString();
//		reqParam.put("amount", amount);
//
//		String idCardNo = "111111111111111111";
//		reqParam.put("idCardNo", idCardNo);
//
//		String accountName = selectByPrimaryKey.getAccountName();
//		reqParam.put("accountName", accountName);
//
//		String bankCard = selectByPrimaryKey.getMerchantBindBank();
//		reqParam.put("bankCard", bankCard);
//
//		String bankName = selectByPrimaryKey.getBankName();
//		reqParam.put("bankName", bankName);
//
//		String bankSubName = selectByPrimaryKey.getBranchBankName();
//		reqParam.put("bankSubName", bankSubName);
//
//		String province = selectByPrimaryKey.getAccountProvice();
//		reqParam.put("province", province);
//
//		String city = selectByPrimaryKey.getAccountCity();
//		reqParam.put("city", city);
//
//		String bankLinked = selectByPrimaryKey.getBankNo();
//		reqParam.put("tradeNo", tradeNo);
//
//		String mobile = "12111111111";
//		reqParam.put("mobile", mobile);
//
//		String sign = buildSign(reqParam).toLowerCase();
//		reqParam.put("sign", sign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(URL, null, reqParam);
//
//		Map result = (Map) JSONUtils.parse(httpPost.getResultAsString());
//
//		Object status = result.get("status").toString();
//
//		Map eresult = Maps.newHashMap();
//		if ("2".equals(status) || "1".equals(status)) // 1 / 2 表示成功
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
//		/**
//		 *
//		 * reqCmd 是 是 64 请求接口名称 req.transfer.order merchNo 是 是 32 请求商户号 222241000001
//		 * charset 是 是 10 请求编码格式 UTF-8 signType 是 是 10 签名算法类型 MD5 reqIp 是 是 32 请求公网地址
//		 * 127.0.0.1 tradeNo 是 是 32 商户订单号 1554717081228 amount 是 是 12 订单金额(元) 18.66
//		 * idCardNo 是 是 32 身份证号 111111111111111111 accountName 是 是 12 账户名称 孙中山 bankCard
//		 * 是 是 32 银行卡号 621111111111111111 bankName 是 是 32 银行名称 中国银行 bankSubName 是 是 32
//		 * 开户行名称 北京三环支行 province 是 是 32 省份名称 北京市 city 是 是 32 城市名称 北京市 bankLinked 是 是 32
//		 * 联行号 425584018689 mobile 是 是 32 手机号码 12111111111 sign 是 否 350 请求加密签名 [签名规则]
//		 */
//		TreeMap reqParam = Maps.newTreeMap();
//		String reqCmd = "req.transfer.order";
//		reqParam.put("reqCmd", reqCmd);
//
//		String merchNo = "999941000005";
//		reqParam.put("merchNo", merchNo);
//
//		String charset = "UTF-8";
//		reqParam.put("charset", charset);
//
//		String signType = "MD5";
//		reqParam.put("signType", signType);
//
//		String reqIp = "127.0.0.1";
//		reqParam.put("reqIp", reqIp);
//
//		String tradeNo = selectByPrimaryKey.getOrderNumber();
//		reqParam.put("tradeNo", tradeNo);
//
//		String amount = selectByPrimaryKey.getPayAmount().subtract(selectByPrimaryKey.getServiceAmount()).toString();
//		reqParam.put("amount", amount);
//
//		String idCardNo = "111111111111111111";
//		reqParam.put("idCardNo", idCardNo);
//
//		String accountName = selectByPrimaryKey.getAccountName();
//		reqParam.put("accountName", accountName);
//
//		String bankCard = selectByPrimaryKey.getBankNumber();
//		reqParam.put("bankCard", bankCard);
//
//		String bankName = selectByPrimaryKey.getBankName();
//		reqParam.put("bankName", bankName);
//
//		String bankSubName = selectByPrimaryKey.getBranchBankName();
//		reqParam.put("bankSubName", bankSubName);
//
//		String province = selectByPrimaryKey.getAccountProvice();
//		reqParam.put("province", province);
//
//		String city = selectByPrimaryKey.getAccountCity();
//		reqParam.put("city", city);
//
//		String bankLinked = selectByPrimaryKey.getBankNo();
//		reqParam.put("tradeNo", tradeNo);
//
//		String mobile = "12111111111";
//		reqParam.put("mobile", mobile);
//
//		String sign = buildSign(reqParam).toLowerCase();
//		reqParam.put("sign", sign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(URL, null, reqParam);
//
//		Map result = (Map) JSONUtils.parse(httpPost.getResultAsString());
//
//		Object status = result.get("status").toString();
//
//		Map eresult = Maps.newHashMap();
//		if ("2".equals(status) || "1".equals(status)) // 1 / 2 表示成功
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
//		/**
//		 *
//		 *
//		 * reqCmd 是 是 64 请求接口名称 req.query.trade:交易订单查询 req.query.transfer:代付订单查询 merchNo
//		 * 是 是 32 请求商户号 222241000001 charset 是 是 10 请求编码格式 UTF-8 signType 是 是 10 签名算法类型
//		 * MD5 reqIp 是 是 32 请求公网地址 127.0.0.1 tradeNo 是 是 32 调用方订单号 1554717081228 remark
//		 * 否 是 256 返回透传信息 明细查询 sign 是 否 350 请求参数签名 【[签名规则]】
//		 */
//		TreeMap rePara = Maps.newTreeMap();
//
//		String reqCmd = "req.query.transfer";
//		rePara.put("reqCmd", reqCmd);
//
//		String merchNo = "999941000005";
//		rePara.put("merchNo", merchNo);
//
//		String charset = "UTF-8";
//		rePara.put("charset", charset);
//
//		String signType = "MD5";
//		rePara.put("signType", signType);
//
//		String reqIp = "127.0.0.1";
//		rePara.put("reqIp", reqIp);
//
//		String tradeNo = orderNumber;
//		rePara.put("tradeNo", tradeNo);
//
//		String sign = buildSign(rePara).toLowerCase();
//		rePara.put("sign", sign);
//
//		HttpRequestDetailVo httpPost = HttpClientUtil.httpPost(URL, null, rePara);
//		Map resultAsString = (Map) JSONUtils.parse(httpPost.getResultAsString());
//
//		/**
//		 *
//		 *
//		 * code 是 是 32 业务响应码 10000 msg 是 是 350 业务响应描述 下单成功 merchNo 是 是 32 商户号
//		 * 222241000001 amount 是 是 32 金额(单位元) 18.66 tradeNo 是 是 32 商户订单号 1554717081228
//		 * orderNo 是 是 32 平台订单号 21554717081228 status 是 是 2 订单状态 4 remark 否 是 256 透传信息
//		 * 明细查询 sign 是 否 350 请求参数签名 [签名规则]
//		 */
//
//		TreeMap rePara1 = Maps.newTreeMap();
//
//		String code1 = resultAsString.get("code").toString();
//		rePara1.put("code", code1);
//
//		String msg1 = resultAsString.get("msg").toString();
//		rePara1.put("msg", msg1);
//
//		String merchNo1 = resultAsString.get("merchNo").toString();
//		rePara1.put("merchNo", merchNo1);
//
//		String amount1 = resultAsString.get("amount").toString();
//		rePara1.put("amount", amount1);
//
//		String tradeNo1 = resultAsString.get("tradeNo").toString();
//		rePara1.put("tradeNo", tradeNo1);
//
//		String orderNo1 = resultAsString.get("orderNo").toString();
//		rePara1.put("orderNo", orderNo1);
//
//		String status1 = resultAsString.get("status").toString();
//		rePara1.put("status", status1);
//
//		String sign2 = buildSign(rePara1).toLowerCase();
//
//		String sign1 = resultAsString.get("sign").toString();
//
//		log.info(sign1);
////		if (!sign1.equals(sign2)) {
////			return null;
////		}
//
//		// 【-1订单不存在】【0正常申请】【1已打款】【2冻结】【3已取消】
//
//		/**
//		 *
//		 * 1 代付成功 代付成功 2 代付中 出款中 3 代付失败 出款失败 6 状态异常 出款异常 9 代付退汇
//		 */
//		if ("1".equals(status1)) {
//			Map result = Maps.newHashMap();
//			result.put("status", "1");
//			return result;
//		}
//
//		return null;
//	}
//}
