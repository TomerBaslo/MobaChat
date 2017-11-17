package com.example.tomer.baslo.firebasechat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tomer.baslo.firebasechat.Game;
import com.example.tomer.baslo.firebasechat.R;
import com.example.tomer.baslo.firebasechat.User;
import com.example.tomer.baslo.firebasechat.ui.activities.ChatActivity;
import com.example.tomer.baslo.firebasechat.ui.adapters.UserRecyclerAdapter;
import com.example.tomer.baslo.firebasechat.utils.Constants;
import com.example.tomer.baslo.firebasechat.utils.ItemSup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tomer on 10/11/2017.
 */

public class UsersFragment extends Fragment implements ItemSup.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String ARG_GAME = "game";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private UserRecyclerAdapter mUserListingRecyclerAdapter;

    private Game mGame;

    public static UsersFragment newInstance(String type, Game game) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putSerializable(ARG_GAME, game);
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mGame = (Game) getArguments().getSerializable(ARG_GAME);
        View fragmentView = inflater.inflate(R.layout.fragment_users, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        getUsers();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ItemSup.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getUsers();
    }

    private void getUsers() {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {

        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_ALL)) {
            getAllUsers();
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ChatActivity.startActivity(getActivity(),
                mUserListingRecyclerAdapter.getUser(position).email,
                mUserListingRecyclerAdapter.getUser(position).uid,
                mUserListingRecyclerAdapter.getUser(position).firebaseToken);
    }

    public void onGetAllUsersSuccess(List<User> users) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUserListingRecyclerAdapter = new UserRecyclerAdapter(users);
        mRecyclerViewAllUserListing.setAdapter(mUserListingRecyclerAdapter);
        mUserListingRecyclerAdapter.notifyDataSetChanged();
    }

    public void onGetAllUsersFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    public void getAllUsers() {
        getAllUsersFromFirebase();
    }

    public void onGetAllUsersFail(String message) {
        onGetAllUsersFailure(message);
    }

    public void getAllUsersFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<User> users = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    if (!TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        // making sure the user is interested in the same game:
                        if (mGame != null)
                        switch(mGame){
                            case LEAGUE_OF_LEGENDS:
                                if(user.leagueOfLegends)
                                    users.add(user);
                                break;
                            case DOTA2:
                                if(user.dota2)
                                    users.add(user);
                                break;
                            case SMITE:
                                if(user.smite)
                                    users.add(user);
                                break;
                            case HEROES_OF_THE_STORM:
                                if(user.heroesOfTheStorm)
                                    users.add(user);
                                break;
                            case HEROES_OF_NEWERTH:
                                if(user.heroesOfNewerth)
                                    users.add(user);
                                break;
                        }
                    }
                }
                onGetAllUsersSuccess(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onGetAllUsersFail(databaseError.getMessage());
            }
        });
    }
}
