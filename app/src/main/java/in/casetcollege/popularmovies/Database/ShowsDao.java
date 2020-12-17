package in.casetcollege.popularmovies.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShowsDao {
    @Query("SELECT * FROM favourite_shows")
    LiveData<List<FavouriteShowsModel>> getAllShows();

    @Query("SELECT * FROM favourite_shows WHERE show_id = :showID")
    FavouriteShowsModel checkIfExists(String showID);

    @Insert
    void insertFavShow(FavouriteShowsModel favouritesModal);

    @Query("DELETE FROM favourite_shows WHERE show_id = :showID")
    void deleteFavShow(String showID);
}
