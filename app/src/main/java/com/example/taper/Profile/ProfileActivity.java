package com.example.taper.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.taper.R;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.GridImageAdapter;
import com.example.taper.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG="SearchAcitivity";
    private static final int Activity_num=4;
    private ProgressBar mProgressBar;
    private Context mContext=ProfileActivity.this;
    private ImageView profilephoto;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        mProgressBar=(ProgressBar) findViewById(R.id.profile_progress);
//        mProgressBar.setVisibility(View.GONE);
        setup_bottom_navigation();
        setUpToolBar();
        setUpAcitivityWidgets();
        setProfileImage();
        tempGridSetup();
    }
    private void tempGridSetup(){
        ArrayList<String> imgURLs=new ArrayList<>();
        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");

        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");

        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");

        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");

        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");

        imgURLs.add("https://www.kolpaper.com/wp-content/uploads/2020/05/Moon-Street-Iphone-Wallpaper.jpg");
        setUpImageGrid(imgURLs);
    }
    private void setUpImageGrid(ArrayList<String> imgURLs){
        GridView gridView=(GridView) findViewById(R.id.gridview);
        GridImageAdapter adapter=new GridImageAdapter(mContext,R.layout.layout_grid_imageview,"",imgURLs);
        gridView.setAdapter(adapter);
    }
    private void setProfileImage(){
        String imgURL="english.mathrubhumi.com/polopoly_fs/1.3305719.1542098400!/image/image.jpg_gen/derivatives/landscape_894_577/image.jpg";
        UniversalImageLoader.setImage(imgURL,profilephoto,mProgressBar,"https://");
    }
    private void setUpAcitivityWidgets(){
          mProgressBar=(ProgressBar) findViewById(R.id.profile_progress);
          mProgressBar.setVisibility(View.GONE);
          profilephoto=(ImageView) findViewById(R.id.profile_image);
    }
    private  void setUpToolBar(){
        Toolbar toolbar=(Toolbar) findViewById(R.id.profiletoolbar);
        setSupportActionBar(toolbar);
        ImageView profileMenu=(ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,AccountSettingActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setup_bottom_navigation(){
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottom_Nav_View);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(ProfileActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(Activity_num);
       // menuItem.setChecked(true);
    }


}
