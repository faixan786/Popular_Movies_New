package in.casetcollege.popularmovies.UI;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Adapter.MoviesAdapter;
import in.casetcollege.popularmovies.Api.MoviesApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.Model.MovieResults;
import in.casetcollege.popularmovies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesSearchActivity extends AppCompatActivity {

    private SearchView searchView;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    List<Movie> movieMovies;

    String API_KEY = BuildConfig.GoogleSecAPIKEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_search_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        settingLayoutManager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menuSearch_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);

        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadMovies(query);
                hideKeyboard(MoviesSearchActivity.this);
                getSupportActionBar().setTitle(searchView.getQuery());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void settingLayoutManager() {
        int noOfColumns = 2;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fade_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setHasFixedSize(true);
    }

    private void loadMovies(String movieName) {
        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL_FOR_SEARCHING_BY_NAME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        MoviesApi.Api api = retrofit.create(MoviesApi.Api.class);

        // now making the call object
        Call<MovieResults> call = api.getMoviesByName(movieName, false, API_KEY);

        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                MovieResults moviesModel = response.body();
                movieMovies = new ArrayList<>();
                movieMovies = moviesModel.getMovies();
                MoviesAdapter adapter = new MoviesAdapter(movieMovies, getApplicationContext());

                recyclerView.setAdapter(adapter);

                // Animation
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                Log.d("TAG", "onFailure: Message " + t.getMessage());
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
