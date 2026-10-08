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
import com.google.android.material.textfield.TextInputLayout;
import android.widget.Toast;

public class UserRegisterFragment extends Fragment {

    private EditText idCardEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private TextInputLayout idCardLayout;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        idCardEditText = view.findViewById(R.id.idCard);
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);
        idCardLayout = view.findViewById(R.id.idCardLayout);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        confirmPasswordLayout = view.findViewById(R.id.confirmPasswordLayout);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> performRegister());

        return view;
    }

    private void performRegister() {
        String idCard = idCardEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(idCard) || idCard.length() != 18) {
            idCardLayout.setError("请输入有效的身份证号码");
            return;
        } else {
            idCardLayout.setError(null);
        }

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

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("密码不匹配");
            return;
        } else {
            confirmPasswordLayout.setError(null);
        }

        // 处理注册逻辑
        // TODO: 添加你的注册逻辑，例如调用API等

        Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
        // 注册成功后的处理，例如跳转到登录界面
    }
}
