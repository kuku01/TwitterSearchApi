package co.harsh.twittersearch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import co.harsh.twittersearch.Helper.Utils;
import co.harsh.twittersearch.R;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity {
    private TwitterLoginButton loginButton;
    private TextView LoginStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Utils.TWITTER_KEY, Utils.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.login_page);
        LoginStatus =(TextView)findViewById(R.id.tvLoginStatus);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                LoginStatus.setText("Successfully Logged in");
                Utils mUtils = new Utils(LoginActivity.this);
                mUtils.setvalue("token", token);
                mUtils.setvalue("secret", secret);
                Intent i = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(i);
                LoginActivity.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
            }

            @Override
            public void failure(TwitterException exception) {
                LoginStatus.setText("Please try again");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}