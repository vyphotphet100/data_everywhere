package com.caovy2001.data_everywhere.service.cart_item;

import com.caovy2001.data_everywhere.command.cart_item.CommandAPIAddCartItem;
import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface ICartItemServiceAPI extends IBaseService {
    CartItemEntity addCartItem(CommandAPIAddCartItem command) throws Exception;

    Paginated<CartItemEntity> getPaginatedList(CommandGetListCartItem command) throws Exception;
}
