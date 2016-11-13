package app.com.trethtzer.popularmovies.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.utilities.Movie;
import app.com.trethtzer.popularmovies.utilities.MovieAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Movie> movies = new ArrayList<Movie>();
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

        MovieAdapter adapter = new MovieAdapter(getActivity(),R.layout.item_gridview_movie,movies);
        GridView gv = (GridView) rootView.findViewById(R.id.gridView_summary);
        gv.setAdapter(adapter);


        return rootView;
    }
}
