package com.example.taper.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taper.Models.Comment;
import com.example.taper.Models.Likes;
import com.example.taper.Models.Photo;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.R;
import com.example.taper.Utils.MainfeedListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home_fragment extends Fragment {
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListVew;
    private MainfeedListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        mListVew=(ListView) view.findViewById(R.id.listView);
        mFollowing=new ArrayList<>();
        mPhotos=new ArrayList<>();
        getFollowing();

        return view;
    }
    private void getFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                    mFollowing.add(singlesnapshot.child(getString(R.string.field_user_id)).getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for(int i=0;i<mFollowing.size();i++){
            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                        DatabaseReference reference1= FirebaseDatabase.getInstance().getReference();
                        Query query1 =reference1.child(getString(R.string.dbname_user_account_settings))
                                .orderByChild(getString(R.string.field_user_id))
                                .equalTo(mUser.getUser_id());
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                                    UserSettings settings=new UserSettings();
                                    settings.setUser(mUser);
                                    settings.setSetting(singleSnapshot.getValue(UserAccountSetting.class));
                                    setProfileWidgets(settings);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //get users profile photo
                        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference();
                        Query query2=reference2
                                .child(getString(R.string.dbname_user_photos))
                                .child(mUser.getUser_id());

                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                ArrayList<Photo> photos=new ArrayList<Photo>();
                                for(DataSnapshot singleSnapshot:snapshot.getChildren()){

                                    Photo photo=new Photo();
                                    Map<String,Object> objectMap=(HashMap<String,Object>) singleSnapshot.getValue();
                                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                                    ArrayList<Comment> comments=new ArrayList<Comment>();
                                    for(DataSnapshot dataSnapshot:singleSnapshot
                                            .child(getString(R.string.field_comments)).getChildren()){
                                        Comment comment=new Comment();
                                        comment.setUser_id(dataSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dataSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dataSnapshot.getValue(Comment.class).getDate_created());
                                        comments.add(comment);
                                    }
                                    photo.setComments(comments);
                                    List<Likes> likesList=new ArrayList<Likes>();

                                    for(DataSnapshot dataSnapshot:singleSnapshot
                                            .child(getString(R.string.field_likes)).getChildren()){
                                        Likes like =new Likes();
                                        like.setUser_id(dataSnapshot.getValue(Likes.class).getUser_id());
                                        likesList.add(like);
                                    }
                                    photo.setLikes(likesList);
                                    photos.add(photo);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
