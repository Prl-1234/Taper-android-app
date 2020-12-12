package com.example.taper.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taper.R;
import com.example.taper.Utils.FirebaseMethods;
import com.example.taper.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class
NextActivity extends AppCompatActivity {
    private int imageCount=0;
    private FirebaseAuth mAuth;
    private String mAppend="file:/";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private FirebaseMethods mfirebaseMethods;
    private String imgUrl;
    private EditText mCaption;
    private Intent intent;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mfirebaseMethods=new FirebaseMethods(NextActivity.this);
        mCaption=(EditText) findViewById(R.id.description_next);
        ImageView backarrow=(ImageView) findViewById(R.id.icbackshare);

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView share=(TextView) findViewById(R.id.toShare);
        share.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {
                Toast.makeText(NextActivity.this, "Sharing", Toast.LENGTH_SHORT).show();
                String caption=mCaption.getText().toString();
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl=intent.getStringExtra(getString(R.string.selected_image));
                    mfirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,imageCount,imgUrl,null);

                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap=(Bitmap)intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mfirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,imageCount,null,bitmap);

                }

            }
        });
        setImage();
        setUpFirebase();
    }
    private void subMethod(){

        //Create datamodel for photos
        //Add properties to the Photo object(caption)
        //Count the number of photos thaht user already has
        //upload the photo to firebase
        //insert into photos
        //insert into user_photos

    }
    private void setImage(){
        intent=getIntent();
        ImageView image=(ImageView) findViewById(R.id.imageshare);
        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl=intent.getStringExtra(getString(R.string.selected_image));
            UniversalImageLoader.setImage(intent.getStringExtra(getString(R.string.selected_image)),image,null,mAppend);

        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap=(Bitmap)intent.getParcelableExtra(getString(R.string.selected_bitmap));
            image.setImageBitmap(bitmap);

        }
    }
    private void setUpFirebase(){
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myref=mFirebaseDatabase.getReference();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();

                if(user!=null){

                }
                else{

                }
            }
        };
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageCount=mfirebaseMethods.getImageCount(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
}
