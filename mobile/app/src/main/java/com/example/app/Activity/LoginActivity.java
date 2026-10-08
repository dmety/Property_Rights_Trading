package com.example.app.Activity; // 替换为你的包名

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.RadioGroup;

import com.example.testforenv.R;
import com.example.app.Frame.Login.UserLoginFragment;
import com.example.app.Frame.Login.UserRegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private RadioGroup userTypeGroup;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userTypeGroup = findViewById(R.id.userTypeGroup);
        fragmentManager = getSupportFragmentManager();

        // 默认加载登录 fragment
        loadFragment(new UserLoginFragment());

        // 设置用户类型的选择监听器
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment fragment;
            if (checkedId == R.id.systemUser) {
                fragment = new UserLoginFragment();
            } else {
                fragment = new UserRegisterFragment();
            }
            loadFragment(fragment);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.loginFragmentContainer, fragment);
        transaction.commit();
    }
}
