package jp.tf_web.fukuon.network.model;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class PostUserRequest extends Request {
	private static final String TAG  = "PostUserRequest";
	
	private String server;
	private User user;
	
	public PostUserRequest(String server,User user){
		this.setServer( server );
		this.setUser( user );
	}

	@Override
	public Response doInBackground() {
		User u = this.getUser();

		//リクエストを送信先		
		String url = "http://"+this.getServer()+":3000/user";
				
		//URLをリクエストに設定
		HttpPost req = new HttpPost(url);
		
		StringEntity entity = null;
		try {
			Map<String,Object> src = new HashMap<String, Object>();
			if(u.getId() != 0) src.put("id", u.getId());
			src.put("name", u.getName());
			src.put("photo", u.getPhotoBase64());
			src.put("show_name", u.getShowName());
			src.put("show_id", u.getShowId());
			src.put("message", u.getMessage());
			src.put("listener_cnt", u.getListenerCnt());
			
			Gson gson = new Gson();
			String json = gson.toJson(src);
			
			entity = new StringEntity( json, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(entity == null){
			//Body部分を作れなかった場合
			Response resp = new Response(Response.STATUS_ERROR,500);
			return resp;
		}
		entity.setContentType("application/json");
		
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		HttpResponse httpResp = null;
		try {
			//POSTパラメータをリクエストに設定
			req.setEntity(entity);
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
		        resp = new PostUserResponse(Response.STATUS_SUCCESS,status,outputStream.toString());
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
