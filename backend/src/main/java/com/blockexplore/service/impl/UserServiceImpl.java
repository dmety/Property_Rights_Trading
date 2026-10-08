package com.blockexplore.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.callback.QRCodeLoginCallBack;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.UserDao;
import com.blockexplore.model.Claims;
import com.blockexplore.model.ConTractUser;
import com.blockexplore.model.User;
import com.blockexplore.model.WeBASEUser;
import com.blockexplore.request.LoginRequest;
import com.blockexplore.request.LoginWithOutPassRequest;
import com.blockexplore.service.UserService;
import com.blockexplore.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    CodeUtils codeUtils = new CodeUtils();
    JwtUtil jwtUtil = new JwtUtil();
    @Override
    public User getUserById(String id) {
        User user = userDao.getUserById(id);
        return user;
    }

    @Override
    public Result<String> register(User user) throws Exception {
        WeBASEUser weBASEUser = HttpUtils.getUser(user.getLoginName());
        if (weBASEUser == null) {
            return Result.failure("注册失败，用户已存在！");
        } else {
            // 构造请求参数
            List<String> param = new ArrayList<>();
            param.add(weBASEUser.getAddress()); // _userAddr
            param.add(user.getIdCard()); // _cardNo
            param.add(user.getLoginName()); // _name
            param.add("普通用户"); // _role
            param.add("0");
            param.add(weBASEUser.getPublicKey()); // _publicKey
            // 加密
//            user.setLoginPass(EncryptionUtil.encrypt(user.getLoginPass()));
            user.setRole("普通用户");
            user.setAddress(weBASEUser.getAddress());
            user.setOrganId(Long.valueOf(0));
            user.setCreateTime(TimeUtils.getCurrentDateTime());
            user.setEditTime(TimeUtils.getCurrentDateTime());
            try {
//         提前注册了测试用户，这一步报错会导致注册失败，测试用户会占位置
                JSONObject response = JSONUtil.parseObj(HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"createUser",param));
                List<Object> output = codeUtils.decode("createUser",response.getStr("output"));
                List<String> outputList = CodeUtils.convertToStringList(output);
                user.setId(Long.parseLong((outputList.get(0))));
                if (userDao.register(user)){
                    System.out.println("Register Info ===>"+response);
                    return Result.success("注册成功");
                } else {
                    // 注册失败，删除合约中的用户
                    HttpUtils.deleteWeBASEUser(user.getAddress());
                    return Result.failure("注册失败，数据库保存失败！");
                }
            }catch (Exception e){
                // 注册失败，删除合约中的用户
                HttpUtils.deleteWeBASEUser(user.getAddress());
                return Result.failure("注册失败，合约创建失败！");
            }
        }
    }

    @Override
    public Result<User> login(LoginRequest loginRequest) throws Exception {
//        loginRequest.setLoginPass(EncryptionUtil.encrypt(loginRequest.getLoginPass()));
        System.out.println("LoginRequest ===>"+loginRequest.getLoginPass());
        User user = userDao.login(loginRequest);
        if (user != null){
            user.setToken(jwtUtil.generateToken(loginRequest.getLoginName()));
            return Result.success(user);
        } else {
            return Result.failure("登录失败，用户名或密码错误！");
        }
    }

    @Override
    public Boolean isUserExist(String loginName) {
        return userDao.isUserExist(loginName);
    }

    @Override
    public Result<String> createUser(User user) throws ABICodecException {
        // 插入WeBase平台私钥用户
        WeBASEUser weBASEUser = HttpUtils.getUser(user.getLoginName());
        if (weBASEUser == null) {
            return Result.failure("注册失败，用户已存在！");
        }
        List<String> params = new ArrayList<>();
        params.add(weBASEUser.getAddress());
        params.add(user.getIdCard());
        params.add(user.getLoginName());
        params.add(user.getRole());
        params.add(user.getOrganId().toString());
        params.add(weBASEUser.getPublicKey());
        // 插入合约
        JSONObject response = JSONUtil.parseObj(HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"createUser",params));

        List<Object> output = codeUtils.decode("createUser",response.getStr("output"));
        List<String> outputList = CodeUtils.convertToStringList(output);
        user.setAddress(weBASEUser.getAddress());
        user.setId(Long.parseLong(outputList.get(0)));
        // 插入数据库
        user.setCreateTime(TimeUtils.getCurrentDateTime());
        user.setEditTime(TimeUtils.getCurrentDateTime());
        userDao.createUser(user);
        return Result.success("创建成功！");
    }

    @Override
    public Result<List<ConTractUser>> getUserList() {
        List<String> ids = userDao.getAllId();
        List<ConTractUser> conTractUsers = new ArrayList<>();
        for (String id : ids){
            List<String> params = new ArrayList<>();
            params.add(id);
            List<String> response = CodeUtils.processStringData(HttpUtils.systemRequest("","getUser",params));
            ConTractUser conTractUser = new ConTractUser();
            conTractUser.setId(response.get(0));
            conTractUser.setCardNo(response.get(1));
            conTractUser.setUserAddr(response.get(2));
            conTractUser.setLoginName(response.get(3));
            conTractUser.setRole(response.get(4));
            conTractUser.setOrgId(response.get(5));
            conTractUser.setStatus(Boolean.parseBoolean(response.get(6)));
            String orgName = userDao.getOrgNameById(response.get(5));
            conTractUser.setOrgName(orgName);
            conTractUsers.add(conTractUser);
            System.out.println("ConTractUser ===>" + response);
        }

        return Result.success(conTractUsers);
    }

    @Override
    public Result<String> deleteUser(String userId) {
        List<String> params = new ArrayList<>();
        params.add(userId);
        String userAddress = userDao.getUserAddressById(userId);
        // 删除链上私钥用户
        HttpUtils.deleteWeBASEUser(userAddress);
        // 删除合约内用户
        HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"deleteUser",params);
        // 删除数据库内用户
        userDao.deleteUserByName(userId);
        return Result.success("删除成功！");
    }

    @Override
    public Result<String> setUserStatus(String status, String id,String address) {
        List<String> param = new ArrayList<>();
        param.add(status);
        param.add(id);
        param.add("did:blocktrade:" + address);
        HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"setUserStatus",param);
        return Result.success("修改成功！");
    }

    @Override
    public Result<String> init() throws ABICodecException {
        User admin = new User();
        admin.setRole("管理员");
        admin.setLoginName("admin");
        admin.setIdCard("111111111111111111");
        admin.setLoginPass("123456");
        admin.setOrganId(Long.valueOf(0));
        admin.setEditTime(TimeUtils.getCurrentDateTime());
        admin.setCreateTime(TimeUtils.getCurrentDateTime());
        admin.setAddress(EnvConfig.ADMIN_ADDRESS);

        // 插入合约
        List<Object> params = new ArrayList<>();
        params.add(admin.getAddress());
        params.add(admin.getIdCard());
        params.add(admin.getLoginName());
        params.add(admin.getRole());
        params.add(admin.getOrganId());
        params.add(HttpUtils.getUserPublicKey("admin"));
        String content = HttpUtils.systemRequest2(EnvConfig.ADMIN_ADDRESS,"createUser",params);
        System.out.println("InitInputResult===>" + content);
        JSONObject response = JSONUtil.parseObj(content);
        System.out.println("InitOutputResult===>" + response.getStr("output"));
        List<Object> output = codeUtils.decode("createUser",response.getStr("output"));
        List<String> outputList = CodeUtils.convertToStringList(output);
        admin.setId(Long.parseLong(outputList.get(0)));
        // 插入数据库
        userDao.insert(admin);
        return Result.success("初始化成功！");
    }

    @Override
    public Result<String> loadVerifyBook(String username) throws Exception {
        String keyBook = HttpUtils.getUserPrivateKey(username);
        // 加密用户私钥
        String encryptKeyBook = EncryptionUtil.encrypt(keyBook  + ":" + username);
        // 返回加密证书
        return Result.success(encryptKeyBook);
    }

    @Override
    public Result<String> verPrivateKey(String keyBook) throws Exception {
        String content = EncryptionUtil.decrypt(keyBook);
        List<String> keyBooks = Arrays.asList(content.split(":"));
        String privateKey = HttpUtils.getUserPrivateKey(keyBooks.get(1));
        if (privateKey.equals(keyBooks.get(0))) {
            return Result.success("验证成功！");
        } else {
            return Result.failure("验证失败！");
        }

    }

