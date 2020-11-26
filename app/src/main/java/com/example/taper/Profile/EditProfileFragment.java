package com.example.taper.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.R;
import com.example.taper.Share.ShareActivity;
import com.example.taper.Utils.FirebaseMethods;
import com.example.taper.Utils.UniversalImageLoader;
import com.example.taper.dialog.ConfirmPasswordDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements
    ConfirmPasswordDialog.OnConfirmPasswordListener{

    @Override
    public void onConfirmPassword(String password) {
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential= EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(),password);
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //cheeck if email is already in use

                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try {
                                            if(task.getResult().getSignInMethods().size()==1){
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                //Email is available

                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    // email updated
                                                                    Toast.makeText(getActivity(), "Email Updated", Toast.LENGTH_SHORT).show();

                                                                    mfirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){

                                        }

                                    }
                                    else{

                                    }
                                }
                            });

                        }
                        else{

                        }
                    }
                });
    }
    private String userid;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private FirebaseMethods mfirebaseMethods;
    private CircleImageView mProfilePhoto;
    private EditText mDisplayName,mUsername,mWebsite,mDescription,mEmail,mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private UserSettings mUserSettings;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_edit_profile,container,false);
        mProfilePhoto =(CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName=(EditText) view.findViewById(R.id.display_name);
        mUsername=(EditText) view.findViewById(R.id.username_edit);
        mWebsite=(EditText) view.findViewById(R.id.website);
        mEmail=(EditText) view.findViewById(R.id.email);
        mPhoneNumber=(EditText) view.findViewById(R.id.phone);
        mChangeProfilePhoto=(TextView) view.findViewById(R.id.changeProfilePhoto);
        mDescription=(EditText) view.findViewById(R.id.description);
        mfirebaseMethods= new FirebaseMethods(getActivity());

        //initImageLoader();
        //setProfileImage();
        setUpFirebase();

        ImageView backarrow=(ImageView) view.findViewById(R.id.backArrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        ImageView checkMark=(ImageView) view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileSettings();
            }
        });

        return view;
    }
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader=new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
//    private void setProfileImage(){
//        String imgURL="english.mathrubhumi.com/polopoly_fs/1.3305719.1542098400!/image/image.jpg_gen/derivatives/landscape_894_577/image.jpg";
//        UniversalImageLoader.setImage(imgURL,mProfilePhoto,null,"https://");
//    }
    private void setProfileWidgets(UserSettings userSettings){
        // User user=userSettings.getUser();
        mUserSettings=userSettings;
        UserAccountSetting setting=userSettings.getSetting();
        UniversalImageLoader.setImage(setting.getProfile_photo(),mProfilePhoto,null,"");
        mDisplayName.setText(setting.getDisplay_name());
        mWebsite.setText(setting.getWebsite());
        mDescription.setText(setting.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mUsername.setText(setting.getUsername());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }
    private void checkIfUserNameExists(final String username){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists()){
                    mfirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username", Toast.LENGTH_SHORT).show();

                }
//                else{
//                    Toast.makeText(getActivity(), "This username already exists", Toast.LENGTH_SHORT).show();
//                }
//
                for(DataSnapshot singlesnaphot:snapshot.getChildren()){
                    if(singlesnaphot.exists()){
                        Toast.makeText(getActivity(), "This username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveProfileSettings(){
        final String displayName=mDisplayName.getText().toString();
        final String userName=mUsername.getText().toString();
        final String website=mWebsite.getText().toString();
        final String description=mDescription.getText().toString();
        final String email=mEmail.getText().toString();
        final long phoneNumber=Long.parseLong(mPhoneNumber.getText().toString());

        if(!mUserSettings.getUser().getUsername().equals(userName)){

            //Toast.makeText(getActivity(), "hey", Toast.LENGTH_SHORT).show();
            checkIfUserNameExists(userName);
        }
        if(!mUserSettings.getUser().getEmail().equals(email)){
            //Reauthenticate
            //Confirms the password and email
            ConfirmPasswordDialog dialog=new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(),getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this,1);

            //check if email already already registered
            //Fetch Provided Email
            //Change the email
            //Submit the new email to database and authentication

        }
        if(!mUserSettings.getSetting().getDisplay_name().equals(displayName)){
            mfirebaseMethods.updateUserAccountSetting(displayName,null,null,0);
        }
        if(!mUserSettings.getSetting().getWebsite().equals(website)){
            mfirebaseMethods.updateUserAccountSetting(null,website,null,0);
        }
        if(!mUserSettings.getSetting().getDescription().equals(description)){
            mfirebaseMethods.updateUserAccountSetting(null,null,description,0);

        }
        if(!mUserSettings.getSetting().getProfile_photo().equals(phoneNumber)){
            mfirebaseMethods.updateUserAccountSetting(null,null,null,phoneNumber);
            mfirebaseMethods.updatePhoneNumber(phoneNumber);
        }

    }

    private void setUpFirebase(){
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myref=mFirebaseDatabase.getReference();
        userid=mAuth.getCurrentUser().getUid();
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
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }


}
