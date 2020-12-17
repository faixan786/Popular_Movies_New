package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Model.Trailer;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    /**
     * This class is used to hold the information for Trailer Recycler View in
     * <p> {@link MoviesDetailsActivity} and {@link ShowsDetailsActivity} </p>
     */

    private List<Trailer> trailers;
    private Context context;

    public TrailerAdapter(List<Trailer> trailers, Context context) {
        this.trailers = trailers;
        this.context = context;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        final Trailer trailer = trailers.get(position);
        final String BASE_URL = "https://www.youtube.com/watch?v=";
        String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/";
        String YOUTUBE_IMAGE_EXT = "/0.jpg";
        final String YOUTUBE_APP_URI = "vnd.youtube:";
        Picasso.get().load(YOUTUBE_THUMBNAIL_URL + trailer.getKey() + YOUTUBE_IMAGE_EXT)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URI + trailer.getKey()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL + trailer.getKey()));
            if (appIntent.resolveActivity(context.getPackageManager()) != null) {
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(appIntent);
            } else if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(webIntent);
            } else {
                Toast.makeText(context, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trailers == null) {
            return 0;
        }
        return trailers.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailerImage)
        ImageView imageView;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
