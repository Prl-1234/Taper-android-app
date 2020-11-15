package com.example.taper.Login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taper.R;
import com.example.taper.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Register_Acitivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFireDatabase;
    private DatabaseReference myref;
    private String append="";
    private Context mcontext;
    private String email,password,username;
    private EditText memail,mpassword,musername;
    private TextView loadingPleasingWait;
    private Button btnRegister;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mcontext=Register_Acitivity.this;
        firebaseMethods=new FirebaseMethods(mcontext);
        setUpFirebase();
        initWidget();
        init();
    }
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=memail.getText().toString();
                username=musername.getText().toString();
                password=mpassword.getText().toString();
                if(checkinput(email,username,password)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleasingWait.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(email,password,username);
                }
            }
        });
    }
    private boolean checkinput(String email,String username,String password){
        if(email.equals("")||username.equals("")||password.equals("")){
            Toast.makeText(mcontext, "Fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void initWidget(){
        btnRegister=(Button) findViewById(R.id.btn_register);
        musername=(EditText) findViewById(R.id.input_name);
        memail=(EditText) findViewById(R.id.input_email);
        mProgressBar =(ProgressBar) findViewById(R.id.registerresourcelodingprogressbar);
        loadingPleasingWait=(TextView)findViewById(R.id.loadingpleasewait);
        mpassword=(EditText) findViewById(R.id.input_password);
        mcontext= Register_Acitivity.this;
        mProgressBar.setVisibility(View.GONE);
        loadingPleasingWait.setVisibility(View.GONE);
    }
    private boolean isStringNull(String string){
        if(string.equals("")){
            return true;
        }
        else {
            return false;
        }
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

                for(DataSnapshot singlesnaphot:snapshot.getChildren()){
                    if(singlesnaphot.exists()){
                       // Toast.makeText(getActivity(), "This username already exists", Toast.LENGTH_SHORT).show();
                        append=myref.push().getKey().substring(3,7);

                    }
                }
                String mUsername="";
                mUsername=username+append;
                firebaseMethods.addNewUser(email,mUsername,"","","");
                Toast.makeText(mcontext, "Signup successful. Sending verification link", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpFirebase(){
        mAuth= FirebaseAuth.getInstance();
        mFireDatabase=FirebaseDatabase.getInstance();
        myref=mFireDatabase.getReference();

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user=firebaseAuth.getCurrentUser();
                //checkcurrentuser(user);
                if(user!=null){
                    myref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            checkIfUserNameExists(username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    finish();
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
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
}
