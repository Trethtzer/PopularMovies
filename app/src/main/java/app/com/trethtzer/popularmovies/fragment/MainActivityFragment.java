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

import app.com.trethtzer.popularmovies.DetailActivity;
import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.database.MovieContract;
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
    private static MovieAdapter adapter;
    private static String APPKEY_MOVIES = "";
    private ArrayList<Movie> movies;
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
                new FetchMoviesTask().execute(sp.getString("search", getString(R.string.lp_defaultValue_search)));
            }
        }else if(sIS.containsKey("movies")){
            // lastPosition = sIS.getInt("position");
            movies = sIS.getParcelableArrayList("movies");
            adapter.clear();
            // IN case the other adapter was working...
            gv.setAdapter(adapter);
            adapter.addAll(movies);
        }

        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outB){
        outB.putParcelableArrayList("movies",movies);
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

    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>>{

        private String nameClass = "FetchMoviesTask";

        public ArrayList<Movie> doInBackground(String... params){
            // No params
            if(params == null){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader bReader = null;
            String moviesJsonStr = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter("api_key",APPKEY_MOVIES);
                Uri builtUri = builder.build();

                URL url = new URL(builtUri.toString());
                Log.d("La url:",url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream != null) {
                    bReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = bReader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    moviesJsonStr = buffer.toString();
                }
            }catch (IOException e) {
                Log.e(nameClass, e.toString());
            }catch (SecurityException e){
                Log.d(nameClass, e.toString());
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(bReader != null){
                    try{
                        bReader.close();
                    }catch (IOException e){
                        Log.e(nameClass,e.toString());
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            }catch (JSONException e){
                Log.e(nameClass,e.toString());
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            if(result != null) {
                movies.clear();
                for (Movie m : result) {
                    movies.add(m);
                }
                adapter.notifyDataSetChanged();
            }
        }

        // Analiza el json y devuelve un arraylist de peliculas.
        protected ArrayList<Movie> getMoviesDataFromJson(String jsonString) throws JSONException{

            if(jsonString == null){ // In this case we don't need to do anything.
                return null;
            }

            ArrayList<Movie> list = new ArrayList<>();
            final String OWM_RESULT = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_ID = "id";
            final String OWM_AVERAGE = "vote_average";
            final String OWM_SYNOPSIS = "overview";
            final String OWM_TITLE = "original_title";
            final String OWM_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(jsonString);

            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULT);

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject movieJson = moviesArray.getJSONObject(i);
                String posterPath = movieJson.getString(OWM_POSTER_PATH);
                int id = movieJson.getInt(OWM_ID);
                double average = movieJson.getDouble(OWM_AVERAGE);
                String synopsis = movieJson.getString(OWM_SYNOPSIS);
                String title = movieJson.getString(OWM_TITLE);
                String date = movieJson.getString(OWM_DATE);

                Movie m = new Movie(id,"http://image.tmdb.org/t/p/w185/" + posterPath);
                m.setIdMovie(Integer.toString(id));
                m.setSynopsis(synopsis);
                m.setDate(date);
                m.setTitle(title);
                m.setAverage(Double.toString(average));

                list.add(m);
            }

            return list;
        }
    }
}