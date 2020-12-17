package in.casetcollege.popularmovies.Api;

import in.casetcollege.popularmovies.Model.CastResults;
import in.casetcollege.popularmovies.Model.MovieResults;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.Model.ImageResults;
import in.casetcollege.popularmovies.Model.ReviewResults;
import in.casetcollege.popularmovies.Model.TrailerResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesApi {

    public interface Api{
        String BASE_URL="http://api.themoviedb.org/3/movie/";
        String BASE_URL_FOR_SEARCHING_BY_NAME = "https://api.themoviedb.org/3/search/";

        @GET("movie")
        Call<MovieResults> getMoviesByName(@Query("query") String movieName, @Query("include_adult") Boolean includeAdult, @Query("api_key") String KEY);

        @GET("popular")
        Call<MovieResults> getPopularMovies(@Query("api_key") String KEY);

        @GET("top_rated")
        Call<MovieResults> getTopRatedMovies(@Query("api_key") String KEY);

        @GET("latest")
        Call<Movie> getLatestMovie(@Query("api_key") String KEY);

        @GET("{movieId}")
        Call<Movie> getMovieById(@Path("movieId") int movieId, @Query("api_key") String KEY);

        @GET("{movieId}/similar")
        Call<MovieResults> getSimilarMovies(@Path("movieId") String movieId, @Query("api_key") String KEY);

        @GET("{movie_id}/videos")
        Call<TrailerResults> getTrailer (@Path("movie_id") String movieId, @Query("api_key") String apiKey);

        @GET("{movie_id}/credits")
        Call<CastResults> getCast (@Path("movie_id") String movieId, @Query("api_key") String apiKey);

        @GET("{movie_id}/images")
        Call<ImageResults> getImages (@Path("movie_id") String movieId, @Query("api_key") String apiKey);

        @GET("{movie_id}/reviews")
        Call<ReviewResults> getReviews(@Path("movie_id")String movieId, @Query("api_key") String apiKey);
    }
}
