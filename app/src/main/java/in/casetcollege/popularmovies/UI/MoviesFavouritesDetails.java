package in.casetcollege.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.utils.AppExecutors;
import in.casetcollege.popularmovies.utils.Constants;

public class MoviesFavouritesDetails extends AppCompatActivity {
    @BindView(R.id.rootLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.img_poster)
    ImageView backdrop;
    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.movie_overview)
    TextView overview;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.movie_ratings)
    TextView ratings;
    @BindView(R.id.poster)
    ImageView circleBackdrop;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fav_btn)
    FloatingActionButton favBtn;

    private AppDatabase mdb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_favourites_detail_activity);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mdb = AppDatabase.getInstance(this);
        Intent intent = getIntent();
        String movieTitle = intent.getStringExtra(Constants.MOVIE_TITLE);
        String backdropImage = intent.getStringExtra(Constants.MOVIE_BACKDROP_PATH);
        String overviewMovie = intent.getStringExtra(Constants.MOVIE_DESCRIPTION);
        String releaseDate = intent.getStringExtra(Constants.MOVIE_RELEASE_DATE);
        String rating = intent.getStringExtra(Constants.MOVIE_RATINGS);

        title.setText(movieTitle);
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + backdropImage).placeholder(R.mipmap.ic_custom_launcher).into(backdrop);
        overview.setText(overviewMovie);
        this.releaseDate.setText(releaseDate);
        ratings.setText(rating);
        Picasso.get().load(BASE_URL + size + backdropImage).into(circleBackdrop);

        favBtn.setOnClickListener(v -> {
            deleteFromDatabase();
            favBtn.setImageDrawable(ContextCompat.getDrawable(MoviesFavouritesDetails.this, R.drawable.ic_favorite_border_black_24dp));
            finish();
        });
    }

    private void deleteFromDatabase() {
        Intent intent = getIntent();

        final String movieId = intent.getStringExtra(Constants.MOVIE_ID);
        AppExecutors.getInstance().diskIO().execute(() -> mdb.moviesDao().deleteFavMovie(movieId));
    }
}
