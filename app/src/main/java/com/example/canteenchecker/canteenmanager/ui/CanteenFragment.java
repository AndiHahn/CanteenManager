package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CanteenFragment extends Fragment {

    //constants
    private static final String TAG = "CanteenFragment";

    //layout
    private View rootView;

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

    public CanteenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_canteen, container, false);

        //layout
        edtCanteenName = rootView.findViewById(R.id.edtCanteenName);
        edtMenu = rootView.findViewById(R.id.edtMenu);
        edtMenuPrice = rootView.findViewById(R.id.edtMenuPrice);
        edtAddress = rootView.findViewById(R.id.edtAddress);
        edtWebsite = rootView.findViewById(R.id.edtWebsite);
        edtPhoneNumber = rootView.findViewById(R.id.edtPhoneNumber);

        skbAvgWaitingTime = rootView.findViewById(R.id.skbAvgWaitingTime);
        txvAvgWaitingVal = rootView.findViewById(R.id.txvAvgWaitingVal);

        skbAvgWaitingTime.setOnSeekBarChangeListener(new CanteenFragment.SeekbarListener());

        btnSave = rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveCanteen());

        updateCanteen();

        // Inflate the layout for this fragment
        return rootView;
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
                CanteenFragment.this.canteen = canteen;

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
        clearFocusInView();

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
                Toast.makeText(getActivity(), "Canteen successfully stored", Toast.LENGTH_SHORT).show();
                updateCanteen();
            }
        }.execute(getCanteenDetailsFromView());
    }

    private Canteen getCanteenDetailsFromView() {
        return new Canteen(CanteenFragment.this.canteen.getId(),
                ((TextInputEditText)rootView.findViewById(R.id.edtCanteenName)).getText().toString(),
                ((TextInputEditText)rootView.findViewById(R.id.edtMenu)).getText().toString(),
                Float.valueOf(((TextInputEditText)rootView.findViewById(R.id.edtMenuPrice)).getText().toString().replace("€", "").replace(",", ".").substring(1)),
                ((TextInputEditText)rootView.findViewById(R.id.edtAddress)).getText().toString(),
                ((TextInputEditText)rootView.findViewById(R.id.edtWebsite)).getText().toString(),
                ((TextInputEditText)rootView.findViewById(R.id.edtPhoneNumber)).getText().toString(),
                CanteenFragment.this.canteen.getAverageRating(),
                Integer.valueOf(((MaterialTextView)rootView.findViewById(R.id.txvAvgWaitingVal)).getText().toString().replace(" min", ""))
        );
    }

    private void clearFocusInView() {
        rootView.findViewById(R.id.edtCanteenName).clearFocus();
        rootView.findViewById(R.id.edtMenu).clearFocus();
        rootView.findViewById(R.id.edtMenuPrice).clearFocus();
        rootView.findViewById(R.id.edtAddress).clearFocus();
        rootView.findViewById(R.id.edtWebsite).clearFocus();
        rootView.findViewById(R.id.edtPhoneNumber).clearFocus();
        rootView.findViewById(R.id.txvAvgWaitingVal).clearFocus();
    }
}
