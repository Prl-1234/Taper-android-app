
package com.example.taper.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.taper.R;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.SectionPagerAdapter;
import com.example.taper.Utils.UniversalImageLoader;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="HomeActivity";
    private static final int Activity_num=0;
    private Context mcontext=MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        setup_bottom_navigation();
        setUpViewPager();
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader=new UniversalImageLoader(mcontext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void setUpViewPager(){
        SectionPagerAdapter adapter=new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Camera_fragment());
        adapter.addFragment(new Home_fragment());
        adapter.addFragment(new Message_fragment());
        ViewPager viewPager=(ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_house);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }
    private void setup_bottom_navigation(){
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottom_Nav_View);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(MainActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }
}