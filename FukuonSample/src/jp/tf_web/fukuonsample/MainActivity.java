package jp.tf_web.fukuonsample;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jp.tf_web.fukuon.AudioConfig;
import jp.tf_web.fukuon.RecvAudioRunnable;
import jp.tf_web.fukuon.SendAudioRunnable;
import jp.tf_web.fukuon.network.NetworkAsyncTask;
import jp.tf_web.fukuon.network.NetworkWork;
import jp.tf_web.fukuon.network.model.DeleteUserRequest;
import jp.tf_web.fukuon.network.model.PostUserRequest;
import jp.tf_web.fukuon.network.model.Response;
import jp.tf_web.fukuon.network.model.User;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String LOG_TAG = "MainActivity";
	
	private SendAudioRunnable sendAudioRunnable;
	private RecvAudioRunnable recvAudioRunnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//自分のIPアドレスを調べる
		TextView textView1 = (TextView)findViewById(R.id.textView1);
		setIpAddress( textView1 );
		
		//送信先アドレスを設定する
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName("192.168.1.129");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
		}
		Log.e(LOG_TAG, "addr:"+addr);
		
		//オーディオ送信 Runnable		
		sendAudioRunnable = new SendAudioRunnable(addr);
		Log.e(LOG_TAG, "sendAudioRunnable" + sendAudioRunnable);
		
		Button btnRec = (Button) findViewById(R.id.btnRec);
		btnRec.setOnClickListener(clickBtnRec);
		
		Button btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(clickBtnPlay);
		
		Button btnDeleteUser = (Button) findViewById(R.id.btnDeleteUser);
		btnDeleteUser.setOnClickListener(clickBtnDeleteUser);
		
		Button btnAddUser = (Button) findViewById(R.id.btnAddUser);
		btnAddUser.setOnClickListener(clickBtnAddUser);
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
	
    @Override
    protected void onPause() {
        super.onPause();
		if(sendAudioRunnable != null){
			//録音停止
			sendAudioRunnable.stopRecording();
		}
		if(recvAudioRunnable != null){
			//再生停止
			recvAudioRunnable.stopPlay();
		}
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(sendAudioRunnable != null){
			//録音停止
			sendAudioRunnable.stopRecording();
		}
		if(recvAudioRunnable != null){
			//再生停止
			recvAudioRunnable.stopPlay();
		}
	}
	
	private OnClickListener clickBtnRec = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//オーディオ送信
			Thread thrd = new Thread(sendAudioRunnable);
			thrd.start();
		}
	};
	
	private OnClickListener clickBtnPlay = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//オーディオ再生
			Thread thrd = new Thread(recvAudioRunnable);
			thrd.start();
		}
	};

	//ユーザー追加
	private OnClickListener clickBtnAddUser = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			String server = "192.168.1.178"; 
			User user = new User("名前","file://sample.jpg","showName","message");
			PostUserRequest req = new PostUserRequest(server,user);
			
			NetworkWork resultWork = new NetworkWork(){
				@Override
				public void response(Response resp) {
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
	};
	
	//ユーザー削除
	private OnClickListener clickBtnDeleteUser = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			String server = "192.168.1.178"; 
			User user = new User(3);
			DeleteUserRequest req = new DeleteUserRequest(server,user);
			
			NetworkWork resultWork = new NetworkWork(){
				@Override
				public void response(Response resp) {
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
	};
	
}
