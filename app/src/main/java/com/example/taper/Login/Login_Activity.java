package com.example.taper.Login;

import android.content.Context;
import android.content.Intent;
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

import com.example.taper.Home.MainActivity;
import com.example.taper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mcontext;
    private ProgressBar mProgressBar;
    private EditText mEmail,mPassword;
    private TextView mPleaseWait;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar =(ProgressBar) findViewById(R.id.loginresourcelodingprogressbar);
        mPleaseWait=(TextView)findViewById(R.id.pleasewait);
        mEmail=(EditText)findViewById((R.id.input_email));
        mPassword=(EditText) findViewById(R.id.input_password);
        mcontext= Login_Activity.this;
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);
        setUpFirebase();
        init();
    }
    private boolean isStringNull(String string){
        if(string.equals("")){
            return true;
        }
        else {
            return false;
        }
    }
    private void init(){
        Button btnLogin=(Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString();
                String password= mPassword.getText().toString();
                if(isStringNull(email)&&isStringNull(password)){
                    Toast.makeText(mcontext, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user=mAuth.getCurrentUser();

                                    if(!task.isSuccessful()){
                                        Toast.makeText(mcontext, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                         mProgressBar.setVisibility(View.GONE);
                                         mPleaseWait.setVisibility(View.GONE);
                                     }
                                    else{
                                        try{
                                            if(user.isEmailVerified()){
                                                Intent intent=new Intent(Login_Activity.this,MainActivity.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(mcontext, "Email is not verified . Check your email inbox ", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }
                                        }catch (NullPointerException e){

                                        }
                                    }
                                }
                            });
                }
            }
        });
        TextView linkSignUp=(TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login_Activity.this,Register_Acitivity.class);
                startActivity(intent);
            }
        });
        if(mAuth.getCurrentUser()!=null){
            Intent intent=new Intent(Login_Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void setUpFirebase(){
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                //checkcurrentuser(user);
                if(user!=null){

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
