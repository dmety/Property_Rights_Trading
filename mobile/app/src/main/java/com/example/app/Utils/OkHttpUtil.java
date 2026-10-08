package com.example.app.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.app.Activity.IndexActivity;
import com.example.app.Activity.LoginActivity;
import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    // 对安卓OkHttp包封装，与后台进行API对接


    // 创建 OkHttpClient 实例，设置一些默认参数
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // 封装 GET 请求
    public static void sendGetRequest(String url, OkHttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError("Error code: " + response.code());
                }
            }
        });
    }

    // 封装 POST 请求
    public static void sendPostRequest(String url, Map<String, String> params, OkHttpCallback callback) {
        // 构建表单 body
        FormBody.Builder formBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        RequestBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body().string());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError("Error code: " + response.code());
                }
            }
        });
    }
    // 封装 JSON POST 请求
    public static void sendJsonPostRequest(String url, String json, OkHttpCallback callback) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body().string());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError("Error code: " + response.code());
                }
            }
        });
    }

    public static void QRCodeReview() {

    }


    // 定义回调接口
    public interface OkHttpCallback {
        List<ProjectBaseInfo> onSuccess(String response) throws Exception;

        void onFailure(Exception e);

        void onError(String error);
    }
    public static void androidLogin(String loginName, String loginPass, Context context) {
        // 构造一个 JSON 对象
//        Intent fake = new Intent(context, IndexActivity.class);
//        context.startActivity(fake);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginName", loginName);
            jsonObject.put("loginPass", loginPass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 将 JSON 对象转换为字符串
        String json = jsonObject.toString();
        // 将JSON对象使用POST方法发送到后台进行第一次登录
        sendJsonPostRequest(EnvConfig.LOGIN_URL, json, new OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
                JSONObject result = new JSONObject(response);
                if (result.getString("code").equals("200")){
                    Intent intent = new Intent(context, IndexActivity.class);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // 获得返回值中data部分
                    JSONObject data = result.getJSONObject("data");
                    // 获取需要保存的数据
                    //以及证明凭证VC部分
                    String vc = data.getString("vc");
//                    String userInfo = data.getString("userInfo");

                    Log.d("data===>",  data.toString());
                    //从data中获取用户私钥和管理员公钥
                    String privateKey = data.getString("privateKey");
                    String publicKey = data.getString("publicKey");
                    String adminPublicKey = data.getString("adminPublicKey");
                    String idNumber = data.getString("userIdNumber");
                    //从VC中拿到claims
                    //使用KeyUtils中的方法解密VC内容
                    String vcContent = KeyUtils.decrypt(privateKey,vc);
                    String user = new JSONObject(vcContent).put("idNumber", idNumber).toString();
                    Log.d("user", user);
                    //将UserInfo用户状态存入全局状态
                    editor.putString("userInfo",user);
                    editor.apply();
                    // 保存数据到文件
                    saveDataToFile(context, vc, user, privateKey, publicKey, adminPublicKey);
                    context.startActivity(intent);
                }

                return null;
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
            }

            @Override
            public void onError(String error) {
                Log.d("TAG", "onError: " + EnvConfig.LOGIN_URL);
            }
        });
    }
    public static void saveDataToFile(Context context, String vc, String userInfo, String privateKey, String publicKey, String adminPublicKey) {
        // 定义目标目录
        File directory = new File(context.getFilesDir(), "KeyStore");
        // 如果目录不存在，则创建它
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs(); // 创建多级目录
            if (!isCreated) {
                Log.e("TAG", "目录创建失败");
                return;
            }
        }
        // 保存 VC 到文件
        saveToFile(directory, "vc.dat", vc);
        // 保存 userInfo 到文件
        saveToFile(directory, "userinfo.dat", userInfo);
        // 保存 privateKey 到文件
        saveToFile(directory, "privateKey.dat", privateKey);
        // 保存 publicKey 到文件
        saveToFile(directory, "publicKey.dat", publicKey);
        // 保存 adminPublicKey 到文件
        saveToFile(directory, "adminPublicKey.dat", adminPublicKey);
    }

    // 统一的方法用于保存数据到指定文件
    private static void saveToFile(File directory, String fileName, String data) {
        File file = new File(directory, fileName);

        // 写入数据到文件
        try (FileOutputStream fos = new FileOutputStream(file, false)) { // false 表示覆盖写入
            fos.write(data.getBytes());
            Log.d("TAG", "文件保存成功: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("TAG", "文件保存失败: " + e.getMessage());
        }
    }
    public static String loginWithOutPass(String vc,Context context) throws Exception {
        // 读取本地KeyStore密钥仓库
        File directory = new File(context.getFilesDir(), "KeyStore");
        // 获得管理员公钥
        File adminPublicKeyFile = new File(directory, "adminPublicKey.dat");
        // 以及用户私钥
        File privateKeyFile = new File(directory, "privateKey.dat");
        //最后使用DIDUtils的构造方法使用用户私钥构建用户私钥用户密钥对工具
        DIDUtils didUtils = new DIDUtils(readFileContent(privateKeyFile));
        // 拿到管理员公钥加密证明凭证VC生成身份凭证VP
        String vp = didUtils.generateVP(vc);

        String vcObj = KeyUtils.encrypt(readFileContent(adminPublicKeyFile),vp);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vp",vcObj);
        // 使用sendJsonRequest发送POST请求发送VP到服务端
        sendJsonPostRequest(EnvConfig.LOGIN_WITH_OUT_PASS_URL,jsonObject.toString(), new OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject result = new JSONObject(response);
                // 将
                SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userInfo",result.getString("data"));
                editor.apply();
                Intent intent = new Intent(context, IndexActivity.class);
                Log.d("免密登录","成功");
                context.startActivity(intent);
                return null;
            }
            @Override
            public void onFailure(Exception e) {
                Log.d("LOGIN_WITH_OUT_PASS_URL", e.getMessage());
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onError(String error) {
                Log.d("LOGIN_WITH_OUT_PASS_URL", error);

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);;
            }

        });
        return null;
    }
    public static String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            Log.e("TAG", "Error reading file: " + e.getMessage());
            e.printStackTrace(); // 处理异常
        }
        return content.toString().trim(); // 去除末尾空格和换行
    }
    public static String QRCodeLogin(String url ,String status, Context context) throws Exception {
        Log.d("QRCODEURL",url);
        // 构建请求体 JSON
        JSONObject requestBody = new JSONObject();
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);


        // 如果status为0，仅发送status
        if ("0".equals(status)) {
            requestBody.put("status", status); // 仅发送二维码状态
        }
        // 如果status为1，发送加密的vc和status
        else if ("1".equals(status)) {
            // 获取KeyStore目录
            File directory = new File(context.getFilesDir(), "KeyStore");
            File vcFile = new File(directory, "vc.dat");

            // 读取vc.txt文件内容
            String vc = readFileContent(vcFile);

            // 加载本地的adminPublicKey.txt进行加密
            File adminPublicKey = new File(directory, "adminPublicKey.dat");
            File privateKey = new File(directory, "privateKey.dat");

            // 先解密本地VC, 然后用管理员公钥加密数据
            String encryptedVc = KeyUtils.encrypt(readFileContent(adminPublicKey), KeyUtils.decrypt(readFileContent(privateKey), vc));
            Log.d("encryptedVc ===>", encryptedVc);
            // 传递加密的vc
            requestBody.put("vc", encryptedVc); // 加密的 vc
            requestBody.put("status", status);  // 二维码状态
        }

        // 发送POST请求
        sendJsonPostRequest(url, requestBody.toString(), new OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) {
                Log.d("QRCodeUrl", response); // 请求成功后的日志输出
                return null;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("QRCodeUrl", "Request failed", e); // 请求失败的日志输出
            }

            @Override
            public void onError(String error) {
                Log.e("QRCodeUrl", "Error: " + error); // 错误处理
            }
        });

        return null;
    }
    public static void getAllProjectBaseInfoByIdNumber(String idNumber, final ProjectInfoCallback callback) {
        sendGetRequest(EnvConfig.USERIDNUMBER_PROJECT_INFO_LIST_URL + "?idNumber=" + idNumber, new OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                Gson gson = new Gson();
                // 获取ResponseBody
                JsonObject responseBody = gson.fromJson(response, JsonObject.class);
                JsonArray dataArray = responseBody.getAsJsonArray("data");

                List<ProjectBaseInfo> projectList = new ArrayList<>();

                // 遍历 JSON 数组并提取字段
                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject projectObject = dataArray.get(i).getAsJsonObject();

                    // 获取每个字段的值
                    String projectCode = projectObject.get("projectCode").getAsString();
                    String projectName = projectObject.get("projectName").getAsString();
                    String transType = projectObject.get("transKind").getAsString();  // JSON 字段名不同，手动调整
                    String projectStatus = projectObject.get("projectStatus").getAsString();
//                    String orgName = projectObject.get("aptOrgId").getAsString();  // 机构名称可能需要映射或通过 ID 查找
                    String doneTime = projectObject.get("createTime").getAsString();
                    String rightNo = projectObject.get("rightNo").getAsString();

                    // 将解析的值存储到 ProjectBaseInfo 对象
                    ProjectBaseInfo project = new ProjectBaseInfo(projectCode,rightNo, projectName, transType, projectStatus, projectStatus, doneTime);
                    // 将对象添加到列表中
                    projectList.add(project);
                }

                Log.d("TAG", EnvConfig.USERIDNUMBER_PROJECT_INFO_LIST_URL + "?idNumber=" + idNumber);
                Log.d("TAG", response);

                // 使用回调函数返回项目列表
                callback.onSuccess(projectList);
                return projectList;
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("TAG", EnvConfig.USERIDNUMBER_PROJECT_INFO_LIST_URL + "?idNumber=" + idNumber);
                Log.d("TAG", e.getMessage());

                // 调用回调的失败方法
                callback.onFailure(e);
            }

            @Override
            public void onError(String error) {
                Log.d("TAG", EnvConfig.USERIDNUMBER_PROJECT_INFO_LIST_URL + "?idNumber=" + idNumber);
                Log.d("TAG", error);

                // 调用回调的错误方法
                callback.onError(error);
            }
        });
    }

    // 定义回调接口
    public interface ProjectInfoCallback {
        void onSuccess(List<ProjectBaseInfo> projectList);
        void onFailure(Exception e);
        void onError(String error);
    }

}
