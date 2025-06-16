package com.example.medi_ai;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainFragmentAdapter extends FragmentStateAdapter {

    public MainFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Fragment_1();
            case 1: return new Fragment_2();
            case 2: return new Fragment_3();
            default: return new Fragment_1();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 총 프래그먼트 수
    }
}