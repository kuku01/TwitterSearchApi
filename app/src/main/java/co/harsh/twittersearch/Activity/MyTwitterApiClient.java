package co.harsh.twittersearch.Activity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import co.harsh.twittersearch.getterSetter.AllTweets;
import co.harsh.twittersearch.getterSetter.AllUsers;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public CustomService getCustomService() {
        return getService(CustomService.class);
    }
}

interface CustomService {
    @GET("/1.1/users/search.json")
    void show(@Query("q") String var1, @Query("page") Integer var2, @Query("count") Integer var3,  @Query("include_entities") Boolean var4, Callback<List<AllUsers>>var5w);
}