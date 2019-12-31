package com.example.canteenchecker.canteenmanager.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import android.content.pm.PackageManager;


public class ReviewsFragment extends Fragment {

    //constants
    private static final String TAG = "ReviewsFragment";

    //layout
    private View rootView;

    private RecyclerView rcvReviews;
    private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();


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

        Log.e(TAG, "onCreateView() ReviewsFragment");
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
            private final Button btnDustbin = itemView.findViewById(R.id.btnDustbin);

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            void updateView(final Rating rating) {
                txvUsername.setText("User: " + rating.getUserName());
                rtbRating.setRating(rating.getRatingPoints());
                txvRemark.setText("Remark: " + rating.getRemark());

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
