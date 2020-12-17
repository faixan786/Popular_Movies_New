package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Model.Image;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    /**
     * This class is used to hold the information for Images Recycler View in
     * <p> {@link MoviesDetailsActivity} and {@link ShowsDetailsActivity} </p>
     */

    private List<Image> images;
    private Context context;

    public ImagesAdapter(List<Image> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = images.get(position);

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + image.getImageUrl())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(images != null) {
            return images.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movieImage)
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            imageView.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://image.tmdb.org/t/p/original" + images.get(getAdapterPosition()).getImageUrl()));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(browserIntent);
            });
        }
    }
}
