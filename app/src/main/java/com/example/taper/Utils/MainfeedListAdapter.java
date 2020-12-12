package com.example.taper.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taper.Home.MainActivity;
import com.example.taper.Models.Comment;
import com.example.taper.Models.Likes;
import com.example.taper.Models.Photo;
import com.example.taper.Models.User;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.Profile.ProfileActivity;
import com.example.taper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainfeedListAdapter extends ArrayAdapter<Photo> {
    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mrefrence;
    private String currentUsername="";

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource=resource;
        this.mContext=context;
    }
    static class ViewHolder{
        CircleImageView mProfileImage;
        String likesString;
        TextView username,timeDetails,caption,likes,comments;
        SquareImageView image;
        ImageView heartRed,heartWhite,comment;

        UserAccountSetting setting=new UserAccountSetting();
        User user=new User();
        StringBuilder users;
        String mLikesString;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView=mInflater.inflate(mLayoutResource,parent,false);
            holder=new ViewHolder();
            holder.username=(TextView) convertView.findViewById(R.id.username);
            holder.image=(SquareImageView) convertView.findViewById(R.id.post_image);
            holder.heartRed=(ImageView) convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite=(ImageView) convertView.findViewById(R.id.image_heart);
            holder.comment=(ImageView) convertView.findViewById(R.id.speech_bubble);
            holder.likes=(TextView)convertView.findViewById(R.id.imagelikes);
            holder.comments=(TextView) convertView.findViewById(R.id.image_comments_link);
            holder.caption=(TextView) convertView.findViewById(R.id.image_caption);
            holder.timeDetails=(TextView) convertView.findViewById(R.id.image_time_posted);
            holder.mProfileImage=(CircleImageView) convertView.findViewById(R.id.profile_image);
            holder.heart=new Heart(holder.heartWhite,holder.heartRed);
            holder.photo=getItem(position);
            holder.detector=new GestureDetector(mContext,new GestureListener(holder));
            holder.users=new StringBuilder();
            convertView.setTag(holder);

        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        getCurrentUsername();
        getLikesString(holder);
        List<Comment> comments=getItem(position).getComments();
        holder.comments.setText("View all "+comments.size()+" commnets");
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).onCommentThreadSelector(getItem(position),holder.setting);


            }
        });
        String timeStampDifference=getTimeStampDifference(getItem(position));
        if(!timeStampDifference.equals("0")){
            holder.timeDetails.setText(timeStampDifference+" DAYS AGO");

        }else{
            holder.timeDetails.setText("TODAY");
        }
        final ImageLoader imageLoader=ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(),holder.image);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                   // currentUsername=singlesnapshot.getValue(UserAccountSetting.class).getUsername();
                    holder.username.setText(singlesnapshot.getValue(UserAccountSetting.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    imageLoader.displayImage(singlesnapshot.getValue(UserAccountSetting.class).getProfile_photo(),
                            holder.mProfileImage);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(mContext, ProfileActivity.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user),holder.user);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.setting=singlesnapshot.getValue(UserAccountSetting.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MainActivity)mContext).onCommentThreadSelector(getItem(position),holder.setting);


                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query userQuery = mrefrence
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                    holder.user=singlesnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return convertView;
    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        ViewHolder mholder;
        public GestureListener(ViewHolder holder) {
            mholder=holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // mheart.toggleLike();
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
            Query query=reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(mholder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot singlesnapshot: snapshot.getChildren()){
                        //case 1: Then user laready liked the photo
                        String keyID=singlesnapshot.getKey();
                        if(mholder.likeByCurrentUser&&
                                singlesnapshot.getValue(Likes.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            mrefrence.child(mContext.getString(R.string.dbname_photos))
                                    .child(mholder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
                            mrefrence.child(mContext.getString(R.string.dbname_user_photos))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mholder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
                            mholder.heart.toggleLike();
                            getLikesString(mholder);
                        }
                        //case 2: The user has not liked the photo
                        else if(!mholder.likeByCurrentUser){
                            //add New Like
                            addNewLike(mholder);
                            break;
                        }

                    }
                    if(!snapshot.exists()){
                        addNewLike(mholder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return true;
        }
    }
    private void addNewLike(final ViewHolder holder){
        String newLikeId=mrefrence.push().getKey();
        Likes likes=new Likes();
        likes.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mrefrence.child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(likes);
        mrefrence.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeId)
                .setValue(likes);
        holder.heart.toggleLike();
        getLikesString(holder);
    }
    private void getLikesString( final ViewHolder holder){
        try {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(holder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.users = new StringBuilder();
                    for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        Query query = reference
                                .child(mContext.getString(R.string.dbname_users))
                                .orderByChild(mContext.getString(R.string.field_user_id))
                                .equalTo(singlesnapshot.getValue(Likes.class).getUser_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                    holder.users.append(singleSnapshot.getValue(User.class).getUsername());
                                    holder.users.append(",");


                                }
                                String[] splitUsers = holder.users.toString().split(",");
                                if (holder.users.toString().contains(holder.user.getUsername() + ",")) {
                                    holder.likeByCurrentUser = true;
                                } else {
                                    holder.likeByCurrentUser = false;
                                }
                                int length = splitUsers.length;
                                if (length == 1) {
                                    holder.likesString = "Liked by " + splitUsers[0];
                                } else if (length == 2) {
                                    holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];

                                } else if (length == 3) {
                                    holder.likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + " and " + splitUsers[2];
                                } else if (length == 4) {
                                    holder.likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + splitUsers[3];
                                } else if (length > 4) {
                                    holder.likesString = "Liked by " + splitUsers[0] + ", " + splitUsers[1] + ", " + splitUsers[2] + " and " + (splitUsers.length - 3) + " others";

                                }
                                //   setUpWidgets();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    if (!snapshot.exists()) {
                        holder.likesString = "";
                        holder.likeByCurrentUser = false;
                        setUpLikesString(holder,holder.likesString);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (NullPointerException e){
            holder.likesString="";
            holder.likeByCurrentUser=false;
            setUpLikesString(holder,holder.likesString);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setUpLikesString(final ViewHolder holder, String likesString){
        if(holder.likeByCurrentUser){
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return holder.detector.onTouchEvent(motionEvent);
                }
            });
        }
        else {
            holder.heartRed.setVisibility(View.GONE);
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return holder.detector.onTouchEvent(motionEvent);
                }
            });
        }
        holder.likes.setText(likesString);
    }
    private String getTimeStampDifference(Photo photo){
        String difference="";
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today=c.getTime();
        sdf.format(today);

        Date timestamp;
        final String photoTimestamp=photo.getDate_created();
        try {
            timestamp=sdf.parse(photoTimestamp);
            difference=String.valueOf(Math.round(((today.getTime()-timestamp.getTime())/1000/60/60/24)));
        }catch (ParseException e){
            difference="0";
        }
        return difference;
    }
    private void getCurrentUsername(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singlesnapshot : snapshot.getChildren()) {
                    currentUsername=singlesnapshot.getValue(UserAccountSetting.class).getUsername();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
