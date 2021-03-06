package jp.tf_web.hikariboxsample01;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.tf_web.fukuon.AudioConfig;
import jp.tf_web.fukuon.RecvAudioRunnable;
import jp.tf_web.fukuon.network.NetworkAsyncTask;
import jp.tf_web.fukuon.network.NetworkWork;
import jp.tf_web.fukuon.network.model.GetUserRequest;
import jp.tf_web.fukuon.network.model.GetUserResponse;
import jp.tf_web.fukuon.network.model.PostUserRequest;
import jp.tf_web.fukuon.network.model.PutUserRequest;
import jp.tf_web.fukuon.network.model.Response;
import jp.tf_web.fukuon.network.model.User;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
	private final String TAG = "MainActivity";

	private TextView txtAddress;
	private MediaPlayer mp;
	private SurfaceHolder holder;

	// 副音声スレッド
	private Thread audioThrd;
	private RecvAudioRunnable recvAudioRunnable;

	// メニュー部分のコンテナー
	private LinearLayout menuContainer;

	private int TIMER_PERIOD = 3 * 1000;
	private Timer timerGetUsers = new Timer();

	//配信中のユーザーリスト
	private Map<String,User> userMap;
	
	//再生中の番組
	private Integer selectTag;
	
	private HashMap<String, String> filenNameList = new HashMap<String, String>() {
		{
			put("show1", "ring.mp4");
			put("show2", "gacchiri.mp4");
		}
	};
	
	//サーバ
	//private String server = "192.168.1.178";
	private String server = "nodejs.moe.hm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 動画を画面に追加する
		SurfaceView preview = (SurfaceView) findViewById(R.id.surfaceView1);
		holder = preview.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);

		// IPアドレスを画面に表示する
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		setIpAddress(txtAddress);
		txtAddress.requestFocus();

		//btnShowMenu = (ImageButton)findViewById(R.id.btnShowMenu);
		//btnShowMenu.setOnClickListener(clickBtnShowMenu);
		
		// メニューコンテナー
		this.menuContainer = (LinearLayout) findViewById(R.id.menuContainer);

		// ユーザー一覧をサーバから取得する
		timerGetUsers.schedule(timerGetUsersTask, 0, TIMER_PERIOD);
	}

	// 配信中ユーザー一覧をサーバから取得する
	private void getUsers() {
		// 設定画面からサーバのIPは設定するべき。
		GetUserRequest req = new GetUserRequest(server);

		// 非同期でRequestを実行
		NetworkAsyncTask task = new NetworkAsyncTask(getUserResultWork);
		task.execute(req);
	}

	// ユーザー一覧取得結果
	private NetworkWork getUserResultWork = new NetworkWork() {
		@Override
		public void response(Response resp) {
			if (resp == null)
				return;
			if (MainActivity.this.menuContainer == null)
				return;

			//Log.i(TAG, "resp" + resp);
			// ここで レスポンスからユーザー必要情報を取得する
			if (resp.getStatus().equals(Response.STATUS_SUCCESS)) {
				// ユーザー一覧の取得に成功
				GetUserResponse getUserResponse = (GetUserResponse) resp;
				List<User> userList = getUserResponse.getUserList();
				if ((userList != null) && (userList.size() > 0)) {
					//ユーザー一覧
					if(userMap == null) {
						userMap = new HashMap<String,User>();
					}
					UserButton firstUb = null;
					//一覧を削除
					MainActivity.this.menuContainer.removeAllViews();
					// 一覧のボタンを増やす
					for (int i = 0; i < userList.size(); i++) {
						User u = userList.get(i);
						String key = "user_"+u.getId();
						Log.i(TAG, "key:" + key);
						userMap.put(key,u);
						
						// UserButton を メニュー欄に追加
						UserButton ub = new UserButton(MainActivity.this, u);
						MainActivity.this.menuContainer.addView(ub);
						// オーバーレイしているボタンが取得する
						ub.getBtnUserButton1().setOnClickListener(
								MainActivity.this.clickUserButton);
						
						if(firstUb == null) firstUb = ub;
					}
					//最初の物にカーソルを持っていく
					if(firstUb != null){
						firstUb.getBtnUserButton1().requestFocus();
					}
				}
			}
		}
	};

	// IPアドレスを画面に設定
	private void setIpAddress(final TextView txtView) {
		Thread thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				String addr = AudioConfig.getIPAddress();
				txtView.setText(addr);
			}
		});
		thrd.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 動画停止
		mp4stop();
		// ふくおん停止
		recvAudioStop();
		
		//読み込みタイマー
		if(timerGetUsers != null){
			timerGetUsers.cancel();
			timerGetUsers = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 動画停止
		mp4stop();
		// ふくおん停止
		recvAudioStop();
		
		//読み込みタイマー
		if(timerGetUsers != null){
			timerGetUsers.cancel();
			timerGetUsers = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	// リモコンキー 取得
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		
		if (e.getAction() == KeyEvent.ACTION_UP) { // キーが離された時
			switch (e.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_LEFT: // 左キー
				// メイン音声をミュート
				mainAudioMute(true);
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT: // 右キー
				//メイン音声を再生
				mainAudioMute(false);
				return true;
			default:
			}
		}

		return super.dispatchKeyEvent(e);
	}

	// UserButton
	private final OnClickListener clickUserButton = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Integer tag = (Integer) ((Button) view).getTag();
			if(selectTag == tag) return;
			selectTag = tag;
			Log.i(TAG, "onClick tag:" + tag);
			
			String key = "user_"+tag;
			User u = userMap.get( key );
			if(u == null) return;
			
			// 番組IDを取得して 動画名を取得
			String file_name = MainActivity.this.filenNameList
					.get("show" + u.getShowId());
			Log.i(TAG, "file_name:" + file_name);
			if (file_name == null)
				return;

			// 再生
			mp4play(holder, file_name);
			// 副音声 再生
			recvAudioPlay();
			
			//リスナー数をアップする
			userListenerCntUp(u);			
		}
	};
	
	//リスナー数をアップする
	private void userListenerCntUp(User user){
		//リスナー数をアップする
		long cnt = user.getListenerCnt();
		user.setListenerCnt(cnt+1);
		
		PutUserRequest req = new PutUserRequest(server,user);
		NetworkWork resultWork = new NetworkWork(){
			@Override
			public void response(Response resp) {
				if(resp == null) return;
				//ここで レスポンスからユーザー必要情報を取得する
				if(resp.getStatus().equals(Response.STATUS_SUCCESS)){
					//
				}
			}
		};
		
		//非同期でRequestを実行
		NetworkAsyncTask task = new NetworkAsyncTask( resultWork );
		task.execute(req);
	}
	
	//メイン音源をミュート,最大音量に
	private void mainAudioMute(boolean flg){
		AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (flg == false) {
			// ボリューム 最大
			int vol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			manager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
		} else { 
			// 消音
			Toast.makeText(this, "消音", Toast.LENGTH_SHORT).show();
			manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		}
	}
	
	// 副音声 再生
	private void recvAudioPlay() {
		Log.d(TAG,"recvAudioPlay");
		//recvAudioStop();
		MainActivity.this.recvAudioRunnable = new RecvAudioRunnable(this);
		// オーディオ受信
		MainActivity.this.audioThrd = new Thread(
				MainActivity.this.recvAudioRunnable);
		MainActivity.this.audioThrd.start();
	}

	// 副音声 停止
	private void recvAudioStop() {
		Log.d(TAG,"recvAudioStop");
		if (MainActivity.this.recvAudioRunnable == null)
			return;
		// 副音声スレッド停止
		MainActivity.this.recvAudioRunnable.stopPlay();
		MainActivity.this.recvAudioRunnable = null;
		MainActivity.this.audioThrd = null;
	}

	private TimerTask timerGetUsersTask = new TimerTask() {

		@Override
		public void run() {
			// ユーザーをリロードする
			getUsers();
		}

	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void mp4play(SurfaceHolder holder, String filen_ame) {
		if (filen_ame == null)
			return;

		try {
			// 再生停止
			mp4stop();

			mp = new MediaPlayer();
			mp.setDisplay(holder);

			AssetFileDescriptor afd = getAssets().openFd(filen_ame);
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getLength());
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void mp4stop() {
		if (mp == null)
			return;
		mp.stop();
		mp.setDisplay(null);
		mp.release();
		mp = null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}