//    @Override
//    public Result<String> androidLogin(LoginRequest loginRequest) {
//        return null;
//    }
    DIDUtil didUtil = new DIDUtil();
    @Override

    // 通过androidLogin方法接收移动端的数据LoginPass和LoginName
    public Result<Object> androidLogin(LoginRequest loginRequest) throws Exception {
        JSONObject result = new JSONObject();
        //判断用户是否在数据库中
        if (userDao.androidLogin(loginRequest)){
            User user = userDao.getUserByName(loginRequest.getLoginName());
            String did = "did:blocktrade:"+ user.getAddress();
            //使用HttpUtils获得用户Claims
            Claims claims = HttpUtils.getClaims(did);
            claims.setPublicKey(HttpUtils.getUserPublicKey(loginRequest.getLoginName()));
            // 用Claims构造VC
            String vc = DIDUtil.generateVC(claims.toJSON());
            // 获取用户私钥 管理员公钥 以及获得的VC
            String privateKey = HttpUtils.getUserPrivateKey(loginRequest.getLoginName());
            String publicKey = HttpUtils.getUserPublicKey(loginRequest.getLoginName());
            String adminPublicKey = HttpUtils.getUserPublicKey("admin");
            System.out.println("VC generate Result === > " + vc);
            // 返回获得的这些信息
            result.set("vc", KeyUtils.encrypt(publicKey,vc.toString()));
            result.set("privateKey",privateKey);
            result.set("adminPublicKey",adminPublicKey);
            result.set("publicKey",publicKey);
            result.set("userIdNumber",user.getIdCard());

            return Result.success(result);
        }
        return Result.failure("账号或密码错误！");
    }

    @Override
    public Result<Object> loginWithOutPass(LoginWithOutPassRequest loginRequest) throws Exception {
        Claims claims = new Claims();
        // 使用管理员私钥解密VP获得真实VP内容
        String vp = KeyUtils.decrypt(EnvConfig.Admin_Private_Key,loginRequest.getVp());
        // 使用DIDUtil验证VP中VC内容由平台签发，并且验证VP中VP签名由用户签名
        if (DIDUtil.verifyVP(vp)){
            // 验证成功后解析VP获得用户信息
            JSONObject user = JSONUtil.parseObj(vp);
            JSONObject credentialSubject = JSONUtil.parseObj(user.get("credentialSubject"));
            claims.setUserAddr(credentialSubject.getStr("userAddr"));
            claims.setUserType(credentialSubject.getStr("userType"));
            claims.setUserName(credentialSubject.getStr("userName"));
            claims.setUserOrgan(credentialSubject.getStr("userOrgan"));
            claims.setTradeType(credentialSubject.getStr("tradeType"));
            claims.setClaimTime(credentialSubject.getStr("claimTime"));
            claims.setUserId(credentialSubject.getStr("userId"));
            claims.setOrgId(credentialSubject.getStr("orgId"));
            //登录成功返回用户声明信息
            return Result.success(claims);
        }
        return Result.failure("VC验证失败！");
    }

    @Override
    public QRCodeLoginCallBack qrCodeLogin(String vc) throws Exception {
        QRCodeLoginCallBack qrCodeLoginCallBack = new QRCodeLoginCallBack();
        // 私钥解密VC
        String vcDec = KeyUtils.decrypt(EnvConfig.Admin_Private_Key,vc);
        if (DIDUtil.verifyVC(vc)){
            // 验证成功
            JSONObject user = JSONUtil.parseObj(vcDec);
            //组装CallBack
            JSONObject credentialSubject = JSONUtil.parseObj(user.get("credentialSubject"));
            qrCodeLoginCallBack.setUserId(credentialSubject.getStr("userId"));
            qrCodeLoginCallBack.setLoginName(credentialSubject.getStr("userName"));
            qrCodeLoginCallBack.setRole(credentialSubject.getStr("userType"));
            qrCodeLoginCallBack.setOrgId(credentialSubject.getStr("userOrgan"));
            qrCodeLoginCallBack.setAddress(credentialSubject.getStr("userAddr"));
            return qrCodeLoginCallBack;

        }
        return null;
    }

    @Override
    public Claims getClaims(String vc) {
        return null;
    }

    @Override
    public Result<Object> verToken(String token) {
        if (jwtUtil.validateToken(token)){
            return Result.success(userDao.getUserByName(jwtUtil.getUsernameFromToken(token)));
        } else {
            return Result.failure("token验证失败！");
        }
    }
}
