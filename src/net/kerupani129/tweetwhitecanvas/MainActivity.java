package net.kerupani129.tweetwhitecanvas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import net.kerupani129.tweetwhitecanvas.util.TwitterUtil;

public class MainActivity extends Activity {

	/**
	 * 生成時の処理
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 表示
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 未認証なら認証画面を表示
        if (!TwitterUtil.hasAccessToken(this)) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }

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
}
