package com.example.taper.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.taper.Models.Photo;
import com.example.taper.R;
import com.example.taper.Utils.ViewCommentsFragment;
import com.example.taper.Utils.ViewPostFragment;
import com.example.taper.Utils.ViewProfileFragment;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener
{
    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        ViewCommentsFragment fragment=new ViewCommentsFragment();
        Bundle args=new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        fragment.setArguments(args);

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();
    }
    private static final String TAG="SearchAcitivity";
    private static final int Activity_num=4;
    private ProgressBar mProgressBar;
    private static final int NUM_GRID_COL=3;
    private Context mContext=ProfileActivity.this;
    private ImageView profilephoto;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //setContentView(R.layout.fragment_profile);
        init();
//        mProgressBar=(ProgressBar) findViewById(R.id.profile_progress);
//        mProgressBar.setVisibility(View.GONE);
//        setup_bottom_navigation();
//        setUpToolBar();
//        setUpAcitivityWidgets();
//        setProfileImage();
//        tempGridSetup();
    }
    private void init(){
        Intent intent=getIntent();
        if(intent.hasExtra(getString(R.string.calling_activity))){
            if(intent.hasExtra(getString(R.string.intent_user))){
                ViewProfileFragment fragment=new ViewProfileFragment();
                Bundle args=new Bundle();
                args.putParcelable(getString(R.string.intent_user),intent.getParcelableExtra(getString(R.string.intent_user)));
                fragment.setArguments(args);

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container,fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();
            }else{
                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction=ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }

    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        ViewPostFragment fragment=new ViewPostFragment();
        Bundle args=new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_number),activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }



}
