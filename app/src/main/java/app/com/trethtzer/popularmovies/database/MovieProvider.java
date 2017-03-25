package app.com.trethtzer.popularmovies.database;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Trethtzer on 08/03/2017.
 */

public class MovieProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int ONE_MOVIE = 200;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieContract.CONTENT_AUTHORITY;
        sURIMatcher.addURI(authority,MovieContract.PATH_MOVIE,MOVIES);
        sURIMatcher.addURI(authority,MovieContract.PATH_MOVIE + "/*", ONE_MOVIE);

        return sURIMatcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case ONE_MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;

        switch(sUriMatcher.match(uri)){
            case MOVIES:
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ONE_MOVIE:
                String id_movie = MovieContract.MovieEntry.getIdMovieFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_IDMOVIE + " = ?",
                        new String[]{id_movie},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case ONE_MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if( _id >= 0 ) returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int _id;

        final int match = sUriMatcher.match(uri);
        if(null == selection) selection = "1";
        switch(match){
            case MOVIES:
                _id = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection,selectionArgs);
                break;
            case ONE_MOVIE:
                String id_movie = MovieContract.MovieEntry.getIdMovieFromUri(uri);
                _id = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_IDMOVIE + " = ?",
                        new String[]{id_movie});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return _id;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int _id;

        final int match = sUriMatcher.match(uri);
        if(null == selection) selection = "1";

        switch(match){
            case MOVIES:
                _id = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return _id;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch(match){
            case MOVIES:
                db.beginTransaction();;
                int returnCount = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally{
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown(){
        mOpenHelper.close();
        super.shutdown();
    }
}
