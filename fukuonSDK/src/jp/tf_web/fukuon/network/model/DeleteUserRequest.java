package jp.tf_web.fukuon.network.model;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

@SuppressWarnings("serial")
public class DeleteUserRequest  extends Request {
	private static final String TAG  = "DeleteUserRequest";
	private String server;
	private User user;
	
	public DeleteUserRequest(String server,User user){
		this.setServer( server );
		this.setUser( user );
	}

	@Override
	public Response doInBackground() {
		User u = this.getUser();

		//リクエストを送信先		
		String url = "http://"+this.getServer()+":3000/user/"+u.getId();
				
		//URLをリクエストに設定
		HttpDelete req = new HttpDelete(url);

		DefaultHttpClient httpClient = new DefaultHttpClient();		
		HttpResponse httpResp = null;
		try {
			httpResp = httpClient.execute(req);
		} catch (Exception e) {
		    Log.e(TAG, "Error Execute");
		}
		if(httpResp == null) {
			Response resp = new Response(Response.STATUS_ERROR,500);
			return resp;
		}
		
		Response resp = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//ステータスコード取得
		int status = httpResp.getStatusLine().getStatusCode();
		//Log.d(TAG, "status:"+status);
		try {
			//レスポンスを取得
	        httpResp.getEntity().writeTo(outputStream);
	        //Log.d(TAG, "body:"+outputStream.toString());
	    } catch (Exception e) {
	        Log.e(TAG, "Error");
	    }
		
		if((HttpStatus.SC_OK == status)
			|| (HttpStatus.SC_CREATED == status)){
			//リクエスト成功
			try {
		        resp = new DeleteUserResponse(Response.STATUS_SUCCESS,status,outputStream.toString());
		    } catch (Exception e) {
		        Log.e(TAG, "Error");
		    }
		}else{
			//200 以外のレスポンスの場合
			resp = new Response(Response.STATUS_ERROR,status);
		}
		return resp;
	}

	private void setServer(String server) {
		this.server = server;
	}
	
	private String getServer() {
		return this.server;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
