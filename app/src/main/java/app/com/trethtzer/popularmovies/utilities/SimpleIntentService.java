package app.com.trethtzer.popularmovies.utilities;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

import app.com.trethtzer.popularmovies.DetailActivity;
import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.database.MovieContract;

/**
 * Created by Trethtzer on 26/03/2017.
 */

public class SimpleIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    boolean b;

    public SimpleIntentService() {
        super("SimpleIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor c = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(intent.getStringExtra(PARAM_IN_MSG)).build(),
                null,
                null,
                null,
                null);
        if(c.moveToFirst()){
            b = true;
        }else{
            b = false;
        }

        // Now the broadcast.
        Log.d("YEP","Sending broadcastIntent");

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DetailActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_MSG, b);
        sendBroadcast(broadcastIntent);
    }
}