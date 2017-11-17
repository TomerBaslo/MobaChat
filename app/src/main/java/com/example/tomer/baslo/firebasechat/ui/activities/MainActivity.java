package com.example.tomer.baslo.firebasechat.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.tomer.baslo.firebasechat.Game;
import com.example.tomer.baslo.firebasechat.R;
import com.example.tomer.baslo.firebasechat.ui.fragments.ChatFragment;
import com.example.tomer.baslo.firebasechat.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Button mLolButton, mDotaButton, mSmiteButton, mHotsButton, mHonButton;
    private CheckBox mLolCheck, mDotaCheck, mSmiteCheck, mHotsCheck, mHonCheck;
    private Game mChosenGame = null;
    public final static String GAME_KEY = "GameKey";
    public final static String LOBBY_BUNDLE_KEY = "LobbyBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        init();

        sutupInfoButton();
        sutupButtons();
        sutupCheckBoxes();
        checkProperCheckboxes();
        enableProperButtons();

    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set toolbar title
        if (getIntent().getExtras() != null) {
            mToolbar.setTitle(getIntent().getExtras().getString(Constants.ARG_RECEIVER));
        }
    }

    private void sutupInfoButton() {
        Button infoButton = (Button) findViewById(R.id.info_button);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("You may check the games you are interested in, so orders could see you in the relevant lobbies. You may also press a button to enter the relevant lobby.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Simply dismiss.
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Button logoutButton = (Button) findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }
    public void logout() {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this,"Successfully logged out!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this,"No user logged in yet!", Toast.LENGTH_SHORT).show();
            }
    }

    private void sutupButtons() {

        mLolButton = (Button) findViewById(R.id.lol_button);
        mDotaButton = (Button) findViewById(R.id.dota_button);
        mSmiteButton = (Button) findViewById(R.id.smite_button);
        mHotsButton = (Button) findViewById(R.id.hots_button);
        mHonButton = (Button) findViewById(R.id.hon_button);

        mLolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenGame = Game.LEAGUE_OF_LEGENDS;
                startLobbyActivity();
            }
        });
        mDotaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenGame = Game.DOTA2;
                startLobbyActivity();
            }
        });
        mSmiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenGame = Game.SMITE;
                startLobbyActivity();
            }
        });
        mHotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenGame = Game.HEROES_OF_THE_STORM;
                startLobbyActivity();
            }
        });
        mHonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenGame = Game.HEROES_OF_NEWERTH;
                startLobbyActivity();
            }
        });

    }

    private void sutupCheckBoxes() {

        mLolCheck = (CheckBox) findViewById(R.id.lol_check);
        mDotaCheck = (CheckBox) findViewById(R.id.dota_check);
        mSmiteCheck = (CheckBox) findViewById(R.id.smite_check);
        mHotsCheck = (CheckBox) findViewById(R.id.hots_check);
        mHonCheck = (CheckBox) findViewById(R.id.hon_check);

        // Update firebase in each listener according to the checkbox state:

        mLolCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mLolButton.setEnabled(true);
                    updateGameInterest(Game.LEAGUE_OF_LEGENDS, true);
                }
                else{
                    mLolButton.setEnabled(false);
                    updateGameInterest(Game.LEAGUE_OF_LEGENDS, false);
                }
            }
        });
        mDotaCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDotaButton.setEnabled(true);
                    updateGameInterest(Game.DOTA2, true);
                }
                else{
                    mDotaButton.setEnabled(false);
                    updateGameInterest(Game.DOTA2, false);
                }
            }
        });
        mSmiteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSmiteButton.setEnabled(true);
                    updateGameInterest(Game.SMITE, true);
                }
                else{
                    mSmiteButton.setEnabled(false);
                    updateGameInterest(Game.SMITE, false);
                }
            }
        });
        mHotsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mHotsButton.setEnabled(true);
                    updateGameInterest(Game.HEROES_OF_THE_STORM, true);
                }
                else{
                    mHotsButton.setEnabled(false);
                    updateGameInterest(Game.HEROES_OF_THE_STORM, false);
                }
            }
        });
        mHonCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mHonButton.setEnabled(true);
                    updateGameInterest(Game.HEROES_OF_NEWERTH, true);
                }
                else{
                    mHonButton.setEnabled(false);
                    updateGameInterest(Game.HEROES_OF_NEWERTH, false);
                }
            }
        });

    }

    private void checkProperCheckboxes() {

        // Check the CheckBoxes according to the user's preferences that are saved in firebase:

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.child(Constants.ARG_USERS)
                .child(uid)
                .child(Game.LEAGUE_OF_LEGENDS.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class).equals(true)){
                            mLolCheck.setChecked(true);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        database.child(Constants.ARG_USERS)
                .child(uid)
                .child(Game.DOTA2.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class).equals(true)){
                            mDotaCheck.setChecked(true);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        database.child(Constants.ARG_USERS)
                .child(uid)
                .child(Game.SMITE.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class).equals(true)){
                            mSmiteCheck.setChecked(true);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        database.child(Constants.ARG_USERS)
                .child(uid)
                .child(Game.HEROES_OF_THE_STORM.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class).equals(true)){
                            mHotsCheck.setChecked(true);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        database.child(Constants.ARG_USERS)
                .child(uid)
                .child(Game.HEROES_OF_NEWERTH.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(Boolean.class).equals(true)){
                            mHonCheck.setChecked(true);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

    }

    private void enableProperButtons() {

        // Enable buttons according to to checkboxes:

        if(mLolCheck.isChecked()) mLolButton.setEnabled(true);
        else mLolButton.setEnabled(false);

        if(mDotaCheck.isChecked()) mDotaButton.setEnabled(true);
        else mDotaButton.setEnabled(false);

        if(mSmiteCheck.isChecked()) mSmiteButton.setEnabled(true);
        else mSmiteButton.setEnabled(false);

        if(mHotsCheck.isChecked()) mHotsButton.setEnabled(true);
        else mHotsButton.setEnabled(false);

        if(mHonCheck.isChecked()) mHonButton.setEnabled(true);
        else mHonButton.setEnabled(false);
    }

    private void startLobbyActivity() {

        // Creating an intent:
        Intent gameIntent = new Intent(this, UserActivity.class);

        // Creating a bundle for the intent and setting up the bundle:
        Bundle bundle = new Bundle();
        bundle.putSerializable(GAME_KEY, mChosenGame);

        // Put bundle in intent:
        gameIntent.putExtra(LOBBY_BUNDLE_KEY, bundle);

        // Starting the Game Activity:
        startActivity(gameIntent);

    }

    private void updateGameInterest(Game game, boolean isInterested) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.ARG_USERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(game.toString())
                .setValue(isInterested)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Game interest has been updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }
}
