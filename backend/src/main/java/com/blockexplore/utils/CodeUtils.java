package com.blockexplore.utils;

import com.blockexplore.config.EnvConfig;
import org.fisco.bcos.sdk.abi.ABICodec;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.crypto.CryptoSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeUtils {
    CryptoSuite cryptoSuite = new CryptoSuite(1);
    ABICodec abiCodec = new ABICodec(cryptoSuite);
    public String encode(String funcName, List<String> funcParam) throws ABICodecException {
        // 创建一个新的 List<Object> 来存储转换后的参数
        List<Object> params = new ArrayList<>();
        // 将 List<String> 中的每一个元素添加到 List<Object> 中
        for (String param : funcParam) {
            params.add(param);
        }
        // 使用转换后的 List<Object> 进行方法编码
        return abiCodec.encodeMethod(EnvConfig.SYSTEM_CONTRACT_ABI, funcName, params);
    }
    public List<Object> decode(String funcName,String output) throws ABICodecException {
        return abiCodec.decodeMethod(EnvConfig.SYSTEM_CONTRACT_ABI, funcName, output);
    }
    public List<Object> decodeRight(String funcName,String output) throws ABICodecException {
        return abiCodec.decodeMethod(EnvConfig.RIGHT_CONTRACT_ABI, funcName, output);
    }
    public List<Object> decodeProject(String funcName,String output) throws ABICodecException {
        return abiCodec.decodeMethod(EnvConfig.PROJECT_CONTRACT_ABI, funcName, output);
    }
    public List<Object> decodeDIDContract(String funcName,String output) throws ABICodecException {
        return abiCodec.decodeMethod(EnvConfig.BLOCK_TRADE_DID_ABI, funcName, output);
    }
    public static List<String> convertToStringList(List<Object> inputList) {
        System.out.println("InputList ===> " + inputList);
        // 将列表转换为字符串，并移除多余的空格
        String s = inputList.toString()
                .substring(2, inputList.toString().length() - 2)  // 去掉外层的方括号
                .replaceAll("\\s+", "");  // 移除所有空格

        // 使用逗号分割字符串并返回结果
        List<String> split = Arrays.asList(s.split(","));
        return split;
    }

    public static List<String> processStringData(String input) {
        // 去掉外层的方括号、引号和反斜杠
        String cleanedInput = input.replaceAll("[\\[\\]\"\\\\]", "").trim();
        cleanedInput = cleanedInput.trim();

        // 按逗号分割元素，并存储到字符串列表中
        String[] elements = cleanedInput.split(",\\s*"); // 使用正则分割并去除多余的空格
        List<String> resultList = new ArrayList<>();

        // 遍历分割后的元素并添加到列表中
        for (String element : elements) {
            resultList.add(element.trim());
        }

        return resultList;
    }

}
