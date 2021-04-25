package com.ttl.ritz7chat;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAddParticipant extends RecyclerView.Adapter<AdapterAddParticipant.holderParticipant>{

    private Context context;
    private ArrayList<modelGroupParticipant> userList;
    String groupId, myGroupRole;

    public AdapterAddParticipant(Context context, ArrayList<modelGroupParticipant> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public holderParticipant onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_group_participant, parent, false);
        return new holderParticipant(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holderParticipant holder, int position) {

        modelGroupParticipant model = userList.get(position);

        String name = model.getName();
        String image = model.getImage();
        String uid = model.getUid();

        holder.nameP.setText(name);
        try {
            Picasso.get().load(image).placeholder(R.drawable.user).into(holder.imageP);
        }catch (Exception e){
            holder.imageP.setImageResource(R.drawable.group);
        }

        checkIfAlreadyExist(model, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups");
                groupRef.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String hisPrevRole = ""+dataSnapshot.child("role").getValue();

                                    String[] options;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Options");
                                    if (myGroupRole.equals("Creator")){
                                        if (hisPrevRole.equals("admin")){
                                            options = new String[]{"Remove Admin", "Remove User"};

                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    if (i == 0){
                                                        removeAdmin(model);
                                                    }else {
                                                        removeParticipant(model);
                                                    }

                                                }
                                            }).show();
//                                            AlertDialog alert = builder.create();
//                                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                            nbutton.setTextColor(Color.BLUE);
//                                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                                            pbutton.setTextColor(Color.BLUE);
                                        }
                                        else if (hisPrevRole.equals("participant")){
                                            options = new String[]{"Make Admin", "Remove User"};

                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    if (i == 0){
                                                        makeAdmin(model);
                                                    }else {
                                                        removeParticipant(model);
                                                    }

                                                }
                                            }).show();
                                        }
                                    }
                                    else if (myGroupRole.equals("admin")){
                                        if (hisPrevRole.equals("Creator")){
                                            Toast.makeText(context, "Creator of group", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (hisPrevRole.equals("admin")) {
                                            options = new String[]{"Remove Admin", "Remove User"};

                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    if (i == 0) {
                                                        removeAdmin(model);
                                                    } else {
                                                        removeParticipant(model);
                                                    }

                                                }
                                            }).show();
//                                            AlertDialog alert = builder.create();
//                                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                            nbutton.setTextColor(Color.BLUE);
//                                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                                            pbutton.setTextColor(Color.BLUE);
                                        }
                                        else if (hisPrevRole.equals("participant")){
                                            options = new String[]{"Make Admin", "Remove User"};

                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    if (i == 0){
                                                        makeAdmin(model);
                                                    }else {
                                                        removeParticipant(model);
                                                    }

                                                }
                                            }).show();
//                                            AlertDialog alert = builder.create();
//                                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                            nbutton.setTextColor(Color.BLUE);
//                                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                                            pbutton.setTextColor(Color.BLUE);

                                        }
                                    }
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Add user to this group?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    addParticipant(model);

                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    }).show();

//                                    Button nbutton = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                    nbutton.setTextColor(Color.BLUE);
//                                    Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
//                                    pbutton.setTextColor(Color.BLUE);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });

    }

    private void addParticipant(modelGroupParticipant model) {

        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid",model.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(model.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void makeAdmin(modelGroupParticipant model) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role","admin");

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
        ref1.child(groupId).child("Participants").child(model.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "The user is admin now", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void removeParticipant(modelGroupParticipant model) {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Groups");
        ref2.child(groupId).child("Participants").child(model.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "User removed from group", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void removeAdmin(modelGroupParticipant model) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role","participant");

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
        ref1.child(groupId).child("Participant").child(model.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "The user is removed as admin.", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkIfAlreadyExist(modelGroupParticipant model, holderParticipant holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String hisRole = ""+dataSnapshot.child("role").getValue();
                            holder.statusP.setText(hisRole);

                        }else {
                            holder.statusP.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class holderParticipant extends RecyclerView.ViewHolder{
        TextView nameP, statusP;
        CircleImageView imageP;

        public holderParticipant(@NonNull View itemView) {
            super(itemView);
            nameP = itemView.findViewById(R.id.user_name);
            statusP = itemView.findViewById(R.id.user_status);
            imageP = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
