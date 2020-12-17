package in.casetcollege.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Adapter.MoviesAdapter;
import in.casetcollege.popularmovies.Adapter.ShowsAdapter;
import in.casetcollege.popularmovies.Api.TrendingApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.Model.MovieResults;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.Model.ShowResults;
import in.casetcollege.popularmovies.NetworkInfo.Network;
import in.casetcollege.popularmovies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrendingMainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.contentlayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.nofav)
    TextView textView;
    private RecyclerView.LayoutManager layoutManager;

    private String API_KEY = BuildConfig.GoogleSecAPIKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trending_main_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

        settingLayoutManager();

        boolean isConnected = Network.getConnectivityStatus(TrendingMainActivity.this);
        if (isConnected) {
            loadTrendingMovies();
        } else {
            textView.setText("No Internet Connection");
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.toolbarBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomToolbarMovies:
                    startActivity(new Intent(TrendingMainActivity.this, MoviesMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;

                case R.id.bottomToolbarShows:
                    startActivity(new Intent(TrendingMainActivity.this, ShowsMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trending, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuTrending_movies:
                loadTrendingMovies();
                break;

            case R.id.menuTrending_shows:
                loadTrendingShows();
                break;
        }

        return true;
    }

    private void settingLayoutManager() {
        int noOfColumns = 2;

        layoutManager = new GridLayoutManager(this, noOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fade_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setHasFixedSize(true);
    }

    private void loadTrendingMovies() {
        getSupportActionBar().setTitle(getString(R.string.trending_movies));

        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TrendingApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        TrendingApi.Api api = retrofit.create(TrendingApi.Api.class);

        // now making the call object
        Call<MovieResults> call = api.getTrendingMovies(API_KEY);

        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                if(response.body() != null) {
                    List<Movie> movies = response.body().getMovies();
                    MoviesAdapter adapter = new MoviesAdapter(movies, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                    // Animation
                    recyclerView.getAdapter().notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {

            }
        });
    }

    private void loadTrendingShows() {
        getSupportActionBar().setTitle(getString(R.string.trending_shows));

        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TrendingApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        TrendingApi.Api api = retrofit.create(TrendingApi.Api.class);

        // now making the call object
        Call<ShowResults> call = api.getTrendingShows(API_KEY);

        call.enqueue(new Callback<ShowResults>() {
            @Override
            public void onResponse(Call<ShowResults> call, Response<ShowResults> response) {
                if(response!= null) {
                    List<Show> shows = response.body().getShows();
                    ShowsAdapter adapter = new ShowsAdapter(shows, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                    // Animation
                    recyclerView.getAdapter().notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();
                }
            }

            @Override
            public void onFailure(Call<ShowResults> call, Throwable t) {

            }
        });
    }
}