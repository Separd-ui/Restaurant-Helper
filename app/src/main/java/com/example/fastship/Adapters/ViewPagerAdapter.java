package com.example.fastship.Adapters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fastship.Constans;
import com.example.fastship.Cook.ChatFragment;
import com.example.fastship.Cook.MenuFragment;
import com.example.fastship.Cook.OrdersFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,Context context) {
        super(fm, behavior);
        this.context=context;
        SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constans.isCook,true);
        editor.apply();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        if(position==0)
        {
            fragment=new MenuFragment();
        }
        if(position==1)
        {
            fragment=new OrdersFragment();
        }
        if(position==2)
        {
            fragment=new ChatFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if(position==0)
            title="Меню";
        if(position==1)
            title="Заказы";
        if(position==2)
            title="Рабочий чат";
        return title;
    }
}
