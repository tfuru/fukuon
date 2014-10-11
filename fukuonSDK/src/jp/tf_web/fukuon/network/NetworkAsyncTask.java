package jp.tf_web.fukuon.network;

import jp.tf_web.fukuon.network.model.Request;
import jp.tf_web.fukuon.network.model.Response;
import android.os.AsyncTask;

public class NetworkAsyncTask  extends AsyncTask<Request, Void, Response> {
	//private static String TAG = "NetworkAsyncTask";
	private NetworkWork work;
	
	public NetworkAsyncTask(NetworkWork work){
		super();
		this.work = work;
	}
	
	@Override
	protected Response doInBackground(Request... requests) {
		//各リクエストの処理を実行
		return requests[0].doInBackground();
	}
	
	@Override
    protected void onPostExecute(Response resp) {
		//送信結果をコールバック
		this.work.response(resp);
    }
}
