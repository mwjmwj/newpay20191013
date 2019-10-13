package com.polypay.platform.vo;

import java.math.BigDecimal;

public class MerchantAllRechargeVO {
	
	private String proxyId;
	
	private String merchantId;
	
	private String createTime;
	
	private BigDecimal arrivalAmount;
	
	public String getProxyId() {
		return proxyId;
	}

	public void setProxyId(String proxyId) {
		this.proxyId = proxyId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	private BigDecimal rechargeAmount;
	
	private Long rechargeNumber;
	
	private BigDecimal balanceAmount;
	
	private BigDecimal serverAmount;
	
	private BigDecimal frezzAmount;
	
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getFrezzAmount() {
		return frezzAmount;
	}

	public void setFrezzAmount(BigDecimal frezzAmount) {
		this.frezzAmount = frezzAmount;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public Long getRechargeNumber() {
		return rechargeNumber;
	}

	public void setRechargeNumber(Long rechargeNumber) {
		this.rechargeNumber = rechargeNumber;
	}

	public BigDecimal getServerAmount() {
		return serverAmount;
	}

	public void setServerAmount(BigDecimal serverAmount) {
		this.serverAmount = serverAmount;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getArrivalAmount() {
		return arrivalAmount;
	}

	public void setArrivalAmount(BigDecimal arrivalAmount) {
		this.arrivalAmount = arrivalAmount;
	}
	
}
