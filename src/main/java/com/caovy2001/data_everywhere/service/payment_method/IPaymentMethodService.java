package com.caovy2001.data_everywhere.service.payment_method;

import com.caovy2001.data_everywhere.entity.PaymentMethodEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface IPaymentMethodService extends IBaseService {
    PaymentMethodEntity findByCode(String code);
}
