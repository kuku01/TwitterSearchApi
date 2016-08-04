package co.harsh.twittersearch.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils extends Activity {
    public static final String TWITTER_KEY = "DOPj9itih7yb0uugDWMzV7rJq";
    public static final String TWITTER_SECRET = "aZauTxaDjs18a5pikCgdfgXlZQWJ0BtyV7Uexs64sbSWxymza3";

    SharedPreferences sharedpreferences;
    Context context;

    public Utils(Context c) {
        context = c;
        sharedpreferences = context.getSharedPreferences("Registered", MODE_PRIVATE);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(c); // here you get your prefrences by either of two methods

    }

    public boolean setvalue(String keyname, String value) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(keyname, value);
        editor.apply();
        return true;
    }

    public String getvalue(String keyname) {
        return sharedpreferences.getString(keyname, "");
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}