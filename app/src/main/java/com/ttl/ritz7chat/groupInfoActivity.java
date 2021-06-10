package com.ttl.ritz7chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class groupInfoActivity extends AppCompatActivity {

    private String groupId;
    ImageView groupImg;
    TextView textDesc, textCreated,textEdtGroup,textAddPart, textLeave, textPart;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        groupId = getIntent().getStringExtra("groupId");

        groupImg = findViewById(R.id.groupIconIv);
        
    }
}