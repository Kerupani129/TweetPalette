package net.kerupani129.tweetwhitecanvas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import net.kerupani129.tweetwhitecanvas.util.TwitterUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends Activity {

	// 変数
    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;

	/**
	 * 生成時の処理
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 表示
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_oauth);

        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtil.getTwitterInstance(this);

        findViewById(R.id.action_start_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorize();
            }
        });

	}

	/**
	 * メニュー生成時の処理
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// メニューを拡大して表示する
		// アクションバーがあればアイテムを追加
		getMenuInflater().inflate(R.menu.main, menu);

		return true;

	}

	/**
	 * メニュー選択時の処理
	 *
	 * Home / Up ボタンの処理は自動的にされるので、AndroidManifest.xml で
	 * 親の Activity を指定するだけでよい
	 * …というわけでもないらしい (^^;
	 *
	 * 参考: Upボタンの実装メモ
	 *     http://qiita.com/nein37/items/6a063f5462400036920b
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

    /**
     * OAuth 認証 (認可) 開始
     */
    private void startAuthorize() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    // 失敗
                }
            }
        };
        task.execute();

    }

    /**
     * インテント生成時
     */
    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功
                    showToast("認証成功");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                    showToast("認証失敗");
                }
            }
        };
        task.execute(verifier);
    }

    /**
     * 認証成功時
     */
    private void successOAuth(AccessToken accessToken) {
        TwitterUtil.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Toast 表示
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
