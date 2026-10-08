package com.blockexplore.controller;

import cn.edu.cjxy.iotlink.model.SysDevice;
import cn.edu.cjxy.iotlink.service.DeviceService;
import com.blockexplore.model.ConTractUser;
import com.blockexplore.model.User;
import com.blockexplore.request.LoginRequest;
import com.blockexplore.request.LoginWithOutPassRequest;
import com.blockexplore.service.UserService;
import com.blockexplore.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    DeviceService deviceService;
    @RequestMapping( value = "/info")
    public Result<User> getUserInfo(@RequestParam("id") String id){
        User userInfo = userService.getUserById(id);
        return Result.success(userInfo);

    }
    @PostMapping(value = "/register")
    public Result<String> register(@RequestBody User user) throws Exception {
        Result <String> result = userService.register(user);
        return result;
    }
    @PostMapping(value = "/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest) throws Exception {
        Result<User> result = userService.login(loginRequest);
        return result;
    }
    @PostMapping("/verToken")
    public Result<Object> verToken(@RequestHeader("Authorization") String token) {
        // 如果 token 格式是 "Bearer <token>", 需要去掉 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 "Bearer " 前缀
        }
        return userService.verToken(token);
    }


    @GetMapping(value = "/isUserExist")
    public Result<Boolean> isUserExist(@RequestParam("loginName") String loginName){
        Boolean result = userService.isUserExist(loginName);
        return Result.success(result);
    }
    // Admin
    @PostMapping("/createUser")
    public Result<String> createUser (@RequestBody @Validated User user) throws ABICodecException {
        log.info("注册用户:{}",user);
        return userService.createUser(user);
    }
    @GetMapping("/getUserList")
    public Result<List<ConTractUser>> getUserList(){
        return userService.getUserList();
    }
    @GetMapping("/deleteUser")
    public Result<String> deleteUser(@RequestParam("userId") String userId) {
        return userService.deleteUser(userId);
    }
    @GetMapping("/setStatus")
    public Result<String> setUserStatus(@RequestParam("status") String status ,@RequestParam("id")String id ,@RequestParam("address") String address){
        return userService.setUserStatus(status,id,address);
    }

    /**
     * 初始化管理员用户
     * @return
     * @throws ABICodecException
     */
    @GetMapping("/init")
    public Result<String> init() throws ABICodecException {
       return userService.init();
    }
//    @PostMapping("/downloadVerifyBook")
//    public Result<String> downloadVerifyBook(@RequestBody LoginRequest loginRequest) throws Exception {
//        return userService.loadVerifyBook(loginRequest);
//    }


    // Android 部分
    @PostMapping("/androidLogin")
    public Result<Object> androidLogin(@RequestBody LoginRequest loginRequest) throws Exception {
        return userService.androidLogin(loginRequest);
    }
    @GetMapping("/verPrivateKey")
    public Result<String> verPrivateKey(@RequestParam("keyBook") String keyBook) throws Exception {
        return userService.verPrivateKey(keyBook);
    }
    @PostMapping("/loginWithOutPass")
    public Result<Object> loginWithOutPass(@RequestBody LoginWithOutPassRequest loginRequest) throws Exception {
        return userService.loginWithOutPass(loginRequest);
    }

}
