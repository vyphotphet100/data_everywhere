package com.caovy2001.data_everywhere.service.user;

import com.caovy2001.data_everywhere.command.user.CommandAPIUserLogIn;
import com.caovy2001.data_everywhere.command.user.CommandAPIUserSignUp;
import com.caovy2001.data_everywhere.command.user.CommandUpdateUserDetail;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.service.IBaseService;

public interface IUserServiceAPI extends IBaseService {
    UserEntity logIn(CommandAPIUserLogIn command) throws Exception;

    UserEntity signUp(CommandAPIUserSignUp command) throws Exception;

    UserEntity update(CommandUpdateUserDetail command) throws Exception;

    UserEntity updatePassword(CommandUpdateUserDetail command) throws Exception;
}
