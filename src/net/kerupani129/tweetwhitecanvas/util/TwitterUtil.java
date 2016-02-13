package net.kerupani129.tweetwhitecanvas.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import net.kerupani129.tweetwhitecanvas.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtil {

	// 定数
    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    /**
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(Context context) {

		// API 情報取得
    	String api_key;
    	String api_secret;

		try {

			Map<String, String> map = ResourceUtil.getStringMapFromXml(context, R.xml.api_info);

			api_key = map.get("api_key");
			api_secret = map.get("api_secret");

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		// 認証
		TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(api_key, api_secret);

        if (hasAccessToken(context)) {
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }

        return twitter;

    }

    /**
     * アクセストークンをプリファレンスに保存
     */
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(TOKEN, accessToken.getToken());
        editor.putString(TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    /**
     * アクセストークンをプリファレンスから読み込む
     */
    public static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN, null);
        String tokenSecret = preferences.getString(TOKEN_SECRET, null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    /**
     * アクセストークン存在チェック
     */
    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }

}