package com.example.taper.Search;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.opengl.ETC1;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.taper.Models.User;
import com.example.taper.Profile.ProfileActivity;
import com.example.taper.R;
import com.example.taper.Share.ShareActivity;
import com.example.taper.Utils.BottomNavigationViewHelper;
import com.example.taper.Utils.Permissions;
import com.example.taper.Utils.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG="SearchActivity";
    private static final int Activity_num=1;
    private Context mcontext=SearchActivity.this;
    private EditText mSearcParam;
    private ListView mListView;
    private List<User> mUserList;
    private UserListAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearcParam=(EditText) findViewById(R.id.search);
        mListView=(ListView) findViewById(R.id.listView);
        hideSoftKeyboard();
        setup_bottom_navigation();
        initTextListener();
    }
    private void initTextListener(){
        mUserList=new ArrayList<>();
        mSearcParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text=mSearcParam.getText().toString();
                searchForMatch(text);
            }
        });
    }
    private void updateUserlIst(){
        mAdapter=new UserListAdapter(SearchActivity.this,R.layout.layout_user_listitem,mUserList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity),getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user),mUserList.get(i));
                startActivity(intent);
            }
        });
    }
    private void searchForMatch(String keyword){
        mUserList.clear();
        if(keyword.length()==0){

        }else{
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
            Query query =reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username))
                    .equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                        mUserList.add(singleSnapshot.getValue(User.class));
                        updateUserlIst();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null){
            InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    private void setup_bottom_navigation(){
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottom_Nav_View);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mcontext,this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(Activity_num);
        menuItem.setChecked(true);
    }
}
