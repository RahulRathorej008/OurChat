package com.example.ourchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.Collator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout nLoginEmail;
    private TextInputLayout nLoginPassword;
    private Button nLogin_btn;
    private FirebaseAuth mAuth;
    private Toolbar nToolbar;
    private ProgressDialog nLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nToolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nLoginProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        nLoginEmail=(TextInputLayout)findViewById(R.id.login_email);
        nLoginPassword=(TextInputLayout)findViewById(R.id.login_password);
        nLogin_btn= (Button) findViewById(R.id.login_btn);

        nLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = nLoginEmail.getEditText().getText().toString();
                String password = nLoginPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    nLoginProgress.setTitle("Logging In");
                    nLoginProgress.setMessage("Please wait while we check your credentials");
                    nLoginProgress.setCanceledOnTouchOutside(false);
                    nLoginProgress.show();
                    Login_user(email, password);
                }
            }
        });
    }

    private void Login_user(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            nLoginProgress.dismiss();
                            Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                           // Log.d(TAG, "signInWithEmail:success");
                           // FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            nLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

}
