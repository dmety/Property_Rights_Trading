package com.example.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.testforenv.R;


public class CardViewComponent extends CardView {

    private ImageView cardImage;

    public CardViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.card_me_certification_image, this, true);

        // 获取 ImageView
        cardImage = findViewById(R.id.cardImage);
    }

    // 设置图片资源
    public void setCardImage(int resId) {
        cardImage.setImageResource(resId);
    }

    // 设置图片 URL
    public void setCardImageUrl(String imageUrl) {
        Glide.with(getContext()).load(imageUrl).into(cardImage);
    }
}
