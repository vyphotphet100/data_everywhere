package com.caovy2001.data_everywhere.service.cart;

import com.caovy2001.data_everywhere.command.cart.CommandAddCart;
import com.caovy2001.data_everywhere.command.cart.CommandUpdateCart;
import com.caovy2001.data_everywhere.entity.CartEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface ICartService extends IBaseService {
    CartEntity findByUserId(String userId);

    CartEntity add(CommandAddCart command);
    CartEntity update(CommandUpdateCart command);
}
