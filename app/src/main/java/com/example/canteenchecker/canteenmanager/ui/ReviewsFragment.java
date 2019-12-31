package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.core.ReviewData;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.content.pm.PackageManager;

public class ReviewsFragment extends Fragment {

    //constants
    private static final String TAG = "ReviewsFragment";

    //layout
    private TextView txvAverageRating;
    private TextView txvTotalRatings;
    private RatingBar rtbAverageRating;
    private View viwRatingOne;
    private View viwRatingTwo;
    private View viwRatingThree;
    private View viwRatingFour;
    private View viwRatingFive;

    private View rootView;

    private RecyclerView rcvReviews;
    private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
    private SwipeRefreshLayout srlSwipeRefreshLayout;

    private Context context;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateReviews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        //layout
        txvAverageRating = rootView.findViewById(R.id.txvAverageRating);
        txvTotalRatings = rootView.findViewById(R.id.txvTotalRatings);
        rtbAverageRating = rootView.findViewById(R.id.rtbAverageRating);
        viwRatingOne = rootView.findViewById(R.id.viwRatingsOne);
        viwRatingTwo = rootView.findViewById(R.id.viwRatingsTwo);
        viwRatingThree = rootView.findViewById(R.id.viwRatingsThree);
        viwRatingFour = rootView.findViewById(R.id.viwRatingsFour);
        viwRatingFive = rootView.findViewById(R.id.viwRatingsFive);

        //recycler View
        rcvReviews = rootView.findViewById(R.id.rcvReviews);
        context = inflater.getContext();
        rcvReviews.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        rcvReviews.setAdapter(reviewsAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback());
        itemTouchHelper.attachToRecyclerView(rcvReviews);

        //swipe to refresh layout
        srlSwipeRefreshLayout = rootView.findViewById(R.id.srlSwipeRefreshLayout);
        srlSwipeRefreshLayout.setOnRefreshListener(() -> updateReviews());

        //add reviews fragment dynamically
        Log.e(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        return rootView;
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        public SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);

        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            View itemView = viewHolder.itemView;
            Button btnDustbin = itemView.findViewById(R.id.btnDustbin);
            btnDustbin.setVisibility(View.INVISIBLE);

            Rating rating = reviewsAdapter.getRatingOnPos(position);
            reviewsAdapter.removeSingleRating(position);

            Snackbar snackbar = Snackbar
                    .make(srlSwipeRefreshLayout, "Rating deleted", Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", (v) -> {
                btnDustbin.setVisibility(View.INVISIBLE);
                reviewsAdapter.restoreDeletedItem(position);
                btnDustbin.setVisibility(View.INVISIBLE);
            });

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);

                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        // Snackbar closed on its own
                        new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                try {
                                    Log.e(TAG, String.format("Delete canteen with id %s", params[0]));
                                    return new ServiceProxy().deleteRating(params[0], CanteenManagerApplication.getInstance().getAuthToken());
                                } catch (IOException e) {
                                    Log.e(TAG, String.format("Failed to delete review with id %s", params[0]), e);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                Toast.makeText(getActivity(), "Deleted Rating", Toast.LENGTH_SHORT).show();
                            }
                        }.execute(String.valueOf(rating.getId()));
                    }
                }
            });
            snackbar.show();
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            Button btnDustbin = itemView.findViewById(R.id.btnDustbin);

            if (dX < 0) { // Swiping to the left
                btnDustbin.setVisibility(View.VISIBLE);
            } else { // view is unSwiped
                btnDustbin.setVisibility(View.INVISIBLE);
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    public void updateReviews() {
        new AsyncTask<Void, Void, Canteen>() {
            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getAdminCanteen(CanteenManagerApplication.getInstance().getAuthToken());
                } catch (IOException e) {
                    Log.e(TAG, "Failed to download reviews", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                srlSwipeRefreshLayout.setRefreshing(false);
                if (canteen != null) {
                    updateStatistics(canteen.getId());

                    if (canteen.getRatings() != null) {
                        Log.e(TAG, "Loaded: " + canteen.getRatings().length + " ratings");
                    } else {
                        Log.e(TAG, "Canteen.getRationgs() == null");
                    }
                } else {
                    Log.e(TAG, "Canteen == null");
                }

                //refresh Reviews
                reviewsAdapter.displayRatings(canteen.getRatings());
            }
        }.execute();
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txvUsername = itemView.findViewById(R.id.txvUsername);
            private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
            private final AppCompatRatingBar rtbRating = itemView.findViewById(R.id.rtbRating);

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            void updateView(final Rating rating) {
                txvUsername.setText("User: " + rating.getUserName());
                rtbRating.setRating(rating.getRatingPoints());
                txvRemark.setText("Remark: " + rating.getRemark());
            }
        }

        private static final List<Rating> ratingList = new ArrayList<>();
        private static Rating deletedRating;

        void displayRatings(Rating[] ratings) {
            ratingList.clear();
            if (ratings != null) {
                for(int i = 0; i < ratings.length; i++) {
                    ratingList.add(ratings[i]);
                }
                notifyDataSetChanged();
            }
        }

        void removeSingleRating(int ratingPos) {
            deletedRating = ratingList.get(ratingPos);
            ratingList.remove(ratingPos);
            notifyDataSetChanged();
        }

        void restoreDeletedItem(int ratingPos) {
            ratingList.add(ratingPos, deletedRating);
            notifyDataSetChanged();
        }

        Rating getRatingOnPos(int position) {
            if (position <= ratingList.size()) {
                return ratingList.get(position);
            }
            return null;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reviews_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.updateView(ratingList.get(position));
        }

        @Override
        public int getItemCount() {
            return ratingList.size();
        }
    }

    private void updateStatistics(String canteenId) {
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
        }.execute(canteenId);
    }

    private void setWeight(View view, int value, int maximum) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float weight = ((float)value / maximum);
        view.setLayoutParams(new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height, weight));

    }
}
