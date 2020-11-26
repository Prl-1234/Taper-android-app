package com.example.taper.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.taper.Profile.AccountSettingActivity;
import com.example.taper.R;
import com.example.taper.Utils.FileSearch;
import com.example.taper.Utils.GridImageAdapter;
import com.example.taper.Utils.PathFiles;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;

public class Gallery_fragment extends Fragment {
    private GridView gridView;
    private static final int NUM_GRID_COLMUNS=3;
    private ImageView galleryImage;
    private String mAppend="file:/";
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    private ArrayList<String> directories;
    private int selected=0;
    private ArrayList<String> directory;
    private String mselectedImage;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_gallery,container,false);
        selected=0;
        galleryImage=(ImageView) view.findViewById(R.id.galleryImageView);
        gridView=(GridView) view.findViewById(R.id.gridview_g);
        directorySpinner=(Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar=(ProgressBar) view.findViewById(R.id.progress__bar);
        directories=new ArrayList<>();
        mProgressBar.setVisibility(View.GONE);
        ImageView shareClose=(ImageView) view.findViewById(R.id.icCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        TextView nextScreen=(TextView) view.findViewById(R.id.toNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected==1){
                    if(isRightTask()){
                        Intent intent=new Intent(getActivity(),NextActivity.class);
                        intent.putExtra(getString(R.string.selected_image),mselectedImage);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(getActivity(), AccountSettingActivity.class);
                        intent.putExtra(getString(R.string.selected_image),mselectedImage);
                        intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Select an image", Toast.LENGTH_SHORT).show();
                }


            }
        });
        init();
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
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void init(){
        PathFiles pathFiles=new PathFiles();
        if(FileSearch.getDirectoryPaths(pathFiles.PICTURES)!=null){
            directories=FileSearch.getDirectoryPaths(pathFiles.PICTURES);

        }

//        if(FileSearch.getDirectoryPaths(pathFiles.ROOT_DIR)!=null){
//            directories=FileSearch.getDirectoryPaths(pathFiles.ROOT_DIR);
//
//        }
        directories.add(pathFiles.CAMERA);
      //  directories.add(pathFiles.pic);
       // directories.add(pathFiles.ROOT);
       // directories.add(pathFiles.pl);
       // directories.add(pathFiles.nk);
        ArrayList<String> directoryName=new ArrayList<>();
        for(int j =0;j<directories.size();j++){
            int index=directories.get(j).lastIndexOf("/");
            String string =directories.get(j).substring(index).replace("/"," ");
            directoryName.add(string);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,directoryName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupGridView(directories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private void setupGridView(String selected_directory){
        final ArrayList<String> imgURLs= FileSearch.getFilePaths(selected_directory);
        Collections.reverse(imgURLs);
        int gridWidth=getResources().getDisplayMetrics().widthPixels;
        int imageWidth=gridWidth/NUM_GRID_COLMUNS;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter=new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,mAppend,imgURLs);
        gridView.setAdapter(adapter);
//        try {
//
//            setImage(imgURLs.get(0),galleryImage,mAppend);
//            mselectedImage =imgURLs.get(0);
//        }catch (ArrayIndexOutOfBoundsException e){
//
//        }
        if(imgURLs.size()>0){
            setImage(imgURLs.get(0),galleryImage,mAppend);
//            mselectedImage =imgURLs.get(0);
        }

    //  setImage(imgURLs.get(0),galleryImage,mAppend);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setImage(imgURLs.get(i),galleryImage,mAppend);
                mselectedImage =imgURLs.get(i);
            }
        });
    }
    private void setImage(String imgURL,ImageView image,String append){
        selected=1;
        ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                //Toast.makeText(getActivity(), "start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
               // Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
               // Toast.makeText(getActivity(), "Complete", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
                //Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
