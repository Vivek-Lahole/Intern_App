package com.farmigo.app.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.farmigo.app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/* @authorVicek Lahole */


public class MandiFragment1 extends Fragment {

    private String[] manageTitles = new String[]{"My Mandis","Mandis Near me","Crop View" };
    private ViewPager2 mPager;
    private ManageMandisViewPagerAdapter manageMandisViewPagerAdapter;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mandi1, container, false);

        mPager=view.findViewById(R.id.ViewPager);
        tabLayout=view.findViewById(R.id.TabLayout);

        init();
        return view;
    }

    private void init() {

        manageMandisViewPagerAdapter=new ManageMandisViewPagerAdapter(this);
        mPager.setAdapter(manageMandisViewPagerAdapter);
        mPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout,
                mPager, ((tab, position) ->
                tab.setText(manageTitles[position])
        )).attach();
    }

    private class ManageMandisViewPagerAdapter extends FragmentStateAdapter {

        public ManageMandisViewPagerAdapter(@NonNull Fragment fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            SharedPreferences sharedPreferences = getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            switch (position) {
//                case 0:
//                    Log.d("position", "createFragment 0: "+position);
//                    return new CropViewFragment();
                case 0:
                    myEdit.putBoolean("check",true);
                    myEdit.commit();
                    Log.d("position", "createFragment 1: "+position);
                    return new MandiFragment();
                case 1:
                    myEdit.putBoolean("check",false);
                    myEdit.commit();
                    Log.d("position", "createFragment 2: "+position);
                    return new MandiFragment();
            }
            return new CropViewFragment();
        }

        @Override
        public int getItemCount() {
            return manageTitles.length;
        }
    }
}

