package app.com.trethtzer.popularmovies.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.database.MovieContract;
import app.com.trethtzer.popularmovies.utilities.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private String nameClass = "DetailActivityFragment";


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
    private Unbinder unbinder;


    public DetailActivityFragment() {}


    /* public static DetailActivityFragment newInstance(Uri u){
        DetailActivityFragment df = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable("uri",u);
        df.setArguments(args);
        return df;
    } */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        unbinder = ButterKnife.bind(this,rootView);

        Bundle bundle = getActivity().getIntent().getExtras();

        if(bundle.get("movie") != null){
            Movie m = bundle.getParcelable("movie");
            title.setText(m.getTitle());
            releaseDate.setText(m.getReleaseDate());
            rate.setText("Vote average: " + m.getVote_average());
            synopsis.setText(m.getOverview());
            Picasso.with(getActivity()).load(m.getUrl()).into(poster);
        }else{
            uriIntent = getArguments().getParcelable("uri");
        }

        return rootView;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
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
        title.setText(data.getString(COL_TITLE));
        releaseDate.setText(data.getString(COL_DATE));
        rate.setText("Vote average: " + data.getString(COL_AVERAGE));
        synopsis.setText(data.getString(COL_SYNOPSIS));
        Picasso.with(getActivity()).load(data.getString(COL_POSTER_PATH)).into(poster);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
