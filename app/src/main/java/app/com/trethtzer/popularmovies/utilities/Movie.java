package app.com.trethtzer.popularmovies.utilities;

/**
 * Created by Trethtzer on 13/11/2016.
 */

public class Movie {

    private int id;
    private String url;

    public Movie(int id,String url){
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
