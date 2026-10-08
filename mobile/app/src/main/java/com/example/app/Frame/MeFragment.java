package com.example.app.Frame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.app.Activity.LoginActivity;
import com.example.app.Activity.Me.AssigneeMain;
import com.example.app.Activity.Me.CertificationInfoActivity;
import com.example.testforenv.R;
import com.example.app.Activity.Me.NewCertificationInfoActivity;
import com.example.app.Activity.Me.PropertyInfoActivity;
import com.example.app.Activity.MyCertificateActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * "我的"页面Fragment
 * 功能：展示用户基本信息，提供功能入口
 */
public class MeFragment extends Fragment {

    // 空构造方法（Fragment必须包含）
    public MeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        // 初始化用户信息展示控件
        TextView username = view.findViewById(R.id.username); // 用户名
        TextView accountAddress = view.findViewById(R.id.account_address); // 账户地址
        TextView idNumber = view.findViewById(R.id.id_number); // 身份证号


        // 从SharedPreferences中获取存储信息
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        // 获取 UserInfo
        JSONObject userInfo = null;
        JSONObject credentialSubject = null;
        try {
            // 获取userinfo 对象
            userInfo = new JSONObject(sharedPreferences.getString("userInfo", "{}"));
            // 先获取 credentialSubject 对象
            credentialSubject = userInfo.getJSONObject("credentialSubject");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
//        获取用户类型
        String userType = credentialSubject.optString("userType", "普通用户");

        // 获取 userName
        String UserName = credentialSubject.optString("userName","用户123");
//        获取用户地址
        String UserAccountAddress = credentialSubject.optString("userAddr","0x123456789abcdef");
        // 获取 idNumber
        String UseridNumber = userInfo.optString("idNumber","123456789012345678");
//        判断是否为空
        if (UseridNumber.isEmpty()) UseridNumber = "123456789012345678";

        // 设置用户基本信息（示例数据，实际应来自用户登录信息
        username.setText("用户名: " + UserName);
        accountAddress.setText("账号地址: " + UserAccountAddress);
        idNumber.setText("身份证号: " + UseridNumber);

        // 初始化功能列表ListView
        ListView infoListView = view.findViewById(R.id.info_list);
        // 功能项数组
        String[] infoItems = {
                "我的产权",      // 0 - 查看产权信息
                "我的鉴证书",    // 1 - 查看鉴证书
                "我的凭证",      // 2 - 查看凭证
                "鉴定证书",      // 3 - 进入证书鉴定页面
                "受让受理",      // 4 - 受让受理填写
                "退出"          // 5 - 退出登录
        };

        // 创建列表适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, infoItems);
        infoListView.setAdapter(adapter);

        // 设置列表项点击事件
        infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent; // 用于页面跳转的Intent

                // 根据点击位置跳转到不同页面
                switch (position) {
                    case 0: // 我的产权
                        intent = new Intent(getActivity(), PropertyInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 1: // 我的鉴证书
                        intent = new Intent(getActivity(), CertificationInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 2: // 我的凭证
                        intent = new Intent(getActivity(), MyCertificateActivity.class);
                        startActivity(intent);
                        break;
                    case 3: // 鉴定证书
                        intent = new Intent(getActivity(), NewCertificationInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 4: // 受让受理填写
                        intent = new Intent(getActivity(), AssigneeMain.class);
                        startActivity(intent);
                        break;
                    case 5: // 退出登录
                        // 删除本地存储的凭证文件
                        deleteVCFile();
                        // 跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        // 结束当前Activity（如果是从MainActivity进入）
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        break;
                    default:
                        return; // 其他情况不做处理
                }
            }
        });

        return view; // 返回加载的视图
    }

    /**
     * 删除本地存储的凭证文件
     * 功能：在用户退出登录时清除本地保存的敏感信息
     */
    private void deleteVCFile() {
        // 获取应用私有目录中的 KeyStore/vc.dat 文件路径
        File vcFile = new File(getActivity().getFilesDir(), "KeyStore/vc.dat");

        if (vcFile.exists()) {
            // 尝试删除文件
            if (vcFile.delete()) {
                // 文件删除成功提示
                Toast.makeText(getActivity(), "用户凭证已清除", Toast.LENGTH_SHORT).show();
            } else {
                // 文件删除失败提示
                Toast.makeText(getActivity(), "凭证清除失败，请手动清除", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 文件不存在提示
            Toast.makeText(getActivity(), "无需清除，无保存的凭证", Toast.LENGTH_SHORT).show();
        }
    }
}
