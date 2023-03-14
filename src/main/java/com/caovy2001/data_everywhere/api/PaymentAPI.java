package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.command.payment.CommandPaypalAuthorizePayment;
import com.caovy2001.data_everywhere.command.payment.CommandPaypalExecutePayment;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.caovy2001.data_everywhere.service.payment.paypal.IPaymentPaypalServiceAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentAPI extends BaseAPI {
    @Autowired
    private IPaymentPaypalServiceAPI paymentServicePaypalAPI;

    @PostMapping("/paypal/authorize_payment")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel authorizePayment(@RequestBody CommandPaypalAuthorizePayment command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            command.setUserId(userEntity.getId());

            return ResponseModel.builder()
                    .payload(paymentServicePaypalAPI.authorizePayment(command))
                    .status(ResponseModel.Status.builder().build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.builder()
                    .status(ResponseModel.Status.builder()
                            .httpStatus(HttpStatus.EXPECTATION_FAILED)
                            .exceptionCode(StringUtils.isNotBlank(e.getMessage())? e.getMessage(): ExceptionConstant.error_occur)
                            .build())
                    .build();
        }
    }

    @PostMapping("/paypal/execute_payment")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel executePayment(@RequestBody CommandPaypalExecutePayment command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            command.setUserId(userEntity.getId());

            return ResponseModel.builder()
                    .payload(paymentServicePaypalAPI.executePayment(command))
                    .status(ResponseModel.Status.builder().build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.builder()
                    .status(ResponseModel.Status.builder()
                            .httpStatus(HttpStatus.EXPECTATION_FAILED)
                            .exceptionCode(StringUtils.isNotBlank(e.getMessage())? e.getMessage(): ExceptionConstant.error_occur)
                            .build())
                    .build();
        }
    }
}
