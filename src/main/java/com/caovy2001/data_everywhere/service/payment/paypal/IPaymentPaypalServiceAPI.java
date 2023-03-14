package com.caovy2001.data_everywhere.service.payment.paypal;

import com.caovy2001.data_everywhere.command.payment.CommandPaypalAuthorizePayment;
import com.caovy2001.data_everywhere.command.payment.CommandPaypalExecutePayment;
import com.caovy2001.data_everywhere.model.payment.PaymentPaypalResponse;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface IPaymentPaypalServiceAPI extends IBaseService {

    PaymentPaypalResponse authorizePayment(CommandPaypalAuthorizePayment command) throws Exception;

    PaymentPaypalResponse executePayment(CommandPaypalExecutePayment command) throws Exception;
}
