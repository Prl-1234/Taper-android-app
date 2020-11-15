package com.example.taper.Utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mcontext;
    private String userid;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    public FirebaseMethods(Context context){
        mAuth=FirebaseAuth.getInstance();
        mcontext=context;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myref=mFirebaseDatabase.getReference();
        if(mAuth.getCurrentUser()!=null){
            userid=mAuth.getCurrentUser().getUid();
        }
    }
    public void updateUsername(String username){
        myref.child(mcontext.getString(R.string.dbname_users))
                .child(userid)
                .child(mcontext.getString(R.string.field_username))
                .setValue(username);
        myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                .child(userid)
                .child(mcontext.getString(R.string.field_username))
                .setValue(username);
    }
//    public boolean checkForUsernameExists(String username, DataSnapshot dataSnapshot){
//        User user =new User();
//        for(DataSnapshot ds:dataSnapshot.child(userid).getChildren()){
//            user.setUsername(ds.getValue(User.class).getUsername());
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                return true;
//            }
//        }
//        return false;
//    }
    public void updateUserAccountSetting(String displayName,String website,String description,long phoneNumber){
        if(displayName!=null){
            myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                    .child(userid)
                    .child(mcontext.getString(R.string.field_displayname))
                    .setValue(displayName);
        }
        if(description!=null){
            myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                    .child(userid)
                    .child(mcontext.getString(R.string.field_description))
                    .setValue(description);
        }
        if(website!=null){
            myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                    .child(userid)
                    .child(mcontext.getString(R.string.field_website))
                    .setValue(website);
        }
        if(phoneNumber!=0L){
            myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                    .child(userid)
                    .child(mcontext.getString(R.string.field_phoneNumber))
                    .setValue(phoneNumber);
        }

    }
    public void updatePhoneNumber(long phoneNumber){
        myref.child(mcontext.getString(R.string.dbname_users))
                .child(userid)
                .child(mcontext.getString(R.string.field_phoneNumber))
                .setValue(phoneNumber);

    }
    public void registerNewEmail(final String email,String password,String username){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(mcontext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                        else if(task.isSuccessful()){
                            sendVerificationEmail();;
                            userid=mAuth.getCurrentUser().getUid();
                        }
                    }
                });
    }
    public void addNewUser(String email,String username, String description,String website,String profile_photo){
        User user= new User(userid,email,1,StringManipulation.condenseUsername(username));
        myref.child(mcontext.getString(R.string.dbname_users))
                .child(userid)
                .setValue(user);
        UserAccountSetting setting=new UserAccountSetting(
            description,username,0,0,0,profile_photo,StringManipulation.condenseUsername(username),website
        );
        myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                .child(userid)
                .setValue(setting);
    }
    public void sendVerificationEmail(){
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mcontext, "", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(mcontext, "Couldn't send Email verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public UserSettings getUserSetting(DataSnapshot dataSnapshot){
        UserAccountSetting setting=new UserAccountSetting();
        User user=new User();
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            if(ds.getKey().equals(mcontext.getString(R.string.dbname_user_account_settings))){
                try{
                setting.setDisplay_name(
                        ds.child(userid)
                        .getValue(UserAccountSetting.class)
                        .getDisplay_name()
                );
                setting.setUsername(
                        ds.child(userid)
                        .getValue(UserAccountSetting.class)
                        .getUsername()
                );
                setting.setWebsite(
                        ds.child(userid)
                        .getValue(UserAccountSetting.class)
                        .getWebsite()
                );
                setting.setDescription(
                        ds.child(userid)
                                .getValue(UserAccountSetting.class)
                                .getDescription()
                );
                setting.setProfile_photo(
                        ds.child(userid)
                                .getValue(UserAccountSetting.class)
                                .getProfile_photo()
                );
                setting.setPosts(
                        ds.child(userid)
                                .getValue(UserAccountSetting.class)
                                .getPosts()
                );
                setting.setFollowers(
                        ds.child(userid)
                                .getValue(UserAccountSetting.class)
                                .getFollowers()
                );
                setting.setFollowing(
                        ds.child(userid)
                                .getValue(UserAccountSetting.class)
                                .getFollowing()
                );
                }
                catch (NullPointerException e){

                }

            }
            if(ds.getKey().equals(mcontext.getString(R.string.dbname_users))){
                user.setUsername(
                        ds.child(userid)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setPhone_number(
                        ds.child(userid)
                                .getValue(User.class)
                                .getPhone_number()
                );
                user.setEmail(
                        ds.child(userid)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setUser_id(
                        ds.child(userid)
                                .getValue(User.class)
                                .getUser_id()
                );

            }
        }
        return new UserSettings(user,setting);
    }

    /**
     * update email in user's mode
     * @param email
     */
    public void updateEmail(String email){
        myref.child(mcontext.getString(R.string.dbname_users))
                .child(userid)
                .child(mcontext.getString(R.string.field_email))
                .setValue(email);

    }

}



















