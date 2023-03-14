package com.caovy2001.data_everywhere.service;

import com.caovy2001.data_everywhere.constant.Constant;
import com.caovy2001.data_everywhere.model.CommonResponseStatus;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class BaseService implements IBaseService {
    @Override
    public Object responseExceptionForAPI(@NonNull String exceptionCode) {
        CommonResponseStatus commonResponseStatus = CommonResponseStatus.builder()
                .httpStatus(HttpStatus.EXPECTATION_FAILED)
                .exceptionCode(exceptionCode)
                .build();
        Map<String, Object> res = new HashMap<>();
        res.put(Constant.commonResponseStatus, commonResponseStatus);
        return res;
    }
}
