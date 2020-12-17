package in.casetcollege.popularmovies.Api;

import in.casetcollege.popularmovies.Model.MovieResults;
import in.casetcollege.popularmovies.Model.ShowResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class TrendingApi {
    public interface Api {
        String BASE_URL="http://api.themoviedb.org/3/trending/";

        @GET("movie/week")
        Call<MovieResults> getTrendingMovies(@Query("api_key") String KEY);

        @GET("tv/week")
        Call<ShowResults> getTrendingShows(@Query("api_key") String KEY);
    }
}
