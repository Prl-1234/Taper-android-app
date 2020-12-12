package com.example.taper.Utils;

import android.content.Context;
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

import com.example.taper.Models.Comment;
import com.example.taper.Models.Likes;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment {
    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Photo photo);
    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private FirebaseMethods mfirebaseMethods;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView mBackLabel,mCaption,mTimeStamp,mUsername,mLikes,mComments;
    private ImageView mComment,mBackArrow,mEllipses,mHeartRed,mHeartWhite,mProfileImage;
    private Photo mphoto;
    private int mActivityNumber=0;
    private String photo_Username;
    private String photoUrl;
    private UserAccountSetting muser;
    private SquareImageView mPostImage;
    private GestureDetector mgestureDetector;
    private Heart mheart;
    private User mCurrentuser;
    private Boolean mLikedByCurrentUser;
    private  StringBuilder mUsers;
    private String mLikesString="";
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
        mLikes=(TextView) view.findViewById(R.id.imagelikes);
        mProfileImage=(ImageView) view.findViewById(R.id.profile_pphoto);
        mComment=(ImageView) view.findViewById(R.id.speech_bubble);
        mComments=(TextView) view.findViewById(R.id.image_comments_link);
        mheart=new Heart(mHeartWhite,mHeartRed);
        mgestureDetector=new GestureDetector(getActivity(),new GestureListener());

        setUpFirebase();
        setup_bottom_navigation();
      //  testToggle();
        return view;
    }
    private void init(){
        try {
            //    mphoto=getPhotoFromBuncdle();
            UniversalImageLoader.setImage(getPhotoFromBuncdle().getImage_path(),mPostImage,null,"");
            mActivityNumber=getActivityNumberFromBundle();
            String photo_id=getPhotoFromBuncdle().getPhoto_id();
            Query query=FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.dbname_photos))
                    .orderByChild(getString(R.string.field_photo_id))
                    .equalTo(photo_id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot singleSnapshot:snapshot.getChildren()){
                        Photo newPhoto=new Photo();
                        Map<String,Object> objectMap=(HashMap<String, Object>) singleSnapshot.getValue();

                        newPhoto.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                        newPhoto.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newPhoto.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        newPhoto.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newPhoto.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        newPhoto.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        List<Comment> commentList=new ArrayList<Comment>();
                        for(DataSnapshot dSnapshot: singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment=new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentList.add(comment);
                        }
                        newPhoto.setComments(commentList);
                        mphoto=newPhoto;
                        getCurrrentUser();
                        getPhotoDetails();
                        // getLikesString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }catch(NullPointerException e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isAdded()){
            init();
        }
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
               // setUpWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getLikesString(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers=new StringBuilder();
                for(DataSnapshot singlesnapshot: snapshot.getChildren()){
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
                    Query query=reference
                            .child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singlesnapshot.getValue(Likes.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");


                            }
                            String[] splitUsers=mUsers.toString().split(",");
                            if(mUsers.toString().contains(mCurrentuser.getUsername()+",")){
                                mLikedByCurrentUser=true;
                            }
                            else{
                                mLikedByCurrentUser=false;
                            }
                            int length=splitUsers.length;
                            if(length==1){
                                mLikesString="Liked by "+splitUsers[0];
                            }
                            else if(length==2){
                                mLikesString="Liked by "+splitUsers[0]+" and "+splitUsers[1];

                            }
                            else if(length==3){
                                mLikesString="Liked by "+splitUsers[0]+", "+splitUsers[1]+" and "+splitUsers[2];
                            }
                            else if(length==4){
                                mLikesString="Liked by "+splitUsers[0]+", "+splitUsers[1]+", "+splitUsers[2]+" and "+splitUsers[3];
                            }
                            else if(length>4){
                                mLikesString="Liked by "+splitUsers[0]+", "+splitUsers[1]+", "+splitUsers[2]+" and "+(splitUsers.length-3)+" others";

                            }
                            setUpWidgets();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                if(!snapshot.exists()){
                    mLikesString="";
                    mLikedByCurrentUser=false;
                    setUpWidgets();
                }
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
                    .child(getString(R.string.dbname_photos))
                    .child(mphoto.getPhoto_id())
                    .child(getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot singlesnapshot: snapshot.getChildren()){
                        //case 1: Then user laready liked the photo
                        String keyID=singlesnapshot.getKey();
                        if(mLikedByCurrentUser&&
                                singlesnapshot.getValue(Likes.class).getUser_id()
                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            myref.child(getString(R.string.dbname_photos))
                                    .child(mphoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
                            myref.child(getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mphoto.getPhoto_id())
                                    .child(getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
                            mheart.toggleLike();
                            getLikesString();
                        }
                        //case 2: The user has not liked the photo
                        else if(!mLikedByCurrentUser){
                            //add New Like
                            addNewLike();
                            break;
                        }

                    }
                    if(!snapshot.exists()){
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return true;
        }
    }
    private void addNewLike(){
        String newLikeId=myref.push().getKey();
        Likes likes=new Likes();
        likes.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myref.child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(likes);
        myref.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(likes);
        mheart.toggleLike();
        getLikesString();
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
            mLikes.setText(mLikesString);
            mCaption.setText(mphoto.getCaption());
            if(mphoto.getComments().size()>0){
                mComments.setText("View all "+mphoto.getComments().size()+" comments");
            }else{
                mComments.setText("");
            }
            mComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mphoto);
                }
            });
            mBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(mphoto);
                }
            });
            if(mLikedByCurrentUser){
                mHeartWhite.setVisibility(View.GONE);
                mHeartRed.setVisibility(View.VISIBLE);
                mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return mgestureDetector.onTouchEvent(motionEvent);
                    }
                });

            }
            else {
                mHeartWhite.setVisibility(View.VISIBLE);
                mHeartRed.setVisibility(View.GONE);
                mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return mgestureDetector.onTouchEvent(motionEvent);
                    }
                });


            }
        }catch (Exception e) {

        }

    }
    private void getCurrrentUser(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnaphot:snapshot.getChildren()) {
                    mCurrentuser=singleSnaphot.getValue(User.class);
                }
                getLikesString();
                // setUpWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnCommentThreadSelectedListener=(OnCommentThreadSelectedListener) getActivity();

        }
        catch (ClassCastException e){

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
        final String photoTimestamp=mphoto.getDate_created();
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
