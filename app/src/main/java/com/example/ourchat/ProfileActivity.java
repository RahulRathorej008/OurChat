package com.example.ourchat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {


    private ImageView nProfileImage;
    private Button nProfileSendReqBtn, nProfileDeclineReqBtn;
    private TextView nProfileName, nprofileStatus, nprofileFriendsCount;
    private DatabaseReference nUserReference;
    private DatabaseReference nFriendsReference;
    private DatabaseReference nFriendRequestReference;
    private DatabaseReference nReference;
    private DatabaseReference nNotificationReference;
    private ProgressDialog nProgressDialog;

    private FirebaseUser nUser;

    private String nCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nProfileImage = (ImageView) findViewById(R.id.profile_image);
        nProfileName = (TextView) findViewById(R.id.profile_displayName);
        nprofileStatus = (TextView) findViewById(R.id.profile_status);
        nprofileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        nProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        nProfileDeclineReqBtn = (Button) findViewById(R.id.profile_decline_btn);

        nCurrentState = "not friends";

        final String user_id = getIntent().getStringExtra("user_id");

        nReference = FirebaseDatabase.getInstance().getReference();

        nUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = nUser.getUid();
        nProgressDialog = new ProgressDialog(this);
        nProgressDialog.setTitle("Loading User Data");
        nProgressDialog.setMessage("Please wait while we load user data");
        nProgressDialog.setCanceledOnTouchOutside(false);
        nProgressDialog.show();

        nUserReference = nReference.child("Users").child(user_id);
        nFriendRequestReference = nReference.child("Requests");
        nFriendsReference = nReference.child("Friends");
        nNotificationReference = nReference.child("Notifications");


        nUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                nProfileName.setText(display_name);
                nprofileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.default_avater).into(nProfileImage);
                nProgressDialog.dismiss();

                nProfileDeclineReqBtn.setEnabled(false);
                nProfileDeclineReqBtn.setVisibility(View.INVISIBLE);

                nFriendRequestReference.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String requestType = dataSnapshot.child(user_id).child("type").getValue().toString();

                            if (requestType.equals("sent")) {

                                nCurrentState = "request sent";
                                nProfileSendReqBtn.setText("Cancel Friend Request");

                            } else if (requestType.equals("received")) {

                                nCurrentState = "request received";
                                nProfileSendReqBtn.setText("Accept Friend Request");

                                nProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                nProfileDeclineReqBtn.setEnabled(true);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        nFriendsReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nprofileFriendsCount.setText("Total Friends : " + String.valueOf((int) dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nProfileSendReqBtn.setEnabled(false);
                // ----Friend Request Sent-------//
                if(nCurrentState.equals("not friends")) {
                    nFriendRequestReference.child(uid).child(user_id).child("type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                nFriendRequestReference.child(user_id).child(uid).child("type")
                                        .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            nCurrentState = "request sent";
                                            nProfileSendReqBtn.setText("Cancel Friend Request");

                                            Toast.makeText(ProfileActivity.this, "Request Sent Succesfully", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }

                            nProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }
                //---------Cancel Request------//
                if (nCurrentState.equals("request sent")) {

                    nProfileSendReqBtn.setEnabled(false);

                    nFriendRequestReference.child(uid).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                nFriendRequestReference.child(user_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Request Cancelled Succesfully", Toast.LENGTH_SHORT).show();

                                            nCurrentState = "not friends";
                                            nProfileSendReqBtn.setText("Send Friend Request");

                                        }
                                        else {
                                            Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                            }

                            nProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }

                // -------------  Request Received ----------- //

                if (nCurrentState.equals("request received")) {

                    nProfileSendReqBtn.setEnabled(false);
                    nProfileDeclineReqBtn.setEnabled(false);

                    nFriendRequestReference.child(uid).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                nFriendRequestReference.child(user_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    final String myName = dataSnapshot.child("name").getValue().toString();

                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                                            String otherName = dataSnapshot1.child("name").getValue().toString();

                                                            HashMap<String, Object> myRequestMap = new HashMap<>();

                                                            myRequestMap.put("date", ServerValue.TIMESTAMP);
                                                            myRequestMap.put("name", otherName);
                                                            myRequestMap.put("lastchat", new Long(0));

                                                            final HashMap<String, Object> otherRequestMap = new HashMap<>();

                                                            otherRequestMap.put("date", ServerValue.TIMESTAMP);
                                                            otherRequestMap.put("name", myName);
                                                            otherRequestMap.put("lastchat", new Long(0));

                                                            nFriendsReference.child(uid).child(user_id).setValue(myRequestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {

                                                                        nFriendsReference.child(user_id).child(uid).setValue(otherRequestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()) {

                                                                                    Toast.makeText(ProfileActivity.this, "Accepted Successfully", Toast.LENGTH_SHORT).show();


                                                                                    nCurrentState = "friends";
                                                                                    nProfileSendReqBtn.setText("Unfriend");

                                                                                    nProfileDeclineReqBtn.setVisibility(View.INVISIBLE);

                                                                                }
                                                                                else {
                                                                                    Toast.makeText(ProfileActivity.this, "Some Error Occurred !", Toast.LENGTH_SHORT).show();
                                                                                }

                                                                            }
                                                                        });

                                                                    }
                                                                    else {
                                                                        Toast.makeText(ProfileActivity.this, "Some Error Occured !", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                            }

                            nProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }
                // ------------  UnFriend ------------- //

                if (nCurrentState.equals("friends")) {

                    nFriendsReference.child(uid).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                nFriendsReference.child(user_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            FirebaseDatabase.getInstance().getReference().child("Chats").child(uid).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        FirebaseDatabase.getInstance().getReference().child("Chats").child(user_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                                                    nCurrentState = "not friends";
                                                                    nProfileSendReqBtn.setText("Send Friend Request");

                                                                }
                                                                else {
                                                                    Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });

                                                    }
                                                    else {
                                                        Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                        }
                                        else {
                                            Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                            }

                            nProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }

            }
        });

        nProfileDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nCurrentState.equals("request received")) {

                    nProfileSendReqBtn.setEnabled(false);
                    nProfileDeclineReqBtn.setEnabled(false);

                    nFriendRequestReference.child(uid).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                nFriendRequestReference.child(user_id).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(ProfileActivity.this, "Declined Succesfully", Toast.LENGTH_SHORT).show();
                                            nProfileDeclineReqBtn.setVisibility(View.INVISIBLE);

                                            nProfileSendReqBtn.setText("Send Friend Request");
                                            nCurrentState = "not friends";

                                        }
                                        else {
                                            Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                            else {
                                Toast.makeText(ProfileActivity.this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                            }

                            nProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }

            }
        });

    }

}