package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.command.cart_item.CommandAPIAddCartItem;
import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.command.cart_item.CommandRemoveCartItem;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.caovy2001.data_everywhere.service.cart_item.ICartItemServiceAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart_item")
public class CartItemAPI extends BaseAPI {
    @Autowired
    private ICartItemServiceAPI cartItemServiceAPI;

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel addCartItem(@RequestBody CommandAPIAddCartItem command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null || StringUtils.isBlank(userEntity.getId())) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            command.setUserId(userEntity.getId());
            return ResponseModel.builder()
                    .payload(cartItemServiceAPI.addCartItem(command))
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

    @PostMapping("/remove")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel removeCartItem(@RequestBody CommandRemoveCartItem command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null || StringUtils.isBlank(userEntity.getId())) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            command.setUserId(userEntity.getId());
            return ResponseModel.builder()
                    .payload(cartItemServiceAPI.removeCartItem(command))
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

    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel getPaginatedList(@RequestBody CommandGetListCartItem command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null || StringUtils.isBlank(userEntity.getId())) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            command.setUserId(userEntity.getId());
            return ResponseModel.builder()
                    .payload(cartItemServiceAPI.getPaginatedList(command))
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


    @PostMapping(value = "/purchased")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel getPurchasedPaginatedList(@RequestBody CommandGetListCartItem command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }
            command.setUserId(userEntity.getId());
            command.setPurchased(true);
            command.setHasDatasetCollection(true);

            return ResponseModel.builder()
                    .payload(cartItemServiceAPI.getPaginatedList(command))
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

































