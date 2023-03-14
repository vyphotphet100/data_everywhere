package com.caovy2001.data_everywhere.service.payment_method;

import com.caovy2001.data_everywhere.entity.PaymentMethodEntity;
import com.caovy2001.data_everywhere.repository.PaymentMethodRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodService extends BaseService implements IPaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public PaymentMethodEntity findByCode(String code) {
        return paymentMethodRepository.findByCode(code);
    }
}
