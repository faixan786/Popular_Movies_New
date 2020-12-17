package in.casetcollege.popularmovies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ImageResults {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("backdrops")
    @Expose
    private List<Image> backdrops;

    @SerializedName("posters")
    @Expose
    private List<Image> posters;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBackdrops(List<Image> backdrops) {
        this.backdrops = backdrops;
    }

    public void setPosters(List<Image> posters) {
        this.posters = posters;
    }

    public List<Image> getPhotos() {
        ArrayList<Image> images = new ArrayList<>(backdrops);
        images.addAll(posters);
        return images;
    }
}
