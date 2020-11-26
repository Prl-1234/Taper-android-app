package com.example.taper.Utils;

import android.media.Image;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taper.Models.Photo;
import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.R;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.FirebaseMethods;
import com.example.taper.Utils.GridImageAdapter;
import com.example.taper.Utils.SquareImageView;
import com.example.taper.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private FirebaseMethods mfirebaseMethods;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView mBackLabel,mCaption,mTimeStamp,mUsername;
    private ImageView mBackArrow,mEllipses,mHeartRed,mHeartWhite,mProfileImage;
    private Photo mphoto;
    private int mActivityNumber=0;
    private String photo_Username;
    private String photoUrl;
    private UserAccountSetting muser;
    private SquareImageView mPostImage;
    private GestureDetector mgestureDetector;
    private Heart mheart;
    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_view_post,container,false);
        mPostImage=(SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationViewEx=(BottomNavigationViewEx) view.findViewById(R.id.bottom_Nav_View);
        mBackArrow=(ImageView) view.findViewById(R.id.backArrow_post);
        mBackLabel=(TextView) view.findViewById(R.id.tvBackLabel);
        mCaption=(TextView) view.findViewById(R.id.image_caption);
        mUsername=(TextView) view.findViewById(R.id.username);
        mTimeStamp=(TextView)view.findViewById(R.id.image_time_posted);
        mEllipses=(ImageView) view.findViewById(R.id.ivEllipses);
        mHeartRed=(ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite=(ImageView) view.findViewById(R.id.image_heart);
        mProfileImage=(ImageView) view.findViewById(R.id.profile_pphoto);
        mHeartRed.setVisibility(View.GONE);
        mHeartWhite.setVisibility(View.VISIBLE);
        mheart=new Heart(mHeartWhite,mHeartRed);
        mgestureDetector=new GestureDetector(getActivity(),new GestureListener());
        try {
              mphoto=getPhotoFromBuncdle();
            UniversalImageLoader.setImage(mphoto.getImage_path(),mPostImage,null,"");
            mActivityNumber=getActivityNumberFromBundle();
        }catch(NullPointerException e){

        }
        setUpFirebase();
        setup_bottom_navigation();
        getPhotoDetails();
        testToggle();
        return view;
    }
    private void getPhotoDetails(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mphoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnaphot:snapshot.getChildren()) {
                    muser=singleSnaphot.getValue(UserAccountSetting.class);
                }
                setUpWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
           // mheart.toggleLike();
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
            Query query=reference
                    .child(getString(R.string.photo))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mphoto.getUser_id());
            return true;
        }
    }
    private void testToggle(){
        mHeartRed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mgestureDetector.onTouchEvent(motionEvent);
            }
        });
        mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mgestureDetector.onTouchEvent(motionEvent);
            }
        });
    }
    private void setUpWidgets(){
        String timestampDiff=getTimeStampDifference();
        //Toast.makeText(getActivity(), timestampDiff, Toast.LENGTH_SHORT).show();
        if(!timestampDiff.equals("0")){
            mTimeStamp.setText(timestampDiff+" DAYS AGO");
        }
        else{
            mTimeStamp.setText("TODAY");

        }
        try{
            UniversalImageLoader.setImage(muser.getProfile_photo(),mProfileImage,null,"");
            mUsername.setText(muser.getUsername());
            mCaption.setText(mphoto.getCaption());
        }catch (Exception e) {

        }

    }
    private String getTimeStampDifference(){
        String difference="";
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today=c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp=mphoto.getData_created();
        try {
            timestamp=sdf.parse(photoTimestamp);
            difference=String.valueOf(Math.round(((today.getTime()-timestamp.getTime())/1000/60/60/24)));
        }catch (ParseException e){
            difference="0";
        }
        return difference;
    }
    private int getActivityNumberFromBundle(){
        Bundle bundle=this.getArguments();
        if(bundle!=null){
            return bundle.getInt(getString(R.string.activity_number));
        }
        else{
            return 0;
        }
    }
    private Photo getPhotoFromBuncdle(){
        Bundle bundle=this.getArguments();
        if(bundle!=null){
                return bundle.getParcelable(getString(R.string.photo));
        }
        else{
            return null;
        }
    }
    private void setup_bottom_navigation(){

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(getActivity(),getActivity(),bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(mActivityNumber);
        // menuItem.setChecked(true);
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
