package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.command.user.CommandAPIUserLogIn;
import com.caovy2001.data_everywhere.command.user.CommandAPIUserSignUp;
import com.caovy2001.data_everywhere.command.user.CommandUpdateUserDetail;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.caovy2001.data_everywhere.service.user.IUserServiceAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserAPI extends BaseAPI {

    @Autowired
    private IUserServiceAPI userServiceAPI;

    @PostMapping("/log_in")
    public ResponseModel logIn(@RequestBody CommandAPIUserLogIn command) {
        try {
            UserEntity userEntity = userServiceAPI.logIn(command);
            return ResponseModel.builder()
                    .payload(userEntity)
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

    @PostMapping("/sign_up")
    public ResponseModel signUp(@RequestBody CommandAPIUserSignUp command) {
        try {
            UserEntity userEntity = userServiceAPI.signUp(command);
            return ResponseModel.builder()
                    .payload(userEntity)
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

    @GetMapping("/detail")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel getDetail() {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }

            return ResponseModel.builder()
                    .payload(userEntity)
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

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel update(@RequestBody CommandUpdateUserDetail command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }
            command.setUserId(userEntity.getId());

            return ResponseModel.builder()
                    .payload(userServiceAPI.update(command))
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

    @PostMapping("/update_password")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseModel updatePassword(@RequestBody CommandUpdateUserDetail command) {
        try {
            UserEntity userEntity = this.getUser();
            if (userEntity == null) {
                throw new Exception(ExceptionConstant.auth_invalid);
            }
            command.setUserId(userEntity.getId());

            return ResponseModel.builder()
                    .payload(userServiceAPI.updatePassword(command))
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
















