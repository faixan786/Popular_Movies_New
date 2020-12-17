package in.casetcollege.popularmovies.Api;

import in.casetcollege.popularmovies.Model.CastResults;
import in.casetcollege.popularmovies.Model.ImageResults;
import in.casetcollege.popularmovies.Model.ReviewResults;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.Model.ShowResults;
import in.casetcollege.popularmovies.Model.TrailerResults;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ShowsApi {
    public interface Api {
        String BASE_URL="http://api.themoviedb.org/3/tv/";
        String BASE_URL_FOR_SEARCHING_BY_NAME = "https://api.themoviedb.org/3/search/";

        @GET("tv")
        Call<ShowResults> getShowsByName(@Query("query") String movieName, @Query("include_adult") Boolean includeAdult, @Query("api_key") String KEY);

        @GET("popular")
        Call<ShowResults> getPopularShows(@Query("api_key") String KEY);

        @GET("top_rated")
        Call<ShowResults> getTopRatedShows(@Query("api_key") String KEY);

        @GET("{showID}/credits")
        Call<CastResults> getCast (@Path("showID") String showID, @Query("api_key") String apiKey);

        @GET("{showID}/images")
        Call<ImageResults> getImages (@Path("showID") String showID, @Query("api_key") String apiKey);

        @GET("{showID}/videos")
        Call<TrailerResults> getTrailer (@Path("showID") String showID, @Query("api_key") String apiKey);

        @GET("{showID}/similar")
        Call<ShowResults> getSimilarShows(@Path("showID") String showID, @Query("api_key") String KEY);

        @GET("{showID}/reviews")
        Call<ReviewResults> getReviews(@Path("showID")String showID, @Query("api_key") String apiKey);

        @GET("latest")
        Call<Show> getLatestShow(@Query("api_key") String KEY);

        @GET("{showID}")
        Call<Show> getShowById(@Path("showID") int showID, @Query("api_key") String KEY);
    }
}
