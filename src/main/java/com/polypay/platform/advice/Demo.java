package com.polypay.platform.advice;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpUtils;

import com.google.common.collect.Maps;
import com.polypay.platform.utils.HttpClientUtil;
import com.polypay.platform.utils.HttpRequestDetailVo;
import com.polypay.platform.utils.MD5;

public class Demo {
	
	@SuppressWarnings("unchecked")
		public static void main(String[] args) {

			String url = "http://localhost:8080/open/hfbapi/recharge?";
			String str = "merchant_id=10000&order_number=88890&" +
					"pay_amount=100&time=5522541&pay_channel=13" +
					"&notify_url=http://www.xx.com&api_key=10f4a5534852475da415c1eec12bbfbc";

			String sign = MD5.md5(str);

			System.out.println(url+str+"&sign="+sign);
		}

}
