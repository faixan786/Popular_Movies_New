package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
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
import in.casetcollege.popularmovies.Model.Cast;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    /**
     * This class is used to hold the information for Cast Recycler View in
     * <p> {@link MoviesDetailsActivity} and {@link ShowsDetailsActivity} </p>
     */

    private List<Cast> castList;
    private Context context;

    public CastAdapter(List<Cast> castList, Context context) {
        this.castList = castList;
        this.context = context;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_list_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        Cast cast = castList.get(position);

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + cast.getImageUrl())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.castImage);

        holder.castName.setText(cast.getName());
        holder.characterName.setText(cast.getCharacter());
    }

    @Override
    public int getItemCount() {
        if(castList == null)
            return 0;

        return castList.size();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cast_image)
        ImageView castImage;
        @BindView(R.id.cast_name)
        TextView castName;
        @BindView(R.id.cast_character_name)
        TextView characterName;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
