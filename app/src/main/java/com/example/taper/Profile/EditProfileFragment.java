package com.example.taper.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taper.R;
import com.example.taper.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditProfileFragment extends Fragment {
    private ImageView mProfilePhoto;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_profile,container,false);
        mProfilePhoto =(ImageView) view.findViewById(R.id.profile_photo);
        //initImageLoader();
        setProfileImage();
        ImageView backarrow=(ImageView) view.findViewById(R.id.backArrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader=new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void setProfileImage(){
        String imgURL="english.mathrubhumi.com/polopoly_fs/1.3305719.1542098400!/image/image.jpg_gen/derivatives/landscape_894_577/image.jpg";
        UniversalImageLoader.setImage(imgURL,mProfilePhoto,null,"https://");
    }
}
