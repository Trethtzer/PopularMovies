package app.com.trethtzer.popularmovies.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public static Movie movieDetail = new Movie();


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
            uriIntent = getActivity().getIntent().getData();
        }

        return rootView;
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
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
            Toast.makeText(getActivity(),"parece que todo se ha guardado",Toast.LENGTH_LONG).show();
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

        Toast.makeText(getActivity(),data.getString(0) + " " + data.getString(1) + " " + data.getString(2),Toast.LENGTH_LONG).show();

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
}
