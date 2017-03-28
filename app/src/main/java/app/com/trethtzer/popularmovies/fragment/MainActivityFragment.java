package app.com.trethtzer.popularmovies.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.com.trethtzer.popularmovies.BuildConfig;
import app.com.trethtzer.popularmovies.DetailActivity;
import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.database.MovieContract;
import app.com.trethtzer.popularmovies.utilities.FetchMoviesTask;
import app.com.trethtzer.popularmovies.utilities.Movie;
import app.com.trethtzer.popularmovies.utilities.MovieAdapter;
import app.com.trethtzer.popularmovies.utilities.MovieAdapterCursor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static MovieAdapterCursor adapterCursor;
    public static MovieAdapter adapter;
    private static String APPKEY_MOVIES = BuildConfig.MY_API_KEY;
    public static ArrayList<Movie> movies;
    private Bundle sIS;
    private int LOADER_ID = 1005;
    private int lastPosition;

    // Comunicacion del fragmento con mainActivity
    mainActivityCallback mCallback;
    public interface mainActivityCallback{
        public void onItemSelected(Uri dateUri);
    }

    // Para la base de datos
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_IDMOVIE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_DATE,
            MovieContract.MovieEntry.COLUMN_AVERAGE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_REVIEWS,
            MovieContract.MovieEntry.COLUMN_VIDEOS
    };
    public static final int COL_ID = 0;
    public static final int COL_IDMOVIE = 1;
    public static final int COL_POSTER_PATH = 2;
    public static final int COL_TITLE = 3;
    public static final int COL_DATE = 4;
    public static final int COL_AVERAGE = 5;
    public static final int COL_SYNOPSIS = 6;
    public static final int COL_REVIEWS = 7;
    public static final int COL_VIDEOS = 8;

    // Vistas
    @BindView(R.id.gridView_summary) GridView gv;
    private Unbinder unbinder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movies = new ArrayList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        unbinder = ButterKnife.bind(this,rootView);
        // Si tenemos que buscar en la base de datos...
        if(sp.getString("search",getString(R.string.lp_defaultValue_search)).equals("favorite")) {
            adapterCursor = new MovieAdapterCursor(getActivity(), null, 0);
            gv.setAdapter(adapterCursor);
            gv.setOnItemClickListener((new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                    if(cursor != null){
                        Uri dateUri = MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_IDMOVIE));
                        Intent intent = new Intent(getActivity(), DetailActivity.class).setData(dateUri);
                        startActivity(intent);
                    }
                }
            }));
        }else{
            adapter = new MovieAdapter(getActivity(),R.layout.item_gridview_movie,movies);
            gv.setAdapter(adapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Movie m = (Movie) adapterView.getItemAtPosition(i);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movie",m);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        // Deleted content now in onStart method.
        sIS = savedInstanceState;

        return rootView;
    }

    @Override
    public void onStart(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(sIS == null || !sIS.containsKey("movies")){
            // Si es favoritos sacamos de la base de datos.
            if(sp.getString("search",getString(R.string.lp_defaultValue_search)).equals("favorite")){
                getLoaderManager().restartLoader(LOADER_ID, null, this);
            }
            // Buscamos en internet.
            else {
                new FetchMoviesTask(getActivity(),new FetchMyDataTaskCompleteListener()).execute(sp.getString("search", getString(R.string.lp_defaultValue_search)));
            }
        }else{
            // lastPosition = sIS.getInt("position");
            movies = sIS.getParcelableArrayList("movies");
            adapter.clear();
            if(movies.isEmpty()){
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                gv.setAdapter(adapterCursor);
            }else {
                gv.setAdapter(adapter);
                adapter.addAll(movies);
            }
            gv.setSelection(sIS.getInt("index"));
        }

        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outB){
        outB.putParcelableArrayList("movies",movies);
        int index = gv.getFirstVisiblePosition();
        outB.putInt("index",index);
        super.onSaveInstanceState(outB);
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID){
            // Sort order:  Ascending, by date.
            String sortOrder = MovieContract.MovieEntry.COLUMN_AVERAGE + " ASC";
            Uri moviesUri = MovieContract.MovieEntry.buildMovieUri();
            return new CursorLoader(getActivity(),moviesUri,MOVIE_COLUMNS,null,null,sortOrder);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapterCursor.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapterCursor.swapCursor(null);
    }


    // Para usar FetchMoviesTask fuera del fragmento.
    public interface AsyncTaskCompleteListener<T>
    {
        /**
         * Invoked when the AsyncTask has completed its execution.
         * @param result The resulting object from the AsyncTask.
         */
        public void onTaskComplete(T result);
    }
    public class FetchMyDataTaskCompleteListener implements AsyncTaskCompleteListener<ArrayList<Movie>>
    {
        @Override
        public void onTaskComplete(ArrayList<Movie> result)
        {
            if(result != null) {
                movies.clear();
                for (Movie m : result) {
                    movies.add(m);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}