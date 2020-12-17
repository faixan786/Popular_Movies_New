package in.casetcollege.popularmovies.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.Database.FavouriteMoviesModel;

public class FavouriteMoviesViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteMoviesModel>> favourites;

    public FavouriteMoviesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        favourites = appDatabase.moviesDao().getAllMovies();
    }

    public LiveData<List<FavouriteMoviesModel>> getFavourites() {
        return favourites;
    }
}