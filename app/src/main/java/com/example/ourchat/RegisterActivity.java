package com.example.ourchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nDisplayName;
    private TextInputLayout nEmail;
    private TextInputLayout nPassword;
    private Button nCreateBtn;
    private FirebaseAuth mAuth;
    private Toolbar nToolbar;
    private DatabaseReference nDatabase;
    private ProgressDialog nRegProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nToolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nRegProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        nDisplayName=(TextInputLayout)findViewById(R.id.reg_display_name);
        nEmail=(TextInputLayout)findViewById(R.id.reg_email);
        nPassword=(TextInputLayout)findViewById(R.id.reg_password);
        nCreateBtn= (Button) findViewById(R.id.reg_create_btn);

        nCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name=nDisplayName.getEditText().getText().toString();
                String email=nEmail.getEditText().getText().toString();
                String password=nPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    nRegProgress.setTitle("Registering User");
                    nRegProgress.setMessage("Please wait while we create your account");
                    nRegProgress.setCanceledOnTouchOutside(false);
                    nRegProgress.show();
                    register_user(display_name, email, password);
                }
            }
        });
    }
    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user =FirebaseAuth.getInstance().getCurrentUser();
                            String uid=current_user.getUid();
                            nDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            HashMap<String,String> useMap= new HashMap<>();
                            useMap.put("name",display_name);
                            useMap.put("status","Hi there I'm using OurChat App");
                            useMap.put("image","default");
                            useMap.put("thumb_image","default");
                            nDatabase.setValue(useMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        nRegProgress.dismiss();
                                        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                  //  Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                            nRegProgress.hide();
                            Toast.makeText(RegisterActivity.this,"Cannot Sign in. Please check the form and try again.",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}
