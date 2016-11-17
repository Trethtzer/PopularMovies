package app.com.trethtzer.popularmovies.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.utilities.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String nameClass = "DetailActivityFragment";

    @BindView(R.id.title) TextView title;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.rate) TextView rate;
    @BindView(R.id.synopsis) TextView synopsis;
    @BindView(R.id.imageView_poster_detail) ImageView poster;
    private Unbinder unbinder;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        Movie m = bundle.getParcelable("movie");

        unbinder = ButterKnife.bind(this,rootView);
        title.setText(m.getTitle());
        releaseDate.setText(m.getReleaseDate());
        rate.setText("Vote average: " + m.getVote_average());
        synopsis.setText(m.getOverview());
        Picasso.with(getActivity()).load(m.getUrl()).into(poster);


        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }
}
