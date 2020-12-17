package in.casetcollege.popularmovies.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {
    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMoviesModel>> getAllMovies();

    @Query("SELECT * FROM favourite_movies WHERE movie_id = :movieId")
    FavouriteMoviesModel checkIfExists(String movieId);

    @Insert
    void insertFavMovie(FavouriteMoviesModel favouritesModal);

    @Query("DELETE FROM favourite_movies WHERE movie_id = :movieId")
    void deleteFavMovie(String movieId);
}
