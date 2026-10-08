package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Utils.DIDUtils;
import com.example.testforenv.R;
import com.example.app.Activity.IndexActivity;
import com.example.app.Activity.LoginActivity;
import com.example.app.Utils.KeyUtils;
import com.example.app.Utils.OkHttpUtil;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private int[] images;
    private static final String VC_FILE_NAME = "vc.dat";
    private static final String PRIVATE_KEY_FILE_NAME = "privateKey.dat";
    private Context context;

    public ImageAdapter(int[] images,Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        setupBouncyCastle();
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);

        // 判断是否为第二张图片
        if (position == 1) {
            holder.enterAppButton.setVisibility(View.VISIBLE);
            holder.enterAppButton.setOnClickListener(v -> {
                // 读取文件内容
                File directory = new File(holder.itemView.getContext().getFilesDir(), "KeyStore");
                File vcFile = new File(directory, VC_FILE_NAME);
                File privateKeyFile = new File(directory, PRIVATE_KEY_FILE_NAME);
                if (!directory.exists()) {
                    directory.mkdirs(); // 创建目录
                }

                // 检查 vc.txt 文件是否存在
                if (!vcFile.exists() || vcFile.length() == 0) {
                    // 文件不存在或没有内容，进入 LoginActivity
                    Intent intent = new Intent(holder.itemView.getContext(), LoginActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                    return; // 结束当前方法，避免继续执行后续代码
                }

                // 读取 vc.txt 的内容
                String vcContent = readFileContent(vcFile);
                Log.d("vcContent", vcContent);
                try {
                    // 读取 privateKey.txt 的内容
                    String privateKeyContent = readFileContent(privateKeyFile);
                    if (privateKeyContent.isEmpty()) {
                        Log.e("TAG", "Private key file is empty.");
                        // 处理私钥文件为空的情况
                        return;
                    }
                    // 解密 vcContent
                    String decryptedContent = KeyUtils.decrypt(privateKeyContent,vcContent );
                    Log.d("Decrypted Content", decryptedContent);
                    OkHttpUtil.loginWithOutPass(decryptedContent, holder.itemView.getContext());
                } catch (Exception e) {
                    Log.e("TAG", "Decryption error: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            holder.enterAppButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button enterAppButton;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            enterAppButton = itemView.findViewById(R.id.enterAppButton);
        }
    }
    public void setupBouncyCastle() {
        // 为安卓端设置BouncyCastle的初始化
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
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
}
