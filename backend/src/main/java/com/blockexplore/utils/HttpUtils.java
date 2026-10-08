package com.blockexplore.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.model.Claims;
import com.blockexplore.model.WeBASEUser;
//import jdk.nashorn.internal.runtime.regexp.joni.Config;
import lombok.var;
import org.fisco.bcos.sdk.abi.ABICodecException;

import java.util.ArrayList;
import java.util.List;

public class HttpUtils {
    public static String BaseRequest(String groupId, String user, String contractName, String contractPath,
                                     String funcName, List<String> funcParam, String contractAddress,String contractAbi) {
        // 构建 JSON 数据
        var json = JSONUtil.createObj()
                .set("groupId", groupId)
                .set("user", user)
                .set("contractName", contractName)
                .set("contractPath", contractPath)
                .set("version", "")
                .set("funcName", funcName)
                .set("funcParam", funcParam)
                .set("contractAddress", contractAddress)
                .set("contractAbi", JSONUtil.parseArray(contractAbi))
                .set("useAes", false)
                .set("useCns", false)
                .set("cnsName", "");
        System.out.println("RequestBody ===>" + json);

        // 发送 HTTP POST 请求
        return HttpRequest.post(EnvConfig.WeBASE_CONTRACT_URL)
                .header("Content-Type", "application/json; utf-8")
                .body(json.toString())
                .execute()
                .body();
    }
    public static String BaseRequest2(String groupId, String user, String contractName, String contractPath,
                                     String funcName, List<Object> funcParam, String contractAddress,String contractAbi) {
        // 构建 JSON 数据
        var json = JSONUtil.createObj()
                .set("groupId", groupId)
                .set("user", user)
                .set("contractName", contractName)
                .set("contractPath", contractPath)
                .set("version", "")
                .set("funcName", funcName)
                .set("funcParam", funcParam)
                .set("contractAddress", contractAddress)
                .set("contractAbi", JSONUtil.parseArray(contractAbi))
                .set("useAes", false)
                .set("useCns", false)
                .set("cnsName", "");
        System.out.println("RequestBody ===>" + json);

        // 发送 HTTP POST 请求
        return HttpRequest.post(EnvConfig.WeBASE_CONTRACT_URL)
                .header("Content-Type", "application/json; utf-8")
                .body(json.toString())
                .execute()
                .body();
    }

    public static String didRequest(String address, String funcName, List<String> funcParam){
        return BaseRequest("1",address,"DidRegCenter","blocktrade",funcName,funcParam,EnvConfig.DID_CONTRACT_ADDRESS,EnvConfig.DID_CONTRACT_ABI);
    }
    public static String rightRequest(String address, String funcName, List<Object> funcParam){
        return BaseRequest2("1",address,"Right","blocktrade",funcName,funcParam,EnvConfig.RIGHT_CONTRACT_ADDRESS,EnvConfig.RIGHT_CONTRACT_ABI);
    }

    public static String systemRequest(String address, String funcName, List<String> funcParam){
        return BaseRequest("1",address,"System","blocktrade",funcName,funcParam,EnvConfig.SYSTEM_CONTRACT_ADDRESS,EnvConfig.SYSTEM_CONTRACT_ABI);
    }
    public static String systemRequest2(String address, String funcName, List<Object> funcParam){
        return BaseRequest2("1",address,"System","blocktrade",funcName,funcParam,EnvConfig.SYSTEM_CONTRACT_ADDRESS,EnvConfig.SYSTEM_CONTRACT_ABI);
    }
    public static String projectRequest(String address,String funcName,List<String> funcParam){
        return BaseRequest("1",address,"ProjectTrade","blocktrade",funcName,funcParam,EnvConfig.PROJECT_CONTRACT_ADDRESS,EnvConfig.PROJECT_CONTRACT_ABI);
    }
    public static String projectRequest2(String address,String funcName,List<Object> funcParam){
        return BaseRequest2("1",address,"ProjectTrade","blocktrade",funcName,funcParam,EnvConfig.PROJECT_CONTRACT_ADDRESS,EnvConfig.PROJECT_CONTRACT_ABI);
    }
    public static WeBASEUser getUser(String userName) {
        String url = EnvConfig.WeBASE_CREATE_USER_URL + userName;
        WeBASEUser weBASEUser = new WeBASEUser();
        String response = cn.hutool.http.HttpUtil.get(url);
        JSONObject result = JSONUtil.parseObj(response);
        if (result.getInt("code") == null) {

            weBASEUser.setAddress(result.getStr("address"));
            weBASEUser.setPublicKey(result.getStr("publicKey"));
            return weBASEUser;
        } else {
            return null;
        }
    }

