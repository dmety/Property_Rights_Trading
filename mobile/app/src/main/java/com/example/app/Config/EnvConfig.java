package com.example.app.Config;

import com.example.testforenv.BuildConfig;

public class EnvConfig {
    public static final String BASE_URL = BuildConfig.API_BASE_URL.replaceAll("/+$", "") + "/";

    public static final String LOCAL_AI_BACK = BuildConfig.LOCAL_AI_BACKEND_URL;
    public static final String PYTHON_AI_BACK = BuildConfig.PYTHON_AI_BACKEND_URL;

    /***********************************/




    public static final String chatAnyWhereToken = BuildConfig.CHAT_ANYWHERE_TOKEN;
    public static final String chatAnyWhereUrl = BuildConfig.CHAT_ANYWHERE_URL;
    public static String QRCodeUrl = BASE_URL + "qrcode";
    public static String TransFerReURL = BASE_URL + "project/transferRe";
    public static String VerifyBookDownloadUrl = BASE_URL + "user/downloadVerifyBook";
    public static String VerPrivateKey = BASE_URL + "user/verPrivateKey";
    public static String LOGIN_URL = BASE_URL  + "user/androidLogin";
    public static String LOGIN_WITH_OUT_PASS_URL  = BASE_URL + "user/loginWithOutPass";
    public static String PROJECT_INFO_BY_PROJECT_CODE = BASE_URL + "project/getAllProjectInfo";
    public static String USERNAME_PROJECT_INFO_LIST_URL = BASE_URL+"project/getProjectInfoByUserName";
    public static String USERIDNUMBER_PROJECT_INFO_LIST_URL = BASE_URL+"project/getProjectInfoByIdNumber";
    public static String IS_PUB_UP_URL = BASE_URL + "project/getIsPubUp";
    public static String UP_CONTRACT_LIST_URL = BASE_URL + "project/getUpContract";
    public static String REVIEW_TODO_LIST_URL = BASE_URL + "work/getReviewToDoList";
    public static String REVIEW_DONE_LIST_URL = BASE_URL + "work/getReviewDoneList";
    public EnvConfig() {
    }
    public static String getChatAnyWhereToken() {
        return chatAnyWhereToken;
    }


}

