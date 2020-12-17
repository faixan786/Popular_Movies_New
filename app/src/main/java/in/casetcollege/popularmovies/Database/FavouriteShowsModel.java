package in.casetcollege.popularmovies.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_shows")
public class FavouriteShowsModel {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "show_id")
    private String showId;

    @ColumnInfo(name = "vote")
    private String voteAverage;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "original_title")
    private String originalTitle;

    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;

    private String overview;
    private String releaseDate;
    private boolean favourite;

    public FavouriteShowsModel(int id, String showId, String posterPath, String originalTitle, String backdropPath, String overview, String releaseDate, boolean favourite) {
        this.id = id;
        this.showId = showId;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.favourite = favourite;
    }

    @Ignore
    public FavouriteShowsModel(String showId, String voteAverage, String originalTitle, String posterPath, String backdropPath, String overview, String releaseDate, boolean favourite) {
        this.showId = showId;
        this.voteAverage = voteAverage;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.favourite = favourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}