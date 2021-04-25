package com.ttl.ritz7chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantAddActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String groupId;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    String myGroupRole;

    private ArrayList<modelGroupParticipant> userList;
    private AdapterAddParticipant adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);

//        actionBar = getSupportActionBar();
//        actionBar.setTitle("Add Participant");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

        firebaseAuth  = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.rvGroupPart);

        groupId = getIntent().getStringExtra("groupId");
        loadGroupInfo();

    }

    private void getAllUsers() {
        userList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UsersChat");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    modelGroupParticipant model = ds.getValue(modelGroupParticipant.class);

                    if (!firebaseAuth.getUid().equals(model.getUid())){
                        userList.add(model);
                    }
                }

                adapter = new AdapterAddParticipant(GroupParticipantAddActivity.this, userList, ""+groupId,""+myGroupRole);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupInfo() {

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String groupId = "" + ds.child("groupId").getValue();
                    final String grouptitle = "" + ds.child("groupTitle").getValue();
                    String groupDesc = "" + ds.child("groupDescription").getValue();
                    String groupIconUrl = "" + ds.child("groupIcon").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();
                    String createdBy = "" + ds.child("createdBy").getValue();

                    ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        myGroupRole = ""+dataSnapshot.child("role").getValue();
//                                        actionBar.setTitle(grouptitle + "(" + myGroupRole + ")");

                                        getAllUsers();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}