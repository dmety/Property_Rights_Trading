package com.example.app.Frame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.DIDUtils;
import com.example.app.Utils.KeyUtils;
import com.example.testforenv.R;
import com.example.app.Frame.WorkSpace.DoneFragment;
import com.example.app.Frame.WorkSpace.ToDoneFragment;
import com.example.app.Utils.OkHttpUtil;
import com.google.android.material.tabs.TabLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkSpaceFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView scanIcon; // 扫码图标

    // 定义 ActivityResultLauncher
    private final ActivityResultLauncher<Intent> scanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent data = result.getData();
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);
                    if (intentResult != null) {
                        if (intentResult.getContents() == null) {
                            // 用户取消扫码
                            Toast.makeText(getActivity(), "扫码取消", Toast.LENGTH_SHORT).show();
                        } else {
                            // 处理扫码结果
                            String scannedData = intentResult.getContents();
                            if (scannedData != null && scannedData.contains("qrcode")) {
                                Log.d("WorkSpaceFragment", "scannedData: " + scannedData);
                                try {
                                    // 发送状态0 表示已经扫码
                                    OkHttpUtil.QRCodeLogin(scannedData, "0", getContext());

                                    // 创建并显示对话框
                                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                    builder.setTitle("您确定要授权该设备登录吗？");
                                    builder.setPositiveButton("确定", (dialog, which) -> {
                                        try {
                                            // 发送状态1 表示确认授权登录
                                            OkHttpUtil.QRCodeLogin(scannedData, "1", getContext());
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                    // 确保调用show()方法显示对话框
                                    builder.show();

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else if ((scannedData != null && scannedData.contains("review"))) {
                                Log.d("WorkSpaceFragment", "scannedData: " + scannedData);
                                JSONObject requestBeScan = new JSONObject();
                                try {
                                    requestBeScan.put("status", "0");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                // 发送 status=0 请求
                                OkHttpUtil.sendJsonPostRequest(scannedData, requestBeScan.toString(), new OkHttpUtil.OkHttpCallback() {
                                    @Override
                                    public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
                                        requireActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 请求成功，弹出确认对话框
                                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                                builder.setTitle("您确定要授权审核该项目吗？");
                                                builder.setPositiveButton("确定", (dialog, which) -> {
                                                    File directory = new File(getActivity().getFilesDir(), "KeyStore");
                                                    File vcFile = new File(directory, "vc.dat");
                                                    File privateKey = new File(directory, "privateKey.dat");
                                                    Log.d("VCQueryResult ===>" ,readFileContent(vcFile));
                                                    JSONObject requestReview = new JSONObject();
                                                    try {
                                                        String vcContent = KeyUtils.decrypt(readFileContent(privateKey),readFileContent(vcFile));
                                                        requestReview.put("status", "1");
                                                        requestReview.put("vp", DIDUtils.generateVP(vcContent   ));
                                                    } catch (Exception e) {
                                                        throw new RuntimeException(e);
                                                    }


                                                    // 用户确认后，发送 status=1 请求
                                                    OkHttpUtil.sendJsonPostRequest(scannedData, requestReview.toString(), new OkHttpUtil.OkHttpCallback() {
                                                        @Override
                                                        public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
                                                            // 审核成功后的操作
                                                            Log.d("WorkSpaceFragment", "Authorization successful");
                                                            return null;
                                                        }

                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            Log.e("WorkSpaceFragment", "Authorization failed: " + e.getMessage());
                                                        }

                                                        @Override
                                                        public void onError(String error) {
                                                            Log.e("WorkSpaceFragment", "Error occurred: " + error);
                                                        }
                                                    });
                                                });

                                                builder.setNegativeButton("取消", null);  // 用户取消操作
                                                builder.show();
                                            }
                                        });

                                        // 改变状态 0 成功
                                        return null;
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("WorkSpaceFragment", "Request failed: " + e.getMessage());
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.e("WorkSpaceFragment", "Error occurred: " + error);
                                    }
                                });
                            }


                            // 发送请求并处理响应
//                                OkHttpUtil.sendGetRequest(scannedData + "?status=0", new OkHttpUtil.OkHttpCallback() {
//                                    @Override
//                                    public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
//                                        // 在主线程上执行显示对话框的操作
//                                        requireActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                                                builder.setTitle("您确定要授权审核该项目吗？");
//                                                builder.setPositiveButton("确定", (dialog, which) -> {
//                                                    try {
//                                                        // 发送状态1 表示确认授权登录
//                                                        OkHttpUtil.sendGetRequest(scannedData + "?status=1", new OkHttpUtil.OkHttpCallback() {
//                                                            @Override
//                                                            public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
//                                                                // 可以在此处理审核成功后的操作
//                                                                return null;
//                                                            }
//
//                                                            @Override
//                                                            public void onFailure(Exception e) {
//                                                                // 处理请求失败的情况
//                                                            }
//
//                                                            @Override
//                                                            public void onError(String error) {
//                                                                // 处理请求出错的情况
//                                                            }
//                                                        });
//                                                    } catch (Exception e) {
//                                                        throw new RuntimeException(e);
//                                                    }
//                                                });
//                                                builder.show();
//                                            }
//                                        });
//                                        return null;
//                                    }
//
//                                    @Override
//                                    public void onFailure(Exception e) {
//                                        // 处理请求失败的情况
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        // 处理请求出错的情况
//                                    }
//                                });



//                                OkHttpUtil.sendGetRequest(scannedData+"?status=0", new OkHttpUtil.OkHttpCallback() {
//                                    @Override
//                                    public void onSuccess(String response) {
//                                        getActivity().runOnUiThread(() -> {
////                                        Toast.makeText(getActivity(), "GET 请求成功: " + response, Toast.LENGTH_SHORT).show();
////                                        Log.d("OkHttpUtil", "GET 请求成功: " + response);
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                                            builder.setTitle("您确定要授权该设备登录吗？");
//                                            builder.setPositiveButton("确定", (dialog, which) -> {
//                                                        OkHttpUtil.sendGetRequest(scannedData+"?status=1&privateKey=11111", new OkHttpUtil.OkHttpCallback() {
//                                                            @Override
//                                                            public void onSuccess(String response) {
//                                                                getActivity().runOnUiThread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//                                                            }
//
//                                                            @Override
//                                                            public void onFailure(Exception e) {
//
//                                                            }
//
//                                                            @Override
//                                                            public void onError(String error) {
//
//                                                            }
//                                                        });
//                                                    }
//                                            );
//                                            builder.setNegativeButton("取消", null);
//                                            builder.show();
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onFailure(Exception e) {
//                                        getActivity().runOnUiThread(() -> {
//                                            Toast.makeText(getActivity(), "GET 请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            Log.d("OkHttpUtil", "GET 请求失败: " + e.getMessage());
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        getActivity().runOnUiThread(() -> {
//                                            Toast.makeText(getActivity(), "GET 请求错误: " + error, Toast.LENGTH_SHORT).show();
//                                            Log.d("OkHttpUtil", "GET 请求错误: " + error);
//                                        });
//                                    }
//                                });
//                            } else if (scannedData != null && scannedData.contains("login")) {
//
//                            } else if (scannedData != null && scannedData.contains("review")){
//
//                            }

                            // 在这里处理扫码结果，例如显示对话框等
                        }
                    }
                }
            }
    );
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workspace, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        scanIcon = view.findViewById(R.id.scanIcon); // 获取扫码图标的引用

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // 扫码图标的点击事件
        scanIcon.setOnClickListener(v -> {
            // 启动二维码扫描
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setOrientationLocked(true);  // 锁定竖屏
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // 只扫描二维码
            integrator.setPrompt("请对准二维码进行扫描");  // 扫码界面的提示文本
            scanLauncher.launch(integrator.createScanIntent()); // 启动扫码
        });

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new ToDoneFragment(), "待审");
        adapter.addFragment(new DoneFragment(), "已审");
//        adapter.addFragment(new DoneFragment(), "测试");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
