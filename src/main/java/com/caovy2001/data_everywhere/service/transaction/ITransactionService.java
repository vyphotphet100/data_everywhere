package com.caovy2001.data_everywhere.service.transaction;

import com.caovy2001.data_everywhere.entity.TransactionEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface ITransactionService extends IBaseService {
    TransactionEntity add(TransactionEntity transactionEntity);
}
