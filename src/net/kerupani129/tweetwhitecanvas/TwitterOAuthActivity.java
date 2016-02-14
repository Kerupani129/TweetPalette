package net.kerupani129.tweetwhitecanvas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import net.kerupani129.tweetwhitecanvas.util.TwitterUtil;
import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends Activity {

	// 変数
	private Handler mHandler = new Handler();
    private String mCallbackURL;
    private AsyncTwitter mTwitter;
    private RequestToken mRequestToken;

	/**
	 * 生成時の処理
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

    	showToast("testToast");

		// 表示
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_oauth);

        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtil.getAsyncTwitterInstance(this);
        mTwitter.addListener(new TwitterAdapter() {

            @Override
            public void gotOAuthRequestToken(RequestToken token) {
            	mRequestToken = token;
            	String url = mRequestToken.getAuthorizationURL();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                if (token != null) {
                	mHandler.post(new Runnable() {
                		@Override
                		public void run() {
                	        showToast("認証成功");
                		}
                	});
                	succeededOAuth(token);
                } else {
                	mHandler.post(new Runnable() {
                		@Override
                		public void run() {
                	        showToast("認証失敗。もう一度お試しください");
                		}
                	});
                	failedOAuth();
                }
            }

        });

        findViewById(R.id.action_start_oauth).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	mTwitter.getOAuthRequestTokenAsync(mCallbackURL);
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
     * インテント生成時
     */
    @Override
    public void onNewIntent(Intent intent) {

        if (!intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }

        // ブラウザからのコールバックで呼ばれる
        final Uri uri = intent.getData();
        final String verifier = uri.getQueryParameter("oauth_verifier");
        if (verifier != null) {
            mTwitter.getOAuthAccessTokenAsync(mRequestToken, verifier);
        }

    }

    /**
     * 認証成功時
     */
    private void succeededOAuth(AccessToken accessToken) {
        TwitterUtil.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * 認証失敗時
     */
    private void failedOAuth() {}

    /**
     * Toast 表示
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
