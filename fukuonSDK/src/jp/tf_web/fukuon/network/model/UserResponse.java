package jp.tf_web.fukuon.network.model;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class UserResponse extends Response{

	private User user;
	
	public UserResponse(String jsonStr) {
		super(jsonStr);
		try{
			if(srcJsonObj == null) return;
			
			//data部分をパースする
			User tmp = new User();
			if(srcJsonObj.getInt("id") >= 0) tmp.setId( srcJsonObj.getInt("id") );
			if(srcJsonObj.getString("name") != null) tmp.setName( srcJsonObj.getString("name") );
			if(srcJsonObj.getString("photo") != null) tmp.setPhoto( srcJsonObj.getString("photo") );
			if(srcJsonObj.getString("show_name") != null) tmp.setName( srcJsonObj.getString("show_name") );
			if(srcJsonObj.getString("message") != null) tmp.setName( srcJsonObj.getString("message") );			
			setUser( tmp );
		}
		catch (JSONException e) {
			
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}