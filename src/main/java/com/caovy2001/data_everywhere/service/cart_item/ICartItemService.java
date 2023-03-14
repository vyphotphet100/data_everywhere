package com.caovy2001.data_everywhere.service.cart_item;

import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

import java.util.List;

public interface ICartItemService extends IBaseService {
    List<CartItemEntity> getList(CommandGetListCartItem command);

    List<CartItemEntity> updateMany(List<CartItemEntity> cartItemEntities);

    long countPurchasedByUserIdAndDatasetCollectionId(String userId, String datasetCollectionId);
}
