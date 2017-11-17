package com.example.tomer.baslo.firebasechat.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tomer.baslo.firebasechat.R;
import com.example.tomer.baslo.firebasechat.User;
import com.example.tomer.baslo.firebasechat.ui.activities.MainActivity;
import com.example.tomer.baslo.firebasechat.ui.activities.UserActivity;
import com.example.tomer.baslo.firebasechat.utils.Constants;
import com.example.tomer.baslo.firebasechat.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Tomer on 10/11/2017.
 */

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_register:
                onRegister(view);
                break;
        }
    }

    private void onRegister(View view) {
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();

        register(getActivity(), emailId, password);
        mProgressDialog.show();
    }

    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        Toast.makeText(getActivity(), "Registration Successful!", Toast.LENGTH_SHORT).show();
        addUser(getActivity().getApplicationContext(), firebaseUser);
    }

    public void onRegistrationFailure(String message) {
        mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));
        Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(getActivity(), "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }

    public void onAddUserSuccess(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        MainActivity.startActivity(getActivity(),
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void onAddUserFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void register(Activity activity, String email, String password) {
        performFirebaseRegistration(activity, email, password);
    }

    public void onSuccess(FirebaseUser firebaseUser) {
        onRegistrationSuccess(firebaseUser);
    }

    public void onFailure(String message) {
        onRegistrationFailure(message);
    }

    public void performFirebaseRegistration(Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            onFailure(task.getException().getMessage());
                        } else {
                            // Add the user to users table.
                            /*DatabaseReference database= FirebaseDatabase.getInstance().getReference();
                            User user = new User(task.getResult().getUser().getUid(), email);
                            database.child("users").push().setValue(user);*/

                            onSuccess(task.getResult().getUser());
                        }
                    }
                });
    }

    public void addUser(Context context, FirebaseUser firebaseUser) {
        addUserToDatabase(context, firebaseUser);
    }

    public void onSuccess(String message) {
        onAddUserSuccess(message);
    }

    public void onFailureAdd(String message) {
        onAddUserFailure(message);
    }

    public void addUserToDatabase(final Context context, FirebaseUser firebaseUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN));
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onSuccess(context.getString(R.string.user_successfully_added));
                        } else {
                            onFailureAdd(context.getString(R.string.user_unable_to_add));
                        }
                    }
                });
    }
}
