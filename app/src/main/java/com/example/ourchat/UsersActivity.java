package com.example.ourchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar nToolbar;
    private RecyclerView nUserslist;
    private DatabaseReference nUsersDatabase;
    private FirebaseUser nUser;
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter;
    SwipeRefreshLayout nSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        nToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nUser = FirebaseAuth.getInstance().getCurrentUser();
        nUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        nUserslist = (RecyclerView) findViewById(R.id.users_list);
        nUserslist.setHasFixedSize(true);
        nUserslist.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {

        super.onStart();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(5)
                .setPageSize(12)
                .build();


        FirebaseRecyclerOptions<Users> value = new FirebaseRecyclerOptions.Builder<Users>()
                .setLifecycleOwner(this)
                .setQuery(nUsersDatabase, Users.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(value) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder Holder, int position, @NonNull Users model) {


                final String user_id = getRef(position).getKey();
                Holder.setName(model.getName());
                Holder.setStatus(model.getStatus());
                Holder.setImage(model.getImage(), getApplicationContext());

                if (user_id.equals(nUser.getUid())) {
                    Holder.nView.setVisibility(View.GONE);
                    Holder.nView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                Holder.nView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ProfileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                        ProfileIntent.putExtra("user_id", user_id);
                        startActivity(ProfileIntent);

                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                return new UsersViewHolder(view);
            }
        };


        nUserslist.setAdapter(firebaseRecyclerAdapter);


        /*
        firebaseRecyclerAdapter.startListening();

        nUsersDatabase.keepSynced(true);

        nSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nSwipeRefreshLayout.setRefreshing(false);
            }
        });

         */




    }


    //@Override
   // protected void onStop() {
       // super.onStop();

        //firebaseRecyclerAdapter.stopListening();
    //}


    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        View nView;
        TextView singleUserName;
        TextView singleUserStatus;
        CircleImageView singleUserimage;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            nView = itemView;

        }

        public void setName(String name) {
            singleUserName = nView.findViewById(R.id.singleUserName);
            singleUserName.setText(name);
        }

        public void setStatus(String status) {
            singleUserStatus = nView.findViewById(R.id.singleUserStatus);
            singleUserStatus.setText(status);
        }

        public void setImage(String image, Context ctx) {

            singleUserimage = nView.findViewById(R.id.singleUserPic);
            if (!image.equals("default") || !image.equals("") || !image.equals(null)) {
                Picasso.get().load(image).placeholder(R.drawable.default_avater).into(singleUserimage);
            }
        }

    }
}


