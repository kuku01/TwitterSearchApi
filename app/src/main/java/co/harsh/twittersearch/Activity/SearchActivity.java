package co.harsh.twittersearch.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;

import java.util.ArrayList;
import java.util.List;

import co.harsh.twittersearch.R;
import co.harsh.twittersearch.getterSetter.AllTweets;
import co.harsh.twittersearch.getterSetter.AllUsers;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    public ArrayList<AllTweets> allResults = new ArrayList<>();
    public AllTweets allResult;
    private ListView lv_Tweets;
    private TweetsAdapter madapter;
    private RadioButton userRadio, hashtagsRadio, tweetsRadio;

    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    ImageLoaderConfiguration config;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        tweetsRadio = (RadioButton) findViewById(R.id.radio0);
        userRadio = (RadioButton) findViewById(R.id.radio1);
        hashtagsRadio = (RadioButton) findViewById(R.id.radio2);
        lv_Tweets = (ListView) findViewById(R.id.ResultsList);
        config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);

        etSearch = (EditText) findViewById(R.id.searchPageToolbar_etSearch);
        Button bSearch = (Button) findViewById(R.id.bSearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(SearchActivity.this);
                pd.setMessage("Searching, Please wait.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                if (userRadio.isChecked()) {

                    MyTwitterApiClient asd = new MyTwitterApiClient(session);
                    asd.getCustomService().show(etSearch.getText().toString(), null, 50, false, new Callback<List<AllUsers>>() {
                        @Override
                        public void success(Result<List<AllUsers>> result) {
                            Log.i("ultresult", result.data.get(0).getName());
                            pd.dismiss();
                            allResults = new ArrayList<>();
                            for (int i = 0; i < result.data.size(); i++) {
                                allResult = new AllTweets();
                                allResult.setFavoriteCount(result.data.get(i).getFollowers_count());
                                allResult.setRetweetCount(result.data.get(i).getFriends_count());
                                allResult.setName1(String.valueOf(result.data.get(i).getName()));
                                allResult.setScreenName(String.valueOf(result.data.get(i).getScreen_name()));
                                allResult.setProfileImageUrl(String.valueOf(result.data.get(i).getProfile_image_url()));
                                allResults.add(allResult);
                            }
                            madapter = new TweetsAdapter(SearchActivity.this, R.layout.user_list_item, allResults);
                            lv_Tweets.setAdapter(madapter);
                            pd.dismiss();

                        }

                        @Override
                        public void failure(TwitterException e) {
                            pd.dismiss();
                            Log.i("exception", e.toString());
                        }
                    });
                } else {
                    String query = etSearch.getText().toString();
                    if (!tweetsRadio.isChecked()) {
                        if (hashtagsRadio.isChecked()) {
                            query = "#" + query;
                        }
                    }
                    twitterApiClient.getSearchService().tweets(query, null, null, null, null, 50, null, null, null, true, new Callback<Search>() {
                        @Override
                        public void success(Result<Search> result) {
                            allResults = new ArrayList<>();
                            for (int i = 0; i < result.data.tweets.size(); i++) {
                                allResult = new AllTweets();
                                if (result.data.tweets.get(i).createdAt != null)
                                    allResult.setCreatedAt(String.valueOf(result.data.tweets.get(i).createdAt));
                                if (result.data.tweets.get(i).favoriteCount != null)
                                    allResult.setFavoriteCount(Integer.parseInt(String.valueOf(result.data.tweets.get(i).favoriteCount)));
                                if (result.data.tweets.get(i).user.name != null)
                                    allResult.setName1(String.valueOf(result.data.tweets.get(i).user.name));
                                if (result.data.tweets.get(i).user.screenName != null)
                                    allResult.setScreenName(String.valueOf(result.data.tweets.get(i).user.screenName));
                                if (result.data.tweets.get(i).source != null)
                                    allResult.setSource1(String.valueOf(result.data.tweets.get(i).source));
                                if (result.data.tweets.get(i).text != null)
                                    allResult.setText(String.valueOf(result.data.tweets.get(i).text));
                                if (result.data.tweets.get(i).user.profileImageUrl != null)
                                    allResult.setProfileImageUrl(String.valueOf(result.data.tweets.get(i).user.profileImageUrl));
                                allResults.add(allResult);

                            }
                            madapter = new TweetsAdapter(SearchActivity.this, R.layout.tweet_list_item, allResults);
                            lv_Tweets.setAdapter(madapter);
                            pd.dismiss();
                        }

                        @Override
                        public void failure(TwitterException exception) {
                            pd.dismiss();
                            Log.i("exception", exception.toString());

                        }
                    });
                }

            }
        });


    }

    public class TweetsHolder {
        private TextView name;
        private TextView username;
        private TextView tweet;
        private TextView retweet;
        private TextView fav;
        private ImageView profileImage;

    }

    public class TweetsAdapter extends ArrayAdapter<AllTweets> {

        private Context c;
        private List<AllTweets> Item_list;

        public TweetsAdapter(Context context, int resource, List<AllTweets> objects) {
            super(context, resource, objects);
            this.c = context;
            this.Item_list = objects;
        }


        @Override
        public void add(AllTweets object) {
            super.add(object);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (userRadio.isChecked()) ? 1 : 0;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            lv_Tweets.invalidateViews();
        }

        @Override
        public int getCount() {
            return Item_list.size();
        }

        @Override
        public AllTweets getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(AllTweets item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            int type = getItemViewType(position);
            if (v == null) { // Inflate the layout according to the view type
                if (type == 0) {
                    final TweetsHolder tweetsHolder;
                    if (convertView == null) {
                        tweetsHolder = new TweetsHolder();
                        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.tweet_list_item, parent, false);
                        tweetsHolder.name = (TextView) convertView.findViewById(R.id.NameTitle);
                        tweetsHolder.username = (TextView) convertView.findViewById(R.id.username);
                        tweetsHolder.tweet = (TextView) convertView.findViewById(R.id.Tweet);
                        tweetsHolder.fav = (TextView) convertView.findViewById(R.id.tvFavCount);
                        tweetsHolder.retweet = (TextView) convertView.findViewById(R.id.tvRetweetCount);
                        tweetsHolder.profileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
                        convertView.setTag(tweetsHolder);
                    } else {
                        tweetsHolder = (TweetsHolder) convertView.getTag();
                    }

                    ImageLoader.getInstance().displayImage(Item_list.get(position).getProfileImageUrl(), tweetsHolder.profileImage);
                    tweetsHolder.name.setText(Item_list.get(position).getName1());
                    tweetsHolder.username.setText("@" + Item_list.get(position).getScreenName());
                    tweetsHolder.tweet.setText(Item_list.get(position).getText());
                    tweetsHolder.fav.setText(String.valueOf(Item_list.get(position).getFavoriteCount()));
                    tweetsHolder.retweet.setText(String.valueOf(Item_list.get(position).getRetweetCount()));
                } else {
                    final TweetsHolder tweetsHolder;
                    if (convertView == null) {
                        tweetsHolder = new TweetsHolder();
                        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.user_list_item, parent, false);
                        tweetsHolder.name = (TextView) convertView.findViewById(R.id.NameTitle);
                        tweetsHolder.username = (TextView) convertView.findViewById(R.id.username);
                        tweetsHolder.fav = (TextView) convertView.findViewById(R.id.tvFavCount);
                        tweetsHolder.retweet = (TextView) convertView.findViewById(R.id.tvRetweetCount);
                        tweetsHolder.profileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
                        convertView.setTag(tweetsHolder);
                    } else {
                        tweetsHolder = (TweetsHolder) convertView.getTag();
                    }

                    ImageLoader.getInstance().displayImage(Item_list.get(position).getProfileImageUrl(), tweetsHolder.profileImage);
                    tweetsHolder.name.setText(Item_list.get(position).getName1());
                    tweetsHolder.username.setText("@" + Item_list.get(position).getScreenName());
                    tweetsHolder.fav.setText(String.valueOf(Item_list.get(position).getFavoriteCount()));
                    tweetsHolder.retweet.setText(String.valueOf(Item_list.get(position).getRetweetCount()));
                }
            }


            return convertView;
        }
    }
}
