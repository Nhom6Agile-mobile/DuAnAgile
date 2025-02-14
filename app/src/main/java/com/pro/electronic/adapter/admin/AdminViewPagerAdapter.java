package com.pro.electronic.adapter.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.electronic.fragment.admin.AdminCategoryFragment;
import com.pro.electronic.fragment.admin.AdminOrderFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AdminProductFragment();

            case 2:
                return new AdminOrderFragment();

            case 3:
                return new AdminSettingsFragment();

            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
