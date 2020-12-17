package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;
import in.casetcollege.popularmovies.utils.Constants;

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ShowsViewHolder> {

    private List<Show> shows;
    private Context context;


    public ShowsAdapter(List<Show> shows, Context context) {
        this.shows = shows;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new ShowsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsViewHolder holder, int position) {
        final Show show = shows.get(position);

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + show.getPosterPath())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.posterImage);
        holder.posterTitle.setText(show.getName());
    }


    @Override
    public int getItemCount() {
        if (shows != null) {
            return shows.size();
        }
        return 0;
    }

    public class ShowsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster_image)
        ImageView posterImage;
        @BindView(R.id.poster_title)
        TextView posterTitle;

        public ShowsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, ShowsDetailsActivity.class);
                    intent.putExtra(Constants.SHOW_IMAGE_URL, shows.get(position).getBackdropPath());
                    intent.putExtra(Constants.SHOW_TITLE, shows.get(position).getName());
                    intent.putExtra(Constants.SHOW_DESCRIPTION, shows.get(position).getOverview());
                    intent.putExtra(Constants.SHOW_RATINGS, shows.get(position).getVoteAverage());
                    intent.putExtra(Constants.SHOW_RELEASE_DATE, shows.get(position).getReleaseDate());
                    intent.putExtra(Constants.SHOW_ID, shows.get(position).getId());
                    intent.putExtra(Constants.SHOW_POSTER_IMAGE, shows.get(position).getPosterPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
