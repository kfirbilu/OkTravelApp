package com.example.oktravelapplictaion;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class TabsSearchFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabs_search, container, false);
        tabLayout = view.findViewById(R.id.tab_titles);
        viewPager = view.findViewById(R.id.viewpager_tabs);

        tabLayout.setupWithViewPager(viewPager);
        TabsAdapter adapter = new TabsAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new SearchLocationsFragment(), "Locations");
        viewPager.setAdapter(adapter);
        return view;
    }

    public class TabsAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        private ArrayList<String> fragmentTitle = new ArrayList<>();

        public TabsAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentArrayList.add(fragment);
            fragmentTitle.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}