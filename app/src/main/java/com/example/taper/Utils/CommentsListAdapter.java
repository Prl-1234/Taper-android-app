package com.example.taper.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taper.Models.Comment;
import com.example.taper.Models.UserAccountSetting;
import com.example.taper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsListAdapter extends ArrayAdapter<Comment>  {

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentsListAdapter(@NonNull Context context,
                               int resource,
                               @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext=context;
        layoutResource=resource;
    }
    private static class ViewHolder{
        TextView comment,username,timeStamp,reply,likes;
        CircleImageView profileImage;
        ImageView like;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView=mInflater.inflate(layoutResource,parent,false);
            holder=new ViewHolder();

            holder.comment=(TextView) convertView.findViewById(R.id.comment);
            holder.username=(TextView) convertView.findViewById(R.id.comment_username);
            holder.timeStamp=(TextView) convertView.findViewById(R.id.comment_time_posted);
            holder.reply=(TextView) convertView.findViewById(R.id.comment_reply);
            holder.like=(ImageView) convertView.findViewById(R.id.comment_like);
            holder.profileImage=(CircleImageView) convertView.findViewById(R.id.comment_profile_image);
            convertView.setTag(holder);
        }
        else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.comment.setText(getItem(position).getComment());
        String timeStampDifference=getTimeStampDifference(getItem(position));
        if(!timeStampDifference.equals("0")){
            holder.timeStamp.setText(timeStampDifference+" d");
        }
        else {
            holder.timeStamp.setText("today");
        }
        //set username
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query=reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnaphot:snapshot.getChildren()) {
                    holder.username.setText(
                            singleSnaphot.getValue(UserAccountSetting.class).getUsername()

                    );
                    ImageLoader imageLoader=ImageLoader.getInstance();
                    imageLoader.displayImage(singleSnaphot.getValue(UserAccountSetting.class).getProfile_photo(),
                            holder.profileImage);
                }
                // setUpWidgets();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        try {
            if(position==0){
                holder.like.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){

        }

        return convertView;
    }
    private String getTimeStampDifference(Comment comment){
        String difference="";
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today=c.getTime();
        sdf.format(today);

        Date timestamp;
        final String photoTimestamp=comment.getDate_created();
        try {
            timestamp=sdf.parse(photoTimestamp);
            difference=String.valueOf(Math.round(((today.getTime()-timestamp.getTime())/1000/60/60/24)));
        }catch (ParseException e){
            difference="0";
        }
        return difference;
    }
}
