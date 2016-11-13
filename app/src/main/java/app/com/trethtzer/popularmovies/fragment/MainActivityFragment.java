package app.com.trethtzer.popularmovies.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
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

        new FetchMoviesTask().execute("popular");

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String,Void,ArrayList<Movie>>{

        private String nameClass = "FetchMoviesTask";

        public ArrayList<Movie> doInBackground(String... params){
            // No params
            if(params == null){
                return null;
            }

            // FAKE DATA
            ArrayList<Movie> movies = new ArrayList<>();
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));
            movies.add(new Movie(0,"yep"));

            HttpURLConnection urlConnection;
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
                Log.v(nameClass,builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
            }catch (IOException e){
                e.printStackTrace();
            }


            return movies;
        }

        protected void onPostExecute(ArrayList<Movie> result) {
            adapter.clear();
            for(Movie m : result){
                adapter.add(m);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
