package com.polypay.platform.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.polypay.platform.dao.PaymentCodeMapper;

@Component
public class PaymentCodeTask {
	
	
	@Autowired
	private PaymentCodeMapper mapper;
	
	
	@Scheduled(cron = "0 0 0 */1 * ?")
	public void executor() {
		
		mapper.reverse();
	}
	
	

}
