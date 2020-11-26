package com.example.taper.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taper.Profile.AccountSettingActivity;
import com.example.taper.R;
import com.example.taper.Share.ShareActivity;
import com.example.taper.Utils.Permissions;

public class Photo_fragment extends Fragment {
    private static final int PHOTO_FRAGMENT_APP=1;
    private static final int GALLERY_FRGMENT_APP=0;
    private static final int CAMERA_REQUEST_CODE=5;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_photo,container,false);

        Button btnLaunchCamera=(Button) view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((ShareActivity)getActivity()).getCurrentTabNumber()==PHOTO_FRAGMENT_APP){
                    if(((ShareActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])){
                        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }
                    else{
                        Intent intent=new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }
    private boolean isRightTask(){
        if(((ShareActivity)getActivity()).getTask()==0){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_REQUEST_CODE){
            Bitmap bitmap;
            bitmap=(Bitmap) data.getExtras().get("data");
            if(isRightTask()){
                try{
                    Intent intent=new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    startActivity(intent);
                }catch (NullPointerException e){

                }
            }else{
                try{
                    Intent intent=new Intent(getActivity(), AccountSettingActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }catch (NullPointerException e){

                }
            }
        }
    }
}
