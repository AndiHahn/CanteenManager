package com.example.canteenchecker.canteenmanager.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.IOException;
import java.text.NumberFormat;

public class CanteenActivity extends AppCompatActivity {

    //constants
    private static final String TAG = "CanteenActivity";

    //layout
    private TextInputEditText edtCanteenName;
    private TextInputEditText edtMenu;
    private TextInputEditText edtMenuPrice;
    private TextInputEditText edtAddress;
    private TextInputEditText edtWebsite;
    private TextInputEditText edtPhoneNumber;
    private AppCompatSeekBar skbAvgWaitingTime;
    private MaterialTextView txvAvgWaitingVal;

    private Button btnSave;

    //Canteen
    private Canteen canteen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen);

        //layout
        edtCanteenName = findViewById(R.id.edtCanteenName);
        edtMenu = findViewById(R.id.edtMenu);
        edtMenuPrice = findViewById(R.id.edtMenuPrice);
        edtAddress = findViewById(R.id.edtAddress);
        edtWebsite = findViewById(R.id.edtWebsite);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);

        skbAvgWaitingTime = findViewById(R.id.skbAvgWaitingTime);
        txvAvgWaitingVal = findViewById(R.id.txvAvgWaitingVal);

        skbAvgWaitingTime.setOnSeekBarChangeListener(new SeekbarListener());

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveCanteen());
    }

    private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            //set text in View
            txvAvgWaitingVal.setText(progress + " min");
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private Object update() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check login
        if (!CanteenManagerApplication.getInstance().isAuthenticated()) {
            startActivity(LoginActivity.createIntent(getBaseContext()));
        } else {
            updateCanteen();
        }
    }

    private void updateCanteen() {
        new AsyncTask<Void, Void, Canteen>() {
            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getAdminCanteen(CanteenManagerApplication.getInstance().getAuthToken());
                } catch (IOException e) {
                    Log.e(TAG, "Failed to download canteen", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                CanteenActivity.this.canteen = canteen;

                //update UI
                if (canteen != null) {
                    edtCanteenName.setText(canteen.getName());
                    edtMenu.setText(canteen.getSetMeal());
                    edtMenuPrice.setText(NumberFormat.getCurrencyInstance().format(canteen.getSetMealPrice()));
                    edtAddress.setText(canteen.getLocation());
                    edtWebsite.setText(canteen.getWebsite());
                    edtPhoneNumber.setText(canteen.getPhoneNumber());
                    skbAvgWaitingTime.setProgress(canteen.getAverageWaitingTime());
                    txvAvgWaitingVal.setText(canteen.getAverageWaitingTime() + " min");
                }
            }
        }.execute();
    }

    private void saveCanteen() {
        new AsyncTask<Canteen, Void, String>() {
            @Override
            protected String doInBackground(Canteen... canteen) {
                try {
                    return new ServiceProxy().updateAdminCanteen(CanteenManagerApplication.getInstance().getAuthToken(), canteen[0]);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to update canteen", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(CanteenActivity.this, "Canteen successfully stored", Toast.LENGTH_SHORT).show();
                updateCanteen();
            }
        }.execute(getCanteenDetailsFromView());
    }

    private Canteen getCanteenDetailsFromView() {
        return new Canteen(CanteenActivity.this.canteen.getId(),
                            ((TextInputEditText)findViewById(R.id.edtCanteenName)).getText().toString(),
                            ((TextInputEditText)findViewById(R.id.edtMenu)).getText().toString(),
                            Float.valueOf(((TextInputEditText)findViewById(R.id.edtMenuPrice)).getText().toString().replace("€", "").replace(",", ".").substring(1)),
                            ((TextInputEditText)findViewById(R.id.edtAddress)).getText().toString(),
                            ((TextInputEditText)findViewById(R.id.edtWebsite)).getText().toString(),
                            ((TextInputEditText)findViewById(R.id.edtPhoneNumber)).getText().toString(),
                            CanteenActivity.this.canteen.getAverageRating(),
                            Integer.valueOf(((MaterialTextView)findViewById(R.id.txvAvgWaitingVal)).getText().toString().replace(" min", ""))
                            );
    }
}