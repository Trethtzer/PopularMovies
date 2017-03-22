package app.com.trethtzer.popularmovies.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Trethtzer on 08/03/2017.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "app.com.trethtzer.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildMovieUri(){
            return CONTENT_URI;
        }
        public static String getIdMovieFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_IDMOVIE = "id_movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_AVERAGE = "average";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_REVIEWS = "reviews";
        public static final String COLUMN_VIDEOS = "videos";
    }
}