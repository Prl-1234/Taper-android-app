package com.example.taper.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.taper.Models.Photo;
import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.R;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.FirebaseMethods;
import com.example.taper.Utils.GridImageAdapter;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final int NUM_GRID_COLUMNS=3;
    private TextView mPosts,mFollowers,mFollowing,mDisplay_name,mUsername,mWebsite,mDescription;
    private ProgressBar mProgressBar;
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo,int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;
    private CircleImageView mProfilephoto;
    private ImageView profileMenu;
    private GridView gridView;
    private Toolbar toolbar;
    private static final int Activity_num=4;
    private Context mcontext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private FirebaseMethods mfirebaseMethods;
    private BottomNavigationViewEx bottomNavigationViewEx;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);
        mDisplay_name=(TextView) view.findViewById(R.id.display_name);

        mUsername=(TextView) view.findViewById(R.id.username);

        mWebsite=(TextView) view.findViewById(R.id.website);

        mDescription=(TextView) view.findViewById(R.id.description);

        mPosts=(TextView) view.findViewById(R.id.textView2);

        mFollowers=(TextView) view.findViewById(R.id.textView3);
        mFollowing=(TextView) view.findViewById(R.id.textView4);
        mProfilephoto=(CircleImageView) view.findViewById(R.id.profile_image);
        mProgressBar=(ProgressBar) view.findViewById(R.id.profile_progress);
        gridView=(GridView) view.findViewById(R.id.gridview);
        toolbar=(Toolbar) view.findViewById(R.id.profiletoolbar);
        profileMenu=(ImageView) view.findViewById(R.id.profileMenu);
        bottomNavigationViewEx=(BottomNavigationViewEx) view.findViewById(R.id.bottom_Nav_View);
        mcontext=getActivity();
        mfirebaseMethods =new FirebaseMethods(getActivity());
        setup_bottom_navigation();
        setUpToolBar();
        setUpFirebase();
        setUpGridView();
        TextView editprofile=(TextView) view.findViewById(R.id.textView8);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);


            }
        });
        return view;
    }
    private void setProfileWidgets(UserSettings userSettings){
       // User user=userSettings.getUser();
        UserAccountSetting setting=userSettings.getSetting();
        UniversalImageLoader.setImage(setting.getProfile_photo(),mProfilephoto,null,"");
        mDisplay_name.setText(setting.getDisplay_name());
        mWebsite.setText(setting.getWebsite());
        mDescription.setText(setting.getDescription());
        mPosts.setText(String.valueOf(setting.getPosts()));
        mFollowers.setText(String.valueOf(setting.getFollowers()));
        mFollowing.setText(String.valueOf(setting.getFollowing()));
        mProgressBar.setVisibility(View.GONE);
        mUsername.setText(setting.getUsername());

    }
    private void setUpGridView(){
        final ArrayList<Photo> photos=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnaphot:snapshot.getChildren()){
                    photos.add(singleSnaphot.getValue(Photo.class));
                }
                //setup our image grid
                int gridWidth=getResources().getDisplayMetrics().widthPixels;
                int imageWidth=gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrl=new ArrayList<>();
                for(int i=0;i<photos.size();i++){
                    imgUrl.add(photos.get(i).getImage_path());

                }
                GridImageAdapter adapter=new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,"",imgUrl);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mOnGridImageSelectedListener.onGridImageSelected(photos.get(i),Activity_num);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            mOnGridImageSelectedListener=(OnGridImageSelectedListener)getActivity();
        }catch (ClassCastException e){

        }
        super.onAttach(context);
    }

    private  void setUpToolBar(){
            ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);
            profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mcontext,AccountSettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }
    private void setup_bottom_navigation(){

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mcontext,getActivity(),bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(Activity_num);
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
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setProfileWidgets(mfirebaseMethods.getUserSetting(snapshot));
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
