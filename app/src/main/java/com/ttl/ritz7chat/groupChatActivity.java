package com.ttl.ritz7chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText userMsgText;
    private TextView groupTitle;
    CircleImageView groupIcon;
    private ImageButton btnSendMsg, btnFile;
    private ScrollView scrollView;
    String getGroupId, currUserId, currUsername, currDate, currTime;
    DatabaseReference databaseReference, groupRef,groupMsgRefKey;
    FirebaseAuth auth, currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getGroupId = getIntent().getExtras().get("groupId").toString();


        auth = FirebaseAuth.getInstance();
        currUserId = auth.getCurrentUser().getUid();
        loadGroupInfo();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UsersChat");
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        initializeFeilds();
        getUserInfo();

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = userMsgText.getText().toString().trim();

                if (TextUtils.isEmpty(msg)){
                    Toast.makeText(groupChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }else {
                saveMsgToDatabase();
                userMsgText.setText("");
                }
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void loadGroupInfo() {

        groupRef.orderByChild("groupId").equalTo(getGroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String grouptitle = "" + ds.child("groupTitle").getValue();
                            String groupDesc = "" + ds.child("groupDescription").getValue();
                            String groupIconUrl = "" + ds.child("groupIcon").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String createdBy = "" + ds.child("createdBy").getValue();
                            groupTitle.setText(grouptitle);
                            try {
                                Picasso.get().load(groupIconUrl).placeholder(R.drawable.group).into(groupIcon);
                            }catch (Exception e){
                               groupIcon.setImageResource(R.drawable.group);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        groupRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    displayMsg(snapshot);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    displayMsg(snapshot);
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    private void displayMsg(DataSnapshot snapshot) {
//        Iterator iterator = snapshot.getChildren().iterator();
//
//        while (iterator.hasNext()){
//            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatMsg = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
//            textViewDisplayMsg.append(chatName + ":\n" + chatMsg + "\n" + chatTime + "    " + chatDate + "\n\n\n");
//            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//        }
//    }

    private void saveMsgToDatabase() {

        String message = userMsgText.getText().toString();
        String msgKey = groupRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Empty message", Toast.LENGTH_SHORT).show();
        }else {

            Calendar cForDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd, yyyy");
            currDate = dateFormat.format(cForDate.getTime());

            Calendar cForTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            currTime = timeFormat.format(cForTime.getTime());

            HashMap<String,Object> groupMsgKey = new HashMap<>();
            groupRef.updateChildren(groupMsgKey);

            groupMsgRefKey = groupRef.child(msgKey);
            HashMap<String,Object> msgInfoMap = new HashMap<>();

            msgInfoMap.put("name", currUsername);
            msgInfoMap.put("message", message);
            msgInfoMap.put("date", currDate);
            msgInfoMap.put("time", currTime);
            groupMsgRefKey.updateChildren(msgInfoMap);

        }
    }

    private void getUserInfo() {

        databaseReference.child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currUsername = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFeilds() {

        toolbar = findViewById(R.id.groupChatLayout);
        groupTitle = findViewById(R.id.groupTitletv);
        groupIcon = findViewById(R.id.groupIcon);
        btnFile = findViewById(R.id.btnFiles);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getGroupName);
        userMsgText = findViewById(R.id.input_groupMsg);
        btnSendMsg = findViewById(R.id.send_msg);
//        scrollView = findViewById(R.id.groupChatScrollview);
//        textViewDisplayMsg = findViewById(R.id.grpChatMsgDisplay);


    }
}