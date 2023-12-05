package com.caovy2001.data_everywhere.service.user;

import com.caovy2001.data_everywhere.command.user.CommandGetListUser;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.service.IBaseService;
import lombok.NonNull;

import java.util.List;

public interface IUserService extends IBaseService {
    List<UserEntity> getList(@NonNull CommandGetListUser command) throws Exception;
}
