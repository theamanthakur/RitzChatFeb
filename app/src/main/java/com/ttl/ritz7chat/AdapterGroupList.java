package com.ttl.ritz7chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupList extends RecyclerView.Adapter<AdapterGroupList.holderGroupList>{

    private Context context;
    private ArrayList<modelGroupsList> groupList;

    public AdapterGroupList(Context context, ArrayList<modelGroupsList> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public holderGroupList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group_chat, parent, false);
        return new holderGroupList(view);

    }

    @Override
    public void onBindViewHolder(@NonNull holderGroupList holder, int position) {

        modelGroupsList model = groupList.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.grpName.setText(groupTitle);

        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.group).into(holder.imageView);
        }catch (Exception e){
            holder.imageView.setImageResource(R.drawable.group);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(context,groupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class holderGroupList extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView grpName, sender, grpMsg;

        public holderGroupList(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.groupImage);
            grpName = itemView.findViewById(R.id.textgrpName);
            sender = itemView.findViewById(R.id.textmsgsender);
            grpMsg = itemView.findViewById(R.id.textmsg);

        }
    }
}
