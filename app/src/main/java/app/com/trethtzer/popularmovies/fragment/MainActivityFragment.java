package app.com.trethtzer.popularmovies.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.utilities.Movie;
import app.com.trethtzer.popularmovies.utilities.MovieAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static MovieAdapter adapter;
    private static String APPKEY_MOVIES = "";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Movie> movies = new ArrayList<>();

        adapter = new MovieAdapter(getActivity(),R.layout.item_gridview_movie,movies);
        GridView gv = (GridView) rootView.findViewById(R.id.gridView_summary);
        gv.setAdapter(adapter);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchMoviesTask().execute(sp.getString("search",getString(R.string.lp_defaultValue_search)));

        return rootView;
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
                // Log.v(nameClass,builtUri.toString());

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
            }catch (IOException e){
                Log.e(nameClass,e.toString());
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
            adapter.clear();
            for(Movie m : result){
                adapter.add(m);
            }
            adapter.notifyDataSetChanged();
        }

        // Analiza el json y devuelve un arraylist de peliculas.
        protected ArrayList<Movie> getMoviesDataFromJson(String jsonString) throws JSONException{

            ArrayList<Movie> list = new ArrayList<>();
            final String OWM_RESULT = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(jsonString);

            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULT);

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject movieJson = moviesArray.getJSONObject(i);
                String posterPath = movieJson.getString(OWM_POSTER_PATH);
                int id = movieJson.getInt(OWM_ID);

                list.add(new Movie(id,"http://image.tmdb.org/t/p/w185/" + posterPath));
            }

            return list;
        }
    }
}
