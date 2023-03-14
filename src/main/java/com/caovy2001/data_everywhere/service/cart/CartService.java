package com.caovy2001.data_everywhere.service.cart;

import com.caovy2001.data_everywhere.command.cart.CommandAddCart;
import com.caovy2001.data_everywhere.command.cart.CommandUpdateCart;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.CartEntity;
import com.caovy2001.data_everywhere.repository.CartRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CartService extends BaseService implements ICartService, ICartServiceAPI {
    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartEntity findByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            log.error("[findByUserId]: user_id_null");
            return null;
        }

        return cartRepository.findByUserId(userId);
    }

    @Override
    public CartEntity add(CommandAddCart command) {
        if (StringUtils.isBlank(command.getUserId()) ||
                CollectionUtils.isEmpty(command.getCartItemIds()) ||
                CollectionUtils.isEmpty(command.getDatasetCollectionIds())) {
            log.error("[add]: " + ExceptionConstant.missing_param);
            return null;
        }

        if (BooleanUtils.isTrue(command.isCheckExistByUserId())) {
            CartEntity existCart = cartRepository.findByUserId(command.getUserId());
            if (existCart != null) {
                log.error("[add]: user_cart_exist");
                return null;
            }
        }

        return cartRepository.insert(CartEntity.builder()
                .userId(command.getUserId())
                .datasetCollectionIds(command.getDatasetCollectionIds())
                .cartItemIds(command.getCartItemIds())
                .build());
    }

    @Override
    public CartEntity update(CommandUpdateCart command) {
        if (StringUtils.isBlank(command.getId()) ||
                CollectionUtils.isEmpty(command.getCartItemIds()) ||
                CollectionUtils.isEmpty(command.getDatasetCollectionIds())) {
            log.error("[add]: " + ExceptionConstant.missing_param);
            return null;
        }

        CartEntity cartEntity = cartRepository.findById(command.getId()).orElse(null);
        if (cartEntity == null) {
            log.error("[update]: cart_not_exist");
            return null;
        }

        cartEntity.setDatasetCollectionIds(command.getDatasetCollectionIds());
        cartEntity.setCartItemIds(command.getCartItemIds());
        return cartRepository.save(cartEntity);
    }
}

