    public static String getUserPrivateKey(String username) {
        JSONArray keyStore = JSONUtil.parseArray(HttpUtil.get(EnvConfig.WeBASE_KEY_STORE));
        for (int i = 0; i < keyStore.size(); i++){
            JSONObject jsonObject = keyStore.get(i, JSONObject.class);
            if (jsonObject.getStr("userName").equals(username)){
                return jsonObject.getStr("privateKey");
            }
        }
        return null;
    }
    public static String deleteWeBASEUser(String userAddress) {
        System.out.println("Delete ===>" + EnvConfig.WeBASE_DELETE_USER_URL + userAddress);
        String response = HttpRequest.delete(EnvConfig.WeBASE_DELETE_USER_URL + userAddress)
                .execute()
                .body();
        return response;
    }

    public static String getUserAddress(String username) {
        JSONArray keyStore = JSONUtil.parseArray(HttpUtil.get(EnvConfig.WeBASE_KEY_STORE));
        for (int i = 0; i < keyStore.size(); i++){
            JSONObject jsonObject = keyStore.get(i, JSONObject.class);
            if (jsonObject.getStr("userName").equals(username)){
                return jsonObject.getStr("address");
            }
        }
        return null;
    }
    public static String getUserPublicKey(String username) {
        JSONArray keyStore = JSONUtil.parseArray(HttpUtil.get(EnvConfig.WeBASE_KEY_STORE));
        for (int i = 0; i < keyStore.size(); i++){
            JSONObject jsonObject = keyStore.get(i, JSONObject.class);
            if (jsonObject.getStr("userName").equals(username)){
                return jsonObject.getStr("publicKey");
            }
        }
        return null;
    }
    public static String didContractRequest(String did) {
        List<String> params = new ArrayList<>();
        List<Object> nullParam = new ArrayList<>();
        params.add(did);

        String userAddress = did.split(":")[2];

        JSONArray document = JSONUtil.parseArray(HttpUtils.didRequest(EnvConfig.ADMIN_ADDRESS, "getDocument", params));
        List<String> content = document.getBeanList(0, String.class);

        String result = BaseRequest2("1", userAddress, "BlockTradeDid", "blocktrade", "queryClaim", nullParam, content.get(1), EnvConfig.BLOCK_TRADE_DID_ABI);

        return JSONUtil.parseObj(result).getStr("output");
    }


    public static Claims getClaims(String did) throws ABICodecException {
        CodeUtils codeUtils = new CodeUtils();
        Claims claims = new Claims();
        String output = didContractRequest(did);
        System.out.println("queryClaimsResult  ===>"+codeUtils.decodeDIDContract("queryClaim",output));
        List<Object> outputList = codeUtils.decodeDIDContract("queryClaim",output);
        claims.setUserAddr(outputList.get(0).toString());
        claims.setUserType(outputList.get(1).toString());
        claims.setUserName(outputList.get(2).toString());
        claims.setUserOrgan(outputList.get(3).toString());
        claims.setTradeType(outputList.get(4).toString());
        claims.setClaimTime(outputList.get(5).toString());
        claims.setOrgId(outputList.get(6).toString());
        claims.setUserId(outputList.get(7).toString());
        System.out.println("claims===>"+claims);
        return claims;
    }
}
