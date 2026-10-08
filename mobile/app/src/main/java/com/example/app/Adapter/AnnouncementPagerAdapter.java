package com.example.app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.app.Frame.AnnouncementFragment;
import com.example.app.Frame.PublicAnnouncementFragment;

public class AnnouncementPagerAdapter extends FragmentPagerAdapter {

    public AnnouncementPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PublicAnnouncementFragment(); // 挂牌公示
            case 1:
                return new AnnouncementFragment(); // 项目公告
            default:
                return new PublicAnnouncementFragment();
        }
    }

    @Override
    public int getCount() {
        return 2; // 两个页面
    }
}
