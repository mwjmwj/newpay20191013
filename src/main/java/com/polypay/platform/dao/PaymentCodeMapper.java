package com.polypay.platform.dao;

import java.util.List;
import java.util.Map;

import com.polypay.platform.bean.PaymentCode;

public interface PaymentCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PaymentCode record);

    int insertSelective(PaymentCode record);

    PaymentCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PaymentCode record);

    int updateByPrimaryKey(PaymentCode record);

	List<PaymentCode> listPay(Map param);

	void reverse();

	List<PaymentCode> listByIds(List<String> payIds);
}