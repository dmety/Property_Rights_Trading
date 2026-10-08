package com.blockexplore.service;

import com.blockexplore.callback.QRCodeLoginCallBack;
import com.blockexplore.model.Claims;
import com.blockexplore.model.ConTractUser;
import com.blockexplore.model.Organ;
import com.blockexplore.model.User;
import com.blockexplore.request.LoginRequest;
import com.blockexplore.request.LoginWithOutPassRequest;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User getUserById(String id);
    Result<String> register(User user) throws Exception;
    Result<User> login(LoginRequest loginRequest) throws Exception;
    Boolean isUserExist(String username);

    Result<String> createUser(User user) throws ABICodecException;

    Result<List<ConTractUser>> getUserList();

    Result<String> deleteUser(String username);

    Result<String> setUserStatus(String status, String id,String address);

    Result<String> init() throws ABICodecException;

    Result<String> loadVerifyBook(String username) throws Exception;

    Result<String> verPrivateKey(String keyBook) throws Exception;

    Result<Object> androidLogin(LoginRequest loginRequest) throws Exception;

    Result<Object> loginWithOutPass(LoginWithOutPassRequest loginRequest) throws Exception;
    QRCodeLoginCallBack qrCodeLogin(String vc) throws Exception;
    Claims getClaims(String vc);

    Result<Object> verToken(String token);
}
