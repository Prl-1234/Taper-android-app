package com.example.taper.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.taper.Home.MainActivity;
import com.example.taper.Models.Photo;
import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Models.UserSettings;
import com.example.taper.Profile.AccountSettingActivity;
import com.example.taper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {
    private StorageReference mstorageReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mcontext;
    private String userid;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;
    private double mPhotouploadProgress=0;

    public FirebaseMethods(Context context){
        mAuth=FirebaseAuth.getInstance();
        mcontext=context;
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myref=mFirebaseDatabase.getReference();
        mstorageReference= FirebaseStorage.getInstance().getReference();
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
                            sendVerificationEmail();
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
            description,username,0,0,0,
                profile_photo,StringManipulation.condenseUsername(username),
                website,userid
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
                                Toast.makeText(mcontext, "Verified", Toast.LENGTH_SHORT).show();
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
                setting.setUser_id(
                        ds.child(userid)
                        .getValue(UserAccountSetting.class)
                        .getUser_id()
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

    public int getImageCount(DataSnapshot snapshot) {
        int count=0;
        for(DataSnapshot ds: snapshot
            .child(mcontext.getString(R.string.dbname_user_photos))
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .getChildren()){
            count++;
        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl,Bitmap bm) {
        PathFiles pathFiles=new PathFiles();


        if(photoType.equals(mcontext.getString(R.string.new_photo))){
            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference=mstorageReference
                    .child(pathFiles.FIREBASE_IMAGE_STORAGE+"/"+user_id+"/photo"+(count+1));
            if(bm==null){

                bm=ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes=ImageManager.getByteFromBitmap(bm,100);
            UploadTask uploadTask=null;
            uploadTask=storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri firebaseUri=uri;
                            Toast.makeText(mcontext, "Photo upload Success", Toast.LENGTH_SHORT).show();
                            addPhototoDatabase(caption,firebaseUri.toString());
                            Intent intent=new Intent(mcontext, MainActivity.class);
                            mcontext.startActivity(intent);
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mcontext, "Photo upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    if(progress-15>mPhotouploadProgress){
                        Toast.makeText(mcontext, "Photo upload progress "+String.format("%.0f",progress)+"%", Toast.LENGTH_SHORT).show();
                        mPhotouploadProgress=progress;
                    }

                }
            });
        }
        else if(photoType.equals(mcontext.getString(R.string.profile_photo))){

            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference=mstorageReference
                    .child(pathFiles.FIREBASE_IMAGE_STORAGE+"/"+user_id+"/profile_photo");
            if(bm==null){

                bm=ImageManager.getBitmap(imgUrl);
            }
            byte[] bytes=ImageManager.getByteFromBitmap(bm,100);
            UploadTask uploadTask=null;
            uploadTask=storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri firebaseUri=uri;
                            Toast.makeText(mcontext, "Photo upload Success", Toast.LENGTH_SHORT).show();
                            setProfilePhoto(firebaseUri.toString());
                            ((AccountSettingActivity)mcontext).setmViewPager(
                                    ((AccountSettingActivity)mcontext).pagerAdapter
                                            .getFragmentNumber(mcontext.getString(R.string.edit_profile_fragment))
                            );
                        }
                    });
                   // Task<Uri>firebaseUri=taskSnapshot.getStorage().getDownloadUrl();
                   // String firebaseUri=taskSnapshot.getStorage().getDownloadUrl().toString();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mcontext, "Photo upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    if(progress-15>mPhotouploadProgress){
                        Toast.makeText(mcontext, "Photo upload progress "+String.format("%.0f",progress)+"%", Toast.LENGTH_SHORT).show();
                        mPhotouploadProgress=progress;
                    }

                }
            });
        }
    }
    private void setProfilePhoto(String url){
        myref.child(mcontext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mcontext.getString(R.string.profile_photo))
                .setValue(url);
    }
    private String getTimeStamp(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }
    private void addPhototoDatabase(String caption, String url){
        String tags=StringManipulation.getTag(caption);
       // String tags="7";
        String newPhotoKey=myref.child(mcontext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo=new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);
        myref.child(mcontext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myref.child(mcontext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

}



















