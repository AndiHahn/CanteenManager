package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.core.Canteen;
import com.example.canteenchecker.canteenmanager.core.Rating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ReviewsFragment extends Fragment {

    //constants
    private static final String TAG = "ReviewsFragment";

    //layout
    private View rootView;

    private RecyclerView rcvReviews;
    private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

    //Canteen
    private Canteen canteen = null;
    private Context context;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        //layout
        //recycler View
        rcvReviews = rootView.findViewById(R.id.rcvReviews);
        context = inflater.getContext();
        rcvReviews.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        rcvReviews.setAdapter(reviewsAdapter);

        getReviews();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void getReviews() {
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
                if (canteen != null) {
                    if (canteen.getRatings() != null) {
                        Log.e(TAG, "Loaded: " + canteen.getRatings().length + " ratings");
                    } else {
                        Log.e(TAG, "Canteen.getRationgs() == null");
                    }
                                    } else {
                    Log.e(TAG, "Canteen == null");
                }

                ReviewsFragment.this.canteen = canteen;

                //refresh Reviews
                reviewsAdapter.displayRatings(canteen.getRatings());
            }
        }.execute();
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txvUsername = itemView.findViewById(R.id.txvUsername);
            private final TextView txvRemark = itemView.findViewById(R.id.txvRemark);
            private final TextView txvRatingPoints = itemView.findViewById(R.id.txvRatingPoints);

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            void updateView(final Rating rating) {
                Log.e(TAG, "Username: " + rating.getUserName());
                txvUsername.setText("User: " + rating.getUserName());
                txvRemark.setText("Remark: " + rating.getRemark());
                txvRatingPoints.setText(String.valueOf(rating.getRatingPoints()));

                /*
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: open canteen details activity
                        itemView.getContext().startActivity(CanteenDetailsActivity.createIntent(itemView.getContext(), canteen.getId()));

                    }
                });
                 */
            }
        }

        private final List<Rating> ratingList = new ArrayList<>();

        void displayRatings(Rating[] ratings) {
            ratingList.clear();
            for(int i = 0; i < ratings.length; i++) {
                ratingList.add(ratings[i]);
            }
            notifyDataSetChanged();
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
}
