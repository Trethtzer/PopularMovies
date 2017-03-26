package app.com.trethtzer.popularmovies;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.com.trethtzer.popularmovies.database.MovieContract;
import app.com.trethtzer.popularmovies.fragment.DetailActivityFragment;
import app.com.trethtzer.popularmovies.utilities.Movie;
import app.com.trethtzer.popularmovies.utilities.SimpleIntentService;

public class DetailActivity extends AppCompatActivity {
    private String nameClass = "DetailActivity";
    MenuItem favorite;
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        favorite = menu.findItem(R.id.action_favorite);

        Movie movieDetail = DetailActivityFragment.movieDetail;

        // Todo esto lo hacemos con receiver.
        Intent msgIntent = new Intent(this, SimpleIntentService.class);
        msgIntent.putExtra(SimpleIntentService.PARAM_IN_MSG, movieDetail.getIdMovie());
        startService(msgIntent);

        /*Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieDetail.getIdMovie()).build(),
                null,
                null,
                null,
                null);
        if(c.moveToFirst()){
            favorite.setIcon(R.drawable.favorite);
        }else{
            favorite.setIcon(R.drawable.unfavorite);
        }*/

        return true;
    }


    @Override
    public void onDestroy(){
        try{
            if(receiver!=null)
                unregisterReceiver(receiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }if(id == R.id.action_favorite){
            // Comprobamos si está en la base de datos. En ese caso eliminamos en otro caso añadimos.
            Movie movieDetail = DetailActivityFragment.movieDetail;
            Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieDetail.getIdMovie()).build(),
                    null,
                    null,
                    null,
                    null);

            if(c.moveToFirst()){
                // Tenemos que borrar de la base de datos.
                getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieDetail.getIdMovie()).build(),null,null);
                // favorite.setIcon(R.drawable.favorite);
            }else{
                // Tenemos que añadir a la base de datos.
                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_IDMOVIE,movieDetail.getIdMovie());
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE,movieDetail.getTitle());
                movieValues.put(MovieContract.MovieEntry.COLUMN_AVERAGE,movieDetail.getAverage());
                movieValues.put(MovieContract.MovieEntry.COLUMN_DATE,movieDetail.getDate());
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,movieDetail.getPosterPath());
                movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS,movieDetail.getSynopsis());
                movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS,movieDetail.getReviews());
                movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEOS,movieDetail.getVideos());
                getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieDetail.getIdMovie()).build(),movieValues);
                favorite.setIcon(R.drawable.favorite);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.trethtzer.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean b = intent.getBooleanExtra(SimpleIntentService.PARAM_OUT_MSG,true);
            if(b){
                favorite.setIcon(R.drawable.favorite);
            }else{
                favorite.setIcon(R.drawable.unfavorite);
            }
        }
    }

}
