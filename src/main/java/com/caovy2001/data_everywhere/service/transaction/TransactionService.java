package com.caovy2001.data_everywhere.service.transaction;

import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.TransactionEntity;
import com.caovy2001.data_everywhere.repository.TransactionRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionService extends BaseService implements ITransactionService, ITransactionServiceAPI {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public TransactionEntity add(TransactionEntity transactionEntity) {
        if (StringUtils.isAnyBlank(transactionEntity.getUserId(), transactionEntity.getPaymentMethodId(), transactionEntity.getPaymentMethodId())) {
            log.error("[add]: " + ExceptionConstant.missing_param);
            return null;
        }

        return transactionRepository.insert(transactionEntity);
    }
}
