package in.casetcollege.popularmovies.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.Database.FavouriteShowsModel;

public class FavouriteShowsViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteShowsModel>> favourites;

    public FavouriteShowsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        favourites = appDatabase.showsDao().getAllShows();
    }

    public LiveData<List<FavouriteShowsModel>> getFavourites() {
        return favourites;
    }
}