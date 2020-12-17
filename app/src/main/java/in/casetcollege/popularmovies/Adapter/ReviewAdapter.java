package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Model.Review;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    /**
     * This class is used to hold the information for Reviews Recycler View in
     * <p> {@link MoviesDetailsActivity} and {@link ShowsDetailsActivity} </p>
     */


    private List<Review> reviewList;
    private Context context;

    public ReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.userName.setText(review.getAuthor());
        holder.userReview.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) {
            return 0;

        }
        return reviewList.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.username)
        TextView userName;
        @BindView(R.id.userReview)
        TextView userReview;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
