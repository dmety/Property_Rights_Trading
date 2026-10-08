package com.example.app.Frame.Login; // 替换为你的包名

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.testforenv.R;
import com.example.app.Utils.OkHttpUtil;
import com.google.android.material.textfield.TextInputLayout;

public class UserLoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private Button loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> performLogin());

        return view;
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("用户名不能为空");
            return;
        } else {
            usernameLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("密码不能为空");
            return;
        } else {
            passwordLayout.setError(null);
        }
        // 处理登录逻辑
        // TODO: 添加登录逻辑，例如调用API等
        // 用户免密登录前提是本地已存在证明凭证VC和用户私钥
        OkHttpUtil.androidLogin(username,password,requireContext());
//        SharedPreferences sharedPreferences = getSharedPreferences("YourPrefsName", MODE_PRIVATE);
        // 登录成功后的处理，例如跳转到主界面
    }
}
