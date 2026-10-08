package com.example.app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.app.Frame.AnnouncementFragment;
import com.example.app.Frame.PublicAnnouncementFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PublicAnnouncementFragment(); // 第一个标签
            case 1:
                return new AnnouncementFragment(); // 第二个标签
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // 返回标签数量
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "挂牌公示";
            case 1:
                return "项目公告";
            default:
                return null;
        }
    }
}
