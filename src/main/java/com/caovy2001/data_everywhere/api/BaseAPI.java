package com.caovy2001.data_everywhere.api;

import com.caovy2001.data_everywhere.entity.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BaseAPI {
    protected UserEntity getUser() {
        try {
            return (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}
