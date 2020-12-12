package com.example.taper.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.taper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class ViewCommentsFragment extends Fragment {
    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }
    private Photo mphoto;
    private Context mcontext;
    private ImageView mBackArrow,mCheckMark;
    private EditText mComment;
    private ArrayList<Comment> mComments;
    private ListView mListView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        mBackArrow=(ImageView) view.findViewById(R.id.backArrow);
        mCheckMark=(ImageView) view.findViewById(R.id.ivPostComment);
        mComment=(EditText) view.findViewById(R.id.comment);
        mComments=new ArrayList<>();
        mcontext=getActivity();
        mListView=(ListView) view.findViewById(R.id.listView);

        try {
            mphoto=getPhotoFromBuncdle();
            setUpFirebase();

        }catch(NullPointerException e){

        }

        return view;

    }

    private void setUpWidgets(){

        CommentsListAdapter adapter=new CommentsListAdapter(mcontext,R.layout.layout_comment,mComments);
        mListView.setAdapter(adapter);
        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mComment.getText().toString().equals("")){
                    addComment(mComment.getText().toString());
                    mComment.setText("");
                    closeKeyboard();
                }
                else{
                    Toast.makeText(getActivity(), "You can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void closeKeyboard(){
        View view=getActivity().getCurrentFocus();
        if(view!=null){
            InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void addComment(String newComment){
        String commenID=myref.push().getKey();
        Comment comment =new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimeStamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photos node
        myref.child(getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commenID)
                .setValue(comment);
        //insert in user_photos node
        myref.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commenID)
                .setValue(comment);

    }
    private String getTimeStamp(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
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
        if(mphoto.getComments().size()==0){
            mComments.clear();
            Comment  firstComment=new Comment();
            firstComment.setComment(mphoto.getCaption());
            firstComment.setUser_id(mphoto.getUser_id());
            firstComment.setDate_created(mphoto.getDate_created());
            mComments.add(firstComment);
            mphoto.setComments(mComments);
            setUpWidgets();
        }
        myref.child(mcontext.getString(R.string.dbname_photos))
                .child(mphoto.getPhoto_id())
                .child(mcontext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Query query=myref
                                .child(mcontext.getString(R.string.dbname_photos))
                                .orderByChild(mcontext.getString(R.string.field_photo_id))
                                .equalTo(mphoto.getPhoto_id());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot singleSnapshot:snapshot.getChildren()){

                                    Photo photo=new Photo();
                                    Map<String,Object> objectMap=(HashMap<String,Object>) singleSnapshot.getValue();
                                    photo.setCaption(objectMap.get(mcontext.getString(R.string.field_caption)).toString());
                                    photo.setTags(objectMap.get(mcontext.getString(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(mcontext.getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(mcontext.getString(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(mcontext.getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(mcontext.getString(R.string.field_image_path)).toString());
                                    mComments.clear();
                                    Comment  firstComment=new Comment();
                                    firstComment.setComment(mphoto.getCaption());
                                    firstComment.setUser_id(mphoto.getUser_id());
                                    firstComment.setDate_created(mphoto.getDate_created());
                                    mComments.add(firstComment);
                                    for(DataSnapshot dataSnapshot:singleSnapshot
                                            .child(mcontext.getString(R.string.field_comments)).getChildren()){
                                        Comment comment=new Comment();
                                        comment.setUser_id(dataSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dataSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dataSnapshot.getValue(Comment.class).getDate_created());
                                        mComments.add(comment);
                                    }
                                    photo.setComments(mComments);
                                    mphoto=photo;
                                    setUpWidgets();
//                    List<Likes> likesList=new ArrayList<Likes>();
//
//                    for(DataSnapshot dataSnapshot:singleSnapshot
//                            .child(getString(R.string.field_likes)).getChildren()){
//                        Likes like =new Likes();
//                        like.setUser_id(dataSnapshot.getValue(Likes.class).getUser_id());
//                        likesList.add(like);
//                    }


                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
