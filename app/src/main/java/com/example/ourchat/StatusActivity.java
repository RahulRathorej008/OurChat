package com.example.ourchat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar nToolbar;
    private TextInputLayout nStatus;
    private Button nSavebtn;
    private DatabaseReference nStatusDatabase;
    private FirebaseUser nCurrrentUser;
    private ProgressDialog nProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        nCurrrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=nCurrrentUser.getUid();
        nStatusDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);



        nToolbar=(Toolbar)findViewById(R.id.status_appBar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value=getIntent().getStringExtra("status_value");
        nStatus=(TextInputLayout)findViewById(R.id.status_input);
        nSavebtn=(Button)findViewById(R.id.status_save_btn);
        nStatus.getEditText().setText(status_value);
        nSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nProgress=new ProgressDialog(StatusActivity.this);
                nProgress.setTitle("Saving Changes");
                nProgress.setMessage("Please wait while  we save  the changes");
                nProgress.show();
                String status=nStatus.getEditText().getText().toString();
                nStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            nProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"There was some erroe in saving Changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
