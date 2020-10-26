package com.example.taper.Utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePageAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList=new ArrayList<>();
    private final HashMap<Fragment,Integer> mFragments= new HashMap<>();
    private final HashMap<String,Integer> mFragmentNumbers=new HashMap<>();
    private final HashMap<Integer,String> mFragmentNames=new HashMap<>();

    public SectionStatePageAdapter(FragmentManager fm){
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment,String fragmentName){
        mFragmentList.add(fragment);
        mFragments.put(fragment,mFragmentList.size()-1);
        mFragmentNumbers.put(fragmentName,mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1,fragmentName);

    }
    public Integer getFragmentNumber(String FragmentName){
        if(mFragmentNumbers.containsKey(FragmentName)){
            return mFragmentNumbers.get(FragmentName);
        }
        else {
            return null;
        }
    }
    public Integer getFragmentNumber(Fragment fragment){
        if(mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        }
        else {
            return null;
        }
    }
    public String getFragmentNumber(Integer fragmentName){
        if(mFragmentNames.containsKey(fragmentName)){
            return mFragmentNames.get(fragmentName);
        }
        else {
            return null;
        }
    }
}
