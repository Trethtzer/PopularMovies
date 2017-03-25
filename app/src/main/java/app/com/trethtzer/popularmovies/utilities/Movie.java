package app.com.trethtzer.popularmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Trethtzer on 13/11/2016.
 */

public class Movie implements Parcelable {

    private int id;
    private String idMovie;
    private String posterPath;
    private String title;
    private String synopsis;
    private String date;
    private String average;
    private String reviews;
    private String videos;


    public Movie(){}
    public Movie(int id,String url){
        this.id = id;
        posterPath = url;
    }

    public Movie(Parcel p){
        id = p.readInt();
        idMovie = p.readString();
        posterPath = p.readString();
        title = p.readString();
        synopsis = p.readString();
        date = p.readString();
        average = p.readString();
        reviews = p.readString();
        videos = p.readString();

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getIdMovie() {
        return idMovie;
    }
    public void setIdMovie(String idMovie) {
        this.idMovie = idMovie;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getAverage() {
        return average;
    }
    public void setAverage(String average) {
        this.average = average;
    }
    public String getReviews() {
        return reviews;
    }
    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
    public String getVideos() {
        return videos;
    }
    public void setVideos(String videos) {
        this.videos = videos;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public String toString(){
        return title + "\nID: " + idMovie;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeInt(id);
        parcel.writeString(idMovie);
        parcel.writeString(posterPath);
        parcel.writeString(title);
        parcel.writeString(synopsis);
        parcel.writeString(date);
        parcel.writeString(average);
        parcel.writeString(reviews);
        parcel.writeString(videos);
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
