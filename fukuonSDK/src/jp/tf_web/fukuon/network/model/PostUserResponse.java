package jp.tf_web.fukuon.network.model;

import org.json.JSONException;

@SuppressWarnings("serial")
public class PostUserResponse extends Response{

	private User user;
	
	public PostUserResponse(String status,int code,String jsonStr) {
		super(status,code,jsonStr);
		try{
			if(srcJsonObj == null) return;
			
			//data部分をパースする
			User tmp = new User(srcJsonObj);
			if(srcJsonObj.getInt("id") >= 0) tmp.setId( srcJsonObj.getInt("id") );
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
