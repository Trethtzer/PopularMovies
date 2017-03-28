package app.com.trethtzer.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
import app.com.trethtzer.popularmovies.fragment.MainActivityFragment;

import static app.com.trethtzer.popularmovies.fragment.MainActivityFragment.adapter;
import static app.com.trethtzer.popularmovies.fragment.MainActivityFragment.movies;

/**
 * Created by Trethtzer on 28/03/2017.
 */

public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>> {

    private String nameClass = "FetchMoviesTask";
    private Context context;
    private MainActivityFragment.AsyncTaskCompleteListener<ArrayList<Movie>> listener;

    public FetchMoviesTask(Context ctx, MainActivityFragment.AsyncTaskCompleteListener<ArrayList<Movie>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

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
                    .appendQueryParameter("api_key", BuildConfig.MY_API_KEY);
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
        super.onPostExecute(result);
        listener.onTaskComplete(result);
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
