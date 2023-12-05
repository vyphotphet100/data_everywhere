package com.caovy2001.data_everywhere.service.user;

import com.caovy2001.data_everywhere.command.cart_item.CommandGetListCartItem;
import com.caovy2001.data_everywhere.command.dataset_collection.CommandGetListDatasetCollection;
import com.caovy2001.data_everywhere.command.user.*;
import com.caovy2001.data_everywhere.constant.Constant;
import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.CartItemEntity;
import com.caovy2001.data_everywhere.entity.DatasetCollectionEntity;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.pagination.Paginated;
import com.caovy2001.data_everywhere.repository.UserRepository;
import com.caovy2001.data_everywhere.service.BaseService;
import com.caovy2001.data_everywhere.utils.JwtUtil;
import com.caovy2001.data_everywhere.utils.ParseObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class UserService extends BaseService implements IUserService, IUserServiceAPI {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserEntity logIn(@NonNull CommandAPIUserLogIn command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUsername(), command.getPassword())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        UserEntity existingUser = userRepository.findByUsernameAndPassword(command.getUsername(), command.getPassword());
        if (existingUser == null) {
            throw new Exception("username_or_password_invalid");
        }

        Map<String, String> map = new HashMap<>();
        map.put("id", existingUser.getId());
        String token = null;
        token = JwtUtil.generateToken(ParseObjectUtil.objectToJsonString(map));
        if (StringUtils.isBlank(token)) {
            throw new Exception("cannot_generate_token");
        }

        existingUser.setToken(token);
        existingUser.setPassword(null);
        return existingUser;
    }

    @Override
    public UserEntity signUp(@NonNull CommandAPIUserSignUp command) throws Exception {
        if (StringUtils.isAnyBlank(
                command.getUsername(),
                command.getPassword(),
                command.getConfirmPassword(),
                command.getFullName())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        if (!command.getPassword().equals(command.getConfirmPassword())) {
            throw new Exception("password_not_match");
        }

        long _countByUsername = userRepository.countByUsername(command.getUsername());
        if (_countByUsername > 0) {
            throw new Exception("username_exist");
        }

        UserEntity userEntity = userRepository.insert(UserEntity.builder()
                        .username(command.getUsername())
                        .password(command.getPassword())
                        .address(command.getAddress())
                        .birthday(command.getBirthday())
                        .fullName(command.getFullName())
                .build());
        userEntity.setPassword(null);
        return userEntity;
    }

    @Override
    public UserEntity update(CommandUpdateUserDetail command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUserId(), command.getFullName(), command.getBirthday(), command.getAddress())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        UserEntity userEntity = userRepository.findById(command.getUserId()).orElse(null);
        if (userEntity == null) {
            throw new Exception("user_not_exist");
        }

        userEntity.setFullName(command.getFullName());
        userEntity.setBirthday(command.getBirthday());
        userEntity.setAddress(command.getAddress());
        userEntity = userRepository.save(userEntity);

        userEntity.setPassword(null);
        return userEntity;
    }

    @Override
    public UserEntity updatePassword(CommandUpdateUserDetail command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUserId(), command.getCurrentPassword(), command.getNewPassword(), command.getConfirmPassword())) {
            throw new Exception(ExceptionConstant.missing_param);
        }

        UserEntity userEntity = userRepository.findById(command.getUserId()).orElse(null);
        if (userEntity == null) {
            throw new Exception("user_not_exist");
        }

        if (!command.getCurrentPassword().equals(userEntity.getPassword())) {
            throw new Exception("old_password_wrong");
        }

        if (command.getCurrentPassword().equals(command.getNewPassword())) {
            throw new Exception("old_password_equal_new_password");
        }

        if (!command.getNewPassword().equals(command.getConfirmPassword())) {
            throw new Exception("confirm_password_not_match");
        }

        userEntity.setPassword(command.getNewPassword());
        userEntity = userRepository.save(userEntity);
        userEntity.setPassword(null);
        return userEntity;
    }

    @Override
    public boolean logInFromThirdParty(@NonNull CommandLoginFromThirdPartyResponse command) throws Exception {
        if (StringUtils.isBlank(command.getSecretKey())) {
            log.error("[logInFromThirdParty]: secret_login_key null");
            throw new Exception(ExceptionConstant.error_occur);
        }

        if (command.getStatus() == null) {
            log.error("[logInFromThirdParty]: status_null");
            throw new Exception(ExceptionConstant.error_occur);
        }

        if (Constant.SecretLoginKey.CHATBOT.equals(command.getSecretKey())) {
            Map<String, Object> message = new HashMap<>();
            message.put("status", command.getStatus());
            if (command.getUser() != null) {
                message.put("user", command.getUser());
            }
            simpMessagingTemplate.convertAndSend("/socket_topic/log_in_with_chatbot_acc", objectMapper.writeValueAsString(message));
            return true;
        }

        return false;
    }

    @Override
    public List<UserEntity> getList(@NonNull CommandGetListUser command) {
        Query query = this.buildQueryGetList(command);
        if (query == null) {
            return new ArrayList<>();
        }

        long total = mongoTemplate.count(query, UserEntity.class);
        if (total == 0L) {
            return new ArrayList<>();
        }

        List<UserEntity> userEntities = mongoTemplate.find(query, UserEntity.class);
        return userEntities;
    }

    private Query buildQueryGetList(@NonNull CommandGetListUser command) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriteriaList = new ArrayList<>();
        List<Criteria> andCriteriaList = new ArrayList<>();

        if (StringUtils.isNotBlank(command.getId())) {
            andCriteriaList.add(Criteria.where("id").is(command.getId()));
        }

        if (CollectionUtils.isNotEmpty(orCriteriaList)) {
            criteria.orOperator(orCriteriaList);
        }

        if (CollectionUtils.isNotEmpty(andCriteriaList)) {
            criteria.andOperator(andCriteriaList);
        }
        query.addCriteria(criteria);
        return query;
    }
}

















