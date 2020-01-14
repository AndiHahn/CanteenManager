package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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

import static android.app.Activity.RESULT_OK;

public class CanteenFragment extends Fragment implements TextWatcher {

    //constants
    private static final String TAG = "CanteenFragment";
    private static final int MODIFY_LOCATION_ON_MAP = 1;  // The request code

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
    private Button btnOpenMap;

    private Button btnSave;

    //Canteen
    private Canteen canteen = null;
    private int loadedCanteen = 0;

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

        //Map Button
        btnOpenMap = rootView.findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            if (canteen != null) {
                this.canteen.setLocation(edtAddress.getText().toString());
                startActivityForResult(MapActivity.createIntent(v.getContext(), canteen.getLocation()), MODIFY_LOCATION_ON_MAP);
            }
        });

        skbAvgWaitingTime.setOnSeekBarChangeListener(new CanteenFragment.SeekbarListener());

        btnSave = rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveCanteen());

        //Add text change listener to be evaluated by TextWatcher
        edtCanteenName.addTextChangedListener(this);
        edtMenu.addTextChangedListener(this);
        edtMenuPrice.addTextChangedListener(this);
        edtAddress.addTextChangedListener(this);

        updateCanteen(true);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MODIFY_LOCATION_ON_MAP) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra(MapActivity.ADDRESS_KEY);
                this.canteen.setLocation(result);
                edtAddress.setText(canteen.getLocation());
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        validateInputFields();
    }

    private void validateInputFields() {
        boolean formHasError = false;

        //validate Canteen Name
        String canteenName = ((TextInputEditText)rootView.findViewById(R.id.edtCanteenName)).getText().toString();
        if (canteenName.length() > 0) {
            edtCanteenName.setError(null);
        } else {
            formHasError = true;
            edtCanteenName.setError("Name must not be empty!");
        }

        //validate Menu Name
        String menuName = ((TextInputEditText)rootView.findViewById(R.id.edtMenu)).getText().toString();
        if (menuName.length() > 0) {
            edtMenu.setError(null);
        } else {
            formHasError = true;
            edtMenu.setError("Name must not be empty!");
        }

        String value = ((TextInputEditText)rootView.findViewById(R.id.edtMenuPrice)).getText().toString();
        if (value.length() > 0) {
            Float price = Float.valueOf(value.replace(",", ".").substring(1));

            if (price > 0) {
                edtMenuPrice.setError(null);
            } else {
                formHasError = true;
                edtMenuPrice.setError("Price must be > 0€!");
            }
        }

        String address = ((TextInputEditText)rootView.findViewById(R.id.edtAddress)).getText().toString();
        if (address.length() > 0) {
            edtAddress.setError(null);
        } else {
            formHasError = true;
            edtAddress.setError("Name must not be empty!");
        }

        btnSave.setEnabled(!formHasError);
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

    private void updateCanteen(boolean updateOnlyIfNewCanteen) {
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

                if (canteen != null) {
                    if (updateOnlyIfNewCanteen && CanteenFragment.this.loadedCanteen == Integer.valueOf(canteen.getId())) {
                        return;
                    }
                    CanteenFragment.this.loadedCanteen = Integer.valueOf(canteen.getId());
                    updateCanteenDetailsInView(canteen);
                    validateInputFields();
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
                updateCanteen(false);
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
                Integer.valueOf(((MaterialTextView)rootView.findViewById(R.id.txvAvgWaitingVal)).getText().toString().replace(" min", "")),
                null
        );
    }

    private void updateCanteenDetailsInView(Canteen canteen) {
        edtCanteenName.setText(canteen.getName());
        edtMenu.setText(canteen.getSetMeal());
        edtMenuPrice.setText(NumberFormat.getCurrencyInstance().format(canteen.getSetMealPrice()).replace("€", ""));
        edtAddress.setText(canteen.getLocation());
        edtWebsite.setText(canteen.getWebsite());
        edtPhoneNumber.setText(canteen.getPhoneNumber());
        skbAvgWaitingTime.setProgress(canteen.getAverageWaitingTime());
        txvAvgWaitingVal.setText(canteen.getAverageWaitingTime() + " min");
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
