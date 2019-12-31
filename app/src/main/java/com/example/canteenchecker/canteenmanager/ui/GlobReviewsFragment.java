package com.example.canteenchecker.canteenmanager.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.ReviewData;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.NumberFormat;

public class GlobReviewsFragment extends Fragment {

    private static final String CANTEEN_ID_KEY = "CanteenId";
    private static final String TAG = "Reviews Fragment";
    private static final int LOGIN_FOR_REVIEW_CREATION = 1;
    private TextView txvAverageRating;
    private TextView txvTotalRatings;
    private RatingBar rtbAverageRating;
    private View viwRatingOne;
    private View viwRatingTwo;
    private View viwRatingThree;
    private View viwRatingFour;
    private View viwRatingFive;

    public static Fragment create(String canteenId) {
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(CANTEEN_ID_KEY, canteenId);
        reviewsFragment.setArguments(arguments);
        return reviewsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_glob_reviews, container, false);

        txvAverageRating = rootView.findViewById(R.id.txvAverageRating);
        txvTotalRatings = rootView.findViewById(R.id.txvTotalRatings);
        rtbAverageRating = rootView.findViewById(R.id.rtbAverageRating);
        viwRatingOne = rootView.findViewById(R.id.viwRatingsOne);
        viwRatingTwo = rootView.findViewById(R.id.viwRatingsTwo);
        viwRatingThree = rootView.findViewById(R.id.viwRatingsThree);
        viwRatingFour = rootView.findViewById(R.id.viwRatingsFour);
        viwRatingFive = rootView.findViewById(R.id.viwRatingsFive);

        updateReviews();

        return rootView;
    }

    private void updateReviews() {
        new AsyncTask<String, Void, ReviewData>() {

            @Override
            protected ReviewData doInBackground(String... params) {
                try {
                    return new ServiceProxy().getReviewsDataForCanteen(params[0]);
                } catch (IOException e) {
                    Log.e(TAG, String.format("Downloading of reviews of canteen with id %s failed.", params[0]), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ReviewData reviewData) {
                if(reviewData != null) {
                    txvAverageRating.setText(NumberFormat.getNumberInstance().format(reviewData.getAverageRating()));
                    txvTotalRatings.setText(NumberFormat.getNumberInstance().format(reviewData.getTotalRatings()));
                    rtbAverageRating.setRating(reviewData.getAverageRating());
                    setWeight(viwRatingOne, reviewData.getRatingsOne(), reviewData.getTotalRatingsOfMostCommonGrade());
                    setWeight(viwRatingTwo, reviewData.getRatingsTwo(), reviewData.getTotalRatingsOfMostCommonGrade());
                    setWeight(viwRatingThree, reviewData.getRatingsThree(), reviewData.getTotalRatingsOfMostCommonGrade());
                    setWeight(viwRatingFour, reviewData.getRatingsFour(), reviewData.getTotalRatingsOfMostCommonGrade());
                    setWeight(viwRatingFive, reviewData.getRatingsFive(), reviewData.getTotalRatingsOfMostCommonGrade());

                } else {
                    txvAverageRating.setText(null);
                    txvTotalRatings.setText(null);
                    rtbAverageRating.setRating(0);
                    setWeight(viwRatingOne, 0, 1);
                    setWeight(viwRatingTwo, 0, 1);
                    setWeight(viwRatingThree, 0, 1);
                    setWeight(viwRatingFour, 0, 1);
                    setWeight(viwRatingFive, 0, 1);
                }
            }
        }.execute(getCanteenId());
    }

    private String getCanteenId() {
        return getArguments().getString(CANTEEN_ID_KEY);
    }

    private void setWeight(View view, int value, int maximum) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float weight = ((float)value / maximum);
        view.setLayoutParams(new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height, weight));

    }
}
