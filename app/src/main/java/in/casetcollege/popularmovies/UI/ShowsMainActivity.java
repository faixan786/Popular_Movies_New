package in.casetcollege.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Adapter.FavouriteShowsAdapter;
import in.casetcollege.popularmovies.Adapter.ShowsAdapter;
import in.casetcollege.popularmovies.Api.ShowsApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Database.FavouriteShowsModel;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.Model.ShowResults;
import in.casetcollege.popularmovies.NetworkInfo.Network;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.ViewModel.FavouriteShowsViewModel;
import in.casetcollege.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowsMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FavouriteShowsAdapter favouritesAdapter;

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.contentlayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.nofav)
    TextView textView;
    private RecyclerView.LayoutManager layoutManager;

    private String API_KEY = BuildConfig.GoogleSecAPIKEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shows_main_activity);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settingLayoutManager();

        boolean isConnected = Network.getConnectivityStatus(ShowsMainActivity.this);

        if (isConnected) {
            loadPopularShows();
        } else {
            checkNetworkConnection();
            Snackbar.make(constraintLayout, "Please Check Internet Connection", Snackbar.LENGTH_LONG).show();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.toolbarBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomToolbarMovies:
                    startActivity(new Intent(ShowsMainActivity.this, MoviesMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;

                case R.id.bottomToolbarTrending:
                    startActivity(new Intent(ShowsMainActivity.this, TrendingMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list state
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Retrieve list state and list/item position
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_popular) {
            boolean isConnected = Network.getConnectivityStatus(ShowsMainActivity.this);
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadPopularShows();
            } else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_Top_rated) {
            boolean isConnected = Network.getConnectivityStatus(getApplicationContext());
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadTopRatedShows();
            } else {
                checkNetworkConnection();
//                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.favourites) {
            loadFavouriteShows();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            boolean isConnected = Network.getConnectivityStatus(ShowsMainActivity.this);
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadPopularShows();
            } else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.random) {
            getRandomShow();
        } else if (id == R.id.search) {
            startActivity(new Intent(ShowsMainActivity.this, ShowsSearchActivity.class));
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

    private void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowsMainActivity.this);
        builder.setTitle("No internet connection");
        builder.setMessage("Please turn on internet connection and then Refresh to continue Or press close and check favourite lists");
        builder.setPositiveButton("Favourites", (dialog, which) -> {
            loadFavouriteShows();
        });
        builder.setNegativeButton("Close", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadPopularShows() {
        new ViewModelProvider(this).get(FavouriteShowsViewModel.class).getFavourites().removeObserver(favouritesObserver);

        getSupportActionBar().setTitle(getString(R.string.popular_shows));

        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        // now making the call object
        Call<ShowResults> call = api.getPopularShows(API_KEY);

        call.enqueue(new Callback<ShowResults>() {
            @Override
            public void onResponse(Call<ShowResults> call, Response<ShowResults> response) {
                if (response != null) {
                    ShowResults showResults = response.body();
                    List<Show> shows = showResults.getShows();

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

    private void loadTopRatedShows() {
        new ViewModelProvider(this).get(FavouriteShowsViewModel.class).getFavourites().removeObserver(favouritesObserver);

        getSupportActionBar().setTitle(getString(R.string.top_rated_shows));

        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        // now making the call object
        Call<ShowResults> call = api.getTopRatedShows(API_KEY);

        call.enqueue(new Callback<ShowResults>() {
            @Override
            public void onResponse(Call<ShowResults> call, Response<ShowResults> response) {
                ShowResults showResults = response.body();
                List<Show> shows = showResults.getShows();

                ShowsAdapter adapter = new ShowsAdapter(shows, getApplicationContext());
                recyclerView.setAdapter(adapter);

                // Animation
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onFailure(Call<ShowResults> call, Throwable t) {

            }
        });
    }

    private void loadFavouriteShows() {
        getSupportActionBar().setTitle(getString(R.string.favourite_shows));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        if (favouritesAdapter == null) {
            favouritesAdapter = new FavouriteShowsAdapter(this);
        }
        recyclerView.setAdapter(favouritesAdapter);

        // Animation
        recyclerView.scheduleLayoutAnimation();
        setupViewModel();
    }

    private void setupViewModel() {
        FavouriteShowsViewModel viewModel = new ViewModelProvider(this).get(FavouriteShowsViewModel.class);
        viewModel.getFavourites().observe(this, favouritesObserver);
    }

    private final Observer<List<FavouriteShowsModel>> favouritesObserver = new Observer<List<FavouriteShowsModel>>() {
        @Override
        public void onChanged(List<FavouriteShowsModel> favouriteMoviesModels) {
            if (favouriteMoviesModels != null && favouriteMoviesModels.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                favouritesAdapter.setFavouriteShows(favouriteMoviesModels);
                textView.setVisibility(View.GONE);
            } else {
                favouritesAdapter.setFavouriteShows(null);
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void getRandomShow() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        Call<Show> call = api.getLatestShow(API_KEY);
        call.enqueue(new Callback<Show>() {
            @Override
            public void onResponse(Call<Show> call, Response<Show> response) {
                Show show = response.body();
                int id = Integer.parseInt(show.getId());
                int randomId = (new Random().nextInt(id)) + 1;

                api.getShowById(randomId, API_KEY).enqueue(new Callback<Show>() {
                    @Override
                    public void onResponse(Call<Show> call, Response<Show> response) {
                        Show show = response.body();
                        if (show != null) {
                            Intent intent = new Intent(ShowsMainActivity.this, ShowsDetailsActivity.class);
                            if (show.getBackdropPath() != null) {
                                intent.putExtra(Constants.SHOW_IMAGE_URL, show.getBackdropPath());
                            } else {
                                intent.putExtra(Constants.SHOW_IMAGE_URL, show.getPosterPath());
                            }
                            intent.putExtra(Constants.SHOW_TITLE, show.getName());
                            intent.putExtra(Constants.SHOW_DESCRIPTION, show.getOverview());
                            intent.putExtra(Constants.SHOW_RATINGS, show.getVoteAverage());
                            intent.putExtra(Constants.SHOW_RELEASE_DATE, show.getReleaseDate());
                            intent.putExtra(Constants.SHOW_ID, show.getId());
                            intent.putExtra(Constants.SHOW_POSTER_IMAGE, show.getPosterPath());
                            startActivity(intent);
                        } else {
                            Snackbar.make(constraintLayout, "A Show with no photo or caption was found. Sorry for the trouble. Please try again", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", v -> getRandomShow()).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Show> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Show> call, Throwable t) {
            }
        });
    }
}
