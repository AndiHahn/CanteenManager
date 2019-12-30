package com.example.canteenchecker.canteenmanager.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ReviewsFragment extends Fragment {

    //layout
    private View rootView;

    private RecyclerView rcvReviews;
    //private ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

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
        rcvReviews.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        //rcvReviews.setAdapter(reviewsAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }

    /*
    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txvName = itemView.findViewById(R.id.txvName);
            private final TextView txvSetMeal = itemView.findViewById(R.id.txvSetMeal);
            private final TextView txvSetMealPrice = itemView.findViewById(R.id.txvSetMealPrice);
            private final RatingBar rtbAverageRating = itemView.findViewById(R.id.rtbAverageRating);
            private final TextView txvAverageRating = itemView.findViewById(R.id.txvAverageRating);

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            void updateView(final Rating canteen) {
                txvName.setText(canteen.getName());
                txvSetMeal.setText(canteen.getSetMeal());
                txvSetMealPrice.setText(NumberFormat.getCurrencyInstance().format(canteen.getSetMealPrice()));
                rtbAverageRating.setRating(canteen.getAverageRating());
                txvAverageRating.setText(NumberFormat.getNumberInstance().format(canteen.getAverageRating()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: open canteen details activity
                        itemView.getContext().startActivity(CanteenDetailsActivity.createIntent(itemView.getContext(), canteen.getId()));

                    }
                });
            }
        }

        private final List<Canteen> canteenList = new ArrayList<>();

        void displayCanteens(Collection<Canteen> canteens) {
            canteenList.clear();
            if (canteens != null) {
                canteenList.addAll(canteens);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_canteens_item, parent, false);
            //return new ViewHolder(view);
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //holder.updateView(canteenList.get(position));
        }

        @Override
        public int getItemCount() {
            return canteenList.size();
        }
    }

     */
}
