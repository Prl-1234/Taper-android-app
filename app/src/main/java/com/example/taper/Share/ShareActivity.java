package com.example.taper.Share;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.taper.R;
import com.example.taper.Search.Gallery_fragment;
import com.example.taper.Search.Photo_fragment;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.Permissions;
import com.example.taper.Utils.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG="ShareAcitivity";
    private static final int Activity_num=3;
    private static final int VERIFY_PERMISSION_REQUEST=1;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setViewPager();
        }
        else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
       // setup_bottom_navigation();
    }
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }
    public void setViewPager(){
        SectionPagerAdapter adapter=new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Gallery_fragment());
        adapter.addFragment(new Photo_fragment());
        mViewPager=(ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }
    public void verifyPermissions(String[] permissions){
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSION_REQUEST
        );
    }
    public boolean checkPermissionsArray(String[] permissions){
        for(int i=0;i<permissions.length;i++){
            String check=permissions[i];
            if(!checkPermissions(check)){
                return false;
            }

        }
        return true;
    }

    public boolean checkPermissions(String permission){
        int permissionRequest= ActivityCompat.checkSelfPermission(ShareActivity.this,permission);
        if(permissionRequest!= PackageManager.PERMISSION_GRANTED){
            return false;
        }
        else{
            return true;
        }
    }
    private void setup_bottom_navigation(){
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottom_Nav_View);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(ShareActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }
}
