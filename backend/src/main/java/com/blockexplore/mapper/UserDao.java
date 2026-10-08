package com.blockexplore.mapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blockexplore.model.User;
import com.blockexplore.request.LoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserDao extends BaseMapper<User> {
    User getUserById(String id);
    Boolean register(User user);
    User login(LoginRequest loginRequest);
    boolean isUserExist(String loginName);

    Boolean createUser(User user);

    List<User> getUserList();

    Boolean deleteUserByName(String username);

    String getUserAddressById(String userId);

    List<String> getAllId();

    void init(User admin);

    Boolean androidLogin(LoginRequest loginRequest);

    String getUserAddressByName(String loginName);

    User getUserByName(String loginName);

    String getOrgNameById(String s);

    String getIdByName(String userName);
}
