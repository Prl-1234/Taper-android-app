package com.example.taper.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {
    public ImageView heartWhite,heartRed;
    private static  final DecelerateInterpolator DECCELERTAE_INTERPOLAR=new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR=new AccelerateInterpolator();

    public Heart(ImageView heartWhite, ImageView heartRed) {
        this.heartWhite = heartWhite;
        this.heartRed = heartRed;
    }
    public void toggleLike(){
        AnimatorSet animationSet=new AnimatorSet();

        if(heartRed.getVisibility()== View.VISIBLE){
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator scaleDownY=ObjectAnimator.ofFloat(heartRed,"scaleY",1f,0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator scaleDownX=ObjectAnimator.ofFloat(heartRed,"scaleX",1f,0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);
            animationSet.playTogether(scaleDownY,scaleDownX);

        }
        else if(heartRed.getVisibility()== View.GONE){
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);
            ObjectAnimator scaleDownY=ObjectAnimator.ofFloat(heartRed,"scaleY",0.1f,1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERTAE_INTERPOLAR);
            ObjectAnimator scaleDownX=ObjectAnimator.ofFloat(heartRed,"scaleX",0.1f,1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERTAE_INTERPOLAR);

            heartRed.setVisibility(View.VISIBLE);
            heartWhite.setVisibility(View.GONE);
            animationSet.playTogether(scaleDownY,scaleDownX);

        }
        animationSet.start();

    }
}
