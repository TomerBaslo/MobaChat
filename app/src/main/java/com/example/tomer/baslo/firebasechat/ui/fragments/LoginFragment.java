package com.example.tomer.baslo.firebasechat.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.tomer.baslo.firebasechat.ui.activities.MainActivity;
import com.example.tomer.baslo.firebasechat.ui.activities.RegisterActivity;
import com.example.tomer.baslo.firebasechat.ui.activities.UserActivity;
import com.example.tomer.baslo.firebasechat.utils.Constants;
import com.example.tomer.baslo.firebasechat.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/**
 * Created by Tomer on 10/11/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText mETxtEmail, mETxtPassword;
    private Button mBtnLogin, mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mBtnLogin = (Button) view.findViewById(R.id.button_login);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void login(Activity activity, String email, String password) {
        performFirebaseLogin(activity, email, password);
    }

    public void onSuccess(String message) {
        onLoginSuccess(message);
    }

    public void onFailure(String message) {
        onLoginFailure(message);
    }

    public void performFirebaseLogin(final Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            onSuccess(task.getResult().toString());
                            updateFirebaseToken(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(activity.getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));
                        } else {
                            onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_login:
                onLogin(view);
                break;
            case R.id.button_register:
                onRegister(view);
                break;
        }
    }

    private void onLogin(View view) {
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();

        if (emailId.length() > 2 && password.length() >= 6) {
            login(getActivity(), emailId, password);
            mProgressDialog.show();
        } else {
            Toast.makeText(getContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void onRegister(View view) {
        RegisterActivity.startActivity(getActivity());
    }

    public void onLoginSuccess(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
        MainActivity.startActivity(getActivity(),
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void onLoginFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
