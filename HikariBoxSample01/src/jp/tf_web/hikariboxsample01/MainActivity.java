package jp.tf_web.hikariboxsample01;

import java.io.IOException;
import java.util.HashMap;

import jp.tf_web.fukuon.AudioConfig;
import jp.tf_web.fukuon.RecvAudioRunnable;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	private TextView txtAddress;
	private MediaPlayer mp;
	private SurfaceHolder holder;
	
	//副音声スレッド
	private Thread audioThrd;
	private RecvAudioRunnable recvAudioRunnable;
	
	private HashMap<String, String> filenNameList = new HashMap<String,String>(){
		{
			put("btn1","ring.mp4");
			put("btn2","vtOGSdZ1gU.mp4");
			put("btn3","suzumiya.mp4");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//動画を画面に追加する
		SurfaceView preview = (SurfaceView) findViewById(R.id.surfaceView1);
		holder = preview.getHolder();
	    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    holder.addCallback(this);
	    	    
	    // IPアドレスを画面に表示する
		txtAddress = (TextView) findViewById(R.id.txtAddress);
		setIpAddress( txtAddress );

		ImageButton btnPlay = (ImageButton) findViewById(R.id.button1);
		btnPlay.setOnClickListener(clickBtnPlay);

		ImageButton button2 = (ImageButton) findViewById(R.id.button2);
		button2.setOnClickListener(clickButton2);
		
		ImageButton button3 = (ImageButton) findViewById(R.id.button3);
		button3.setOnClickListener(clickButton3);
		
		ImageButton button4 = (ImageButton) findViewById(R.id.button4);
		button4.setOnClickListener(clickButton4);
	}

	//IPアドレスを画面に設定
	private void setIpAddress(final TextView txtView){
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
		//動画停止
		mp4stop();
		//ふくおん停止
		recvAudioStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//動画停止
		mp4stop();
		//ふくおん停止
		recvAudioStop();
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
		/*
		 * if(txtSample == null) super.dispatchKeyEvent(e);
		 * 
		 * if(e.getAction() == KeyEvent.ACTION_UP){ //キーが離された時
		 * switch(e.getKeyCode()){ case KeyEvent.KEYCODE_DPAD_UP: //上キー
		 * txtSample.setText("KEYCODE_DPAD_UP"); return true; case
		 * KeyEvent.KEYCODE_DPAD_DOWN: //下キー
		 * txtSample.setText("KEYCODE_DPAD_DOWN"); return true; case
		 * KeyEvent.KEYCODE_DPAD_LEFT: //左キー
		 * txtSample.setText("KEYCODE_DPAD_LEFT"); return true; case
		 * KeyEvent.KEYCODE_DPAD_RIGHT: //右キー
		 * txtSample.setText("KEYCODE_DPAD_RIGHT"); return true; case
		 * KeyEvent.KEYCODE_DPAD_CENTER: //十字中央キー
		 * txtSample.setText("KEYCODE_DPAD_CENTER"); return true; default: } }
		 */
		return super.dispatchKeyEvent(e);
	}

	private OnClickListener clickBtnPlay = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		    //再生
		    mp4play(holder, MainActivity.this.filenNameList.get("btn1"));		    
		    //副音声 再生
		    recvAudioPlay();
		}
	};

	private OnClickListener clickButton2 = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		    //再生
		    mp4play(holder,MainActivity.this.filenNameList.get("btn2"));
		    //副音声 再生
		    recvAudioPlay();
		}
	};

	private OnClickListener clickButton3 = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
		    //再生
		    mp4play(holder,MainActivity.this.filenNameList.get("btn3"));
		    //副音声 再生
		    recvAudioPlay();
		}
	};
	
	
	private OnClickListener clickButton4 = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			int vol = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if(vol == 0){
				//最大ボリューム
				vol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
			}
			else{
				//ミュート
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			}
		}
	};
	
	//副音声 再生
	private void recvAudioPlay(){
		recvAudioStop();
		// オーディオ受信
		MainActivity.this.audioThrd = new Thread(new RecvAudioRunnable(this));
		MainActivity.this.audioThrd.start();
	}
	
	//副音声 停止
	private void recvAudioStop(){
		if(MainActivity.this.recvAudioRunnable != null){
			//副音声スレッド停止
			MainActivity.this.recvAudioRunnable.stopPlay();
			MainActivity.this.recvAudioRunnable = null;
			MainActivity.this.audioThrd = null;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private void mp4play(SurfaceHolder holder,String filen_ame){
		if(filen_ame == null) return;

		try {
			//再生停止
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
	private void mp4stop(){
		if(mp == null) return;
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
