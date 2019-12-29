package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText edtUserName;
    private EditText edtPassword;
    private Button btnLogin;

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            return new ServiceProxy().authenticate(params[0], params[1]);
                        } catch (IOException e) {
                            Log.e(TAG, String.format("Login failed for username %s.", params[0]), e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String authToken) {
                        if(authToken != null) {
                            CanteenManagerApplication.getInstance().setAuthToken(authToken);
                            setResult(RESULT_OK);
                            //Start CanteenActivity
                            finish();
                        } else {
                            //reset password field
                            edtPassword.setText(null);
                            Toast.makeText(LoginActivity.this, "Login failed - Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(edtUserName.getText().toString(), edtPassword.getText().toString());
            }
        });
    }
}
