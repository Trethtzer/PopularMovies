package app.com.trethtzer.popularmovies.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import app.com.trethtzer.popularmovies.utilities.ReviewAdapter;
import app.com.trethtzer.popularmovies.utilities.Utility;
import app.com.trethtzer.popularmovies.utilities.VideoAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private String nameClass = "DetailActivityFragment";
    public static Movie movieDetail = new Movie();
    private static String APPKEY_MOVIES = "";
    public static ArrayList<String> videosList;
    public static ArrayList<String> reviewsList;
    public static VideoAdapter vAdapter;
    public static ReviewAdapter rAdapter;


    private Uri uriIntent;
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


    @BindView(R.id.title) TextView title;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.rate) TextView rate;
    @BindView(R.id.synopsis) TextView synopsis;
    @BindView(R.id.imageView_poster_detail) ImageView poster;
    @BindView(R.id.lista_trailers) ListView trailers;
    @BindView(R.id.lista_reviews) ListView reviews;
    private Unbinder unbinder;


    public DetailActivityFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        unbinder = ButterKnife.bind(this,rootView);

        Bundle bundle = getActivity().getIntent().getExtras();
        if(bundle != null) {
            if (bundle.get("movie") != null) {
                movieDetail = bundle.getParcelable("movie");
                title.setText(movieDetail.getTitle());
                releaseDate.setText(movieDetail.getDate());
                rate.setText("Vote average: " + movieDetail.getAverage());
                synopsis.setText(movieDetail.getSynopsis());
                Picasso.with(getActivity()).load(movieDetail.getPosterPath()).into(poster);
            }
        }else{
            if(savedInstanceState != null){
                if(savedInstanceState.containsKey("movie")){
                    movieDetail = savedInstanceState.getParcelable("movie");
                    title.setText(movieDetail.getTitle());
                    releaseDate.setText(movieDetail.getDate());
                    rate.setText("Vote average: " + movieDetail.getAverage());
                    synopsis.setText(movieDetail.getSynopsis());
                    Picasso.with(getActivity()).load(movieDetail.getPosterPath()).into(poster);
                }
            }else {
                uriIntent = getActivity().getIntent().getData();
            }
        }

        // Creamos las listas
        videosList = new ArrayList<>();
        vAdapter = new VideoAdapter(getActivity(),R.id.item_listView,videosList);
        trailers.setAdapter(vAdapter);
        trailers.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String) adapterView.getItemAtPosition(i);
                // Hacemos intent a youtube con el String.
                // Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + s)));
            }
        }));
        reviewsList = new ArrayList<>();
        rAdapter = new ReviewAdapter(getActivity(),R.id.item_listView,reviewsList);
        reviews.setAdapter(rAdapter);
        reviews.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String) adapterView.getItemAtPosition(i);
                // Hacemos intent a pagina web.
                // Toast.makeText(getActivity(), s,Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.getUrl(s))));
            }
        }));

        new FetchVideosAndReviewsTask().execute(movieDetail.getIdMovie());

        return rootView;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outB){
        outB.putParcelable("movie",movieDetail);
        super.onSaveInstanceState(outB);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(R.id.action_favorite == item.getItemId()){
            // Escribimos/borramos de la base de datos.
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_IDMOVIE,movieDetail.getIdMovie());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,movieDetail.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_AVERAGE,movieDetail.getAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_DATE,movieDetail.getDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,movieDetail.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,movieDetail.getSynopsis());
            movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS,movieDetail.getReviews());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEOS,movieDetail.getVideos());
            getContext().getContentResolver().insert(MovieContract.MovieEntry.buildMovieUri(Long.getLong(movieDetail.getIdMovie())),movieValues);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != uriIntent)
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    uriIntent,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        movieDetail.setId(data.getInt(COL_ID));
        movieDetail.setTitle(data.getString(COL_TITLE));
        title.setText(data.getString(COL_TITLE));
        movieDetail.setDate(data.getString(COL_DATE));
        releaseDate.setText(data.getString(COL_DATE));
        movieDetail.setAverage(data.getString(COL_AVERAGE));
        rate.setText("Vote average: " + data.getString(COL_AVERAGE));
        movieDetail.setSynopsis(data.getString(COL_SYNOPSIS));
        synopsis.setText(data.getString(COL_SYNOPSIS));
        movieDetail.setPosterPath(data.getString(COL_POSTER_PATH));
        Picasso.with(getActivity()).load(data.getString(COL_POSTER_PATH)).into(poster);

        movieDetail.setIdMovie(data.getString(COL_IDMOVIE));
        movieDetail.setId(data.getInt(COL_ID));
        movieDetail.setReviews(data.getString(COL_REVIEWS));
        movieDetail.setVideos(data.getString(COL_VIDEOS));
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public void setVideosAndReviews(){
        String videos = movieDetail.getVideos();
        String reviews = movieDetail.getReviews();
        boolean author = false;

        String result = "";
        for(int i = 0; i < videos.length(); i++){
            char c = videos.charAt(i);
            if(c == ';'){
                videosList.add(result.toString()); // Para evitar que cambie al cambiar result.
                result = "";
            }else{
                result = result + c;
            }
        }
        result = "";
        for(int i = 0; i < reviews.length(); i++){
            char c = reviews.charAt(i);
            if(c == ';' && author == false){
                reviewsList.add(result.toString());
                result = "";
            }else if(c == '-'){
                author = true;
                result = result + c;
            }else{
                author = false;
                result = result + c;
            }
        }
    }

    public class FetchVideosAndReviewsTask extends AsyncTask<String,Void,ArrayList<String>> {

        private String nameClass = "FetchMoviesTask";

        public ArrayList<String> doInBackground(String... params){
            // No params
            if(params == null){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader bReader = null;
            String moviesVideosJsonStr = null;
            String moviesReviewsJsonStr = null;

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendPath("videos")
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
                    moviesVideosJsonStr = buffer.toString();
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
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendPath("reviews")
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
                    moviesReviewsJsonStr = buffer.toString();
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
                return getVideosAndReviewFromJson(moviesVideosJsonStr,moviesReviewsJsonStr);
            }catch (JSONException e){
                Log.e(nameClass,e.toString());
            }
            return null;
        }

        protected void onPostExecute(ArrayList<String> result) {
            if(result.isEmpty()){
                Toast.makeText(getActivity(),"YEP",Toast.LENGTH_LONG).show();
            }else {
                if (result.get(0) != null) {
                    movieDetail.setVideos(result.get(0));
                    Log.d("result 0: ",result.get(0));
                }
                if (result.get(1) != null) {
                    movieDetail.setReviews(result.get(1));
                    Log.d("result 1: ",result.get(1));
                }
            }
            setVideosAndReviews();
            vAdapter.notifyDataSetChanged();
            rAdapter.notifyDataSetChanged();
        }

        // Analiza el json y devuelve un arraylist de peliculas.
        protected ArrayList<String> getVideosAndReviewFromJson(String jsonVideoString, String jsonReviewString) throws JSONException{

            ArrayList<String> result = new ArrayList<>();
            if(jsonVideoString == null){ // In this case we don't need to do anything.
                 result.add(";");
            }else {

                String videos = "";
                final String OWM_RESULT = "results";
                final String OWM_KEY = "key";

                JSONObject videosJson = new JSONObject(jsonVideoString);

                JSONArray moviesArray = videosJson.getJSONArray(OWM_RESULT);

                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject videoJson = moviesArray.getJSONObject(i);
                    String key = videoJson.getString(OWM_KEY);

                    videos = videos + key + ";";
                }
                result.add(videos);
                Log.d("Videos: ", videos);
            }

            if(jsonReviewString == null){
                result.add(";");
            }else{

                String reviews = "";
                final String OWM_RESULT = "results";
                final String OWM_KEY = "url";
                final String OWM_AUTHOR = "author";

                JSONObject reviewsJson = new JSONObject(jsonReviewString);

                JSONArray reviewArray = reviewsJson.getJSONArray(OWM_RESULT);

                for (int i = 0; i < reviewArray.length(); i++) {
                    JSONObject reviewJson = reviewArray.getJSONObject(i);
                    String key = reviewJson.getString(OWM_KEY);
                    String author = reviewJson.getString(OWM_AUTHOR);

                    reviews = reviews + author + "-;" + key + ";";
                }
                result.add(reviews);
                Log.d("Reviews: ", reviews);
            }

            return result;
        }
    }
}
