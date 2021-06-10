package com.ttl.ritz7chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View groupFragmentView;
//    private ListView listView;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private AdapterGroupList adapterGroupList;
    private ArrayList<modelGroupsList> groupList;
    DatabaseReference groupRef;
    String currentUser="";

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static GroupsFragment newInstance(String param1, String param2) {
//        GroupsFragment fragment = new GroupsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
       groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
       recyclerView = groupFragmentView.findViewById(R.id.rvGroupList);
//       currentUser = user.getUid();
        mAuth = FirebaseAuth.getInstance();

        loadGroupList();
       return groupFragmentView;

    }

    private void loadGroupList() {
        groupList = new ArrayList<>();
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.size();
                groupList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(mAuth.getCurrentUser().getUid()).exists()){
                        modelGroupsList model = ds.getValue(modelGroupsList.class);
                        groupList.add(model);
                    }
                }
                adapterGroupList = new AdapterGroupList(getActivity(),groupList);
                recyclerView.setAdapter(adapterGroupList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //
//    private void getAndDisplayGroups() {
//        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Set<String> set = new HashSet<>();
//                Iterator iterator = snapshot.getChildren().iterator();
//
//                while (iterator.hasNext()){
//                    set.add(((DataSnapshot)iterator.next()).getKey());
//                  //  Toast.makeText(getContext(), "Test check", Toast.LENGTH_SHORT).show( );
//                }
//                groupList.clear();
//                arrayAdapter.addAll(set);
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Test check", Toast.LENGTH_SHORT).show( );
//            }
//        });
//    }
//
//    private void initializeItem() {
//        listView = groupFragmentView.findViewById(R.id.list_view);
//        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
//        listView.setAdapter(arrayAdapter);
//
//    }
}