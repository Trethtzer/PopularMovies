package app.com.trethtzer.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Trethtzer on 13/11/2016.
 */

public class Movie implements Parcelable {

    private int id;
    private String url;
    private String title;
    private String overview;
    private String releaseDate;
    private double vote_average;

    public Movie(int id,String url){
        this.id = id;
        this.url = url;
    }
    public Movie(Parcel p){
        id = p.readInt();
        url = p.readString();
        title = p.readString();
        overview = p.readString();
        releaseDate = p.readString();
        vote_average = p.readDouble();
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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
    public double getVote_average() {
        return vote_average;
    }
    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public String toString(){
        return title + "\nID: " + id;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeInt(id);
        parcel.writeString(url);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeDouble(vote_average);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }
        @Override
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };
}
