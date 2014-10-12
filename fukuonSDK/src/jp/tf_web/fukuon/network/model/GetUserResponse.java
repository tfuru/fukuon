package jp.tf_web.fukuon.network.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class GetUserResponse extends Response{

	private List<User> userList;
	
	public GetUserResponse(String status,int code,String jsonStr) {
		super(status,code,jsonStr);
		try{
			//data部分をパースする
			JSONArray srcJsonArr = new JSONArray(jsonStr);
			if(srcJsonArr != null){
				userList = new ArrayList<User>();
				for(int i=0;i< srcJsonArr.length();i++){
					JSONObject obj = srcJsonArr.getJSONObject(i);
					userList.add( new User(obj) );
				}
			}
		}
		catch (JSONException e) {
			
		}
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

}
