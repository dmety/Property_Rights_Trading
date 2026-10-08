package com.example.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.testforenv.R;
import com.example.app.Frame.AiFragment;
import com.example.app.Frame.PublicAnnouncementFragment;
import com.example.app.Frame.WorkSpaceFragment;
import com.example.app.Frame.ProjectInfoFragment;
import com.example.app.Frame.MeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class IndexActivity extends AppCompatActivity {
    private static final String TAG = "IndexActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载主页布局
        setContentView(R.layout.activity_main);

        // 输出 R.id.main_index 的值
        Log.d(TAG, "main_index ID: " + R.id.main_workspace);

        // 获取底部导航菜单
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // 获取到 UserInfo 的数据
        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        try {
            JSONObject userInfo = new JSONObject(sharedPreferences.getString("userInfo", "{}"));
            Log.d("userInfoResult ===>", userInfo.toString());

            // 1. 先获取 credentialSubject 对象
            JSONObject credentialSubject = userInfo.getJSONObject("credentialSubject");

            // 2. 从 credentialSubject 获取 userType，使用 optString 防止异常
            String role = credentialSubject.optString("userType", "管理员用户");

            // 根据角色加载不同的 Fragment
            if (role.equals("普通用户")) {
                bottomNavigationView.getMenu().removeItem(R.id.main_workspace);
                loadFragment(new ProjectInfoFragment());
            } else {
                loadFragment(new WorkSpaceFragment()); // 如果不是普通用户，加载工作台 Fragment
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON 解析错误: " + e.getMessage());
            loadFragment(new WorkSpaceFragment()); // 加载默认的工作台 Fragment
        }

        // 设置导航菜单点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.main_workspace) {
                    Log.d(TAG, "点击了工作台");
                    loadFragment(new WorkSpaceFragment());
                    return true;
                } else if (item.getItemId() == R.id.main_projectinfo) {
                    Log.d(TAG, "点击了项目");
                    loadFragment(new ProjectInfoFragment());
                    return true;
                } else if (item.getItemId() == R.id.main_agent) {
                    Log.d(TAG, "点击了AiGC");
                    loadFragment(new AiFragment());
                    return true;
                } else if (item.getItemId() == R.id.main_me) {
                    Log.d(TAG, "点击了我");
                    loadFragment(new MeFragment());
                    return true;
                } else if (item.getItemId() == R.id.announcement) {
                    loadFragment(new PublicAnnouncementFragment());
                    Log.d(TAG, "点击了公告");
                    return true;
                }
                return false;
            }
        });

        // 加载默认的首页 Fragment
        if (savedInstanceState == null) {
            loadFragment(new WorkSpaceFragment()); // 默认加载首页
        }
    }

    // 加载 Fragment 的方法
    private void loadFragment(Fragment fragment) {
        Log.d(TAG, "加载 Fragment: " + fragment.getClass().getSimpleName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
