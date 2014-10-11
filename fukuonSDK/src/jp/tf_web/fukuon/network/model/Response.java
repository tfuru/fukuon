package jp.tf_web.fukuon.network.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** レスポンスの基本オブジェクト
 * 
 * @author furukawanobuyuki
 *
 */
@SuppressWarnings("serial")
public class Response implements Serializable {
	private static String TAG = "Response";
	
	//レスポンス ステータス
	public static String STATUS_SUCCESS = "success";
	public static String STATUS_ERROR = "error";
	
	//ソース文字列を保存
	protected String srcJson;
	
	//ソース文字列をJSONに変換した
	protected JSONObject srcJsonObj;
	
	//レスポンスのステータス
	protected String status;
	
	//レスポンスのステータスコード
	protected int code;
	
	//他データ
	protected Map<String,Object> data;
	
	public Response(String status,int code,Map<String,Object> data){
		this.setStatus(status);
		this.setCode(code);
		this.setData(data);
	}
	
	//レスポンスをパースして作成
	public Response(String jsonStr){		
		Log.d(TAG, jsonStr);
		this.setSrcJson(jsonStr);
		this.parse(jsonStr);
	}
	
	//レスポンスをパースする
	private void parse(String jsonStr){
		try{
			srcJsonObj = new JSONObject(jsonStr);
			/*
			if(srcJsonObj != null){
				//パース
				this.setStatus( this.getJsonString(srcJsonObj, "status", null) );
				this.setCode(  this.getJsonInt(srcJsonObj, "code", null) );
				
				//data 部分のパース
				JSONObject tmpData = srcJsonObj.getJSONObject("data");
				if(tmpData != null){
					if(data == null) data = new HashMap<String,Object>();
					if(tmpData.getString("message") != null){
						data.put("message", tmpData.getString("message"));
					}
				}
			}*/
		}
		catch (JSONException e) {
			
		}
	}
	
	public String getSrcJson() {
		return srcJson;
	}

	public void setSrcJson(String srcJson) {
		this.srcJson = srcJson;
	}

	/** JSONから指定キーの文字列を取得
	 * 
	 * @param jsonObj
	 * @param key
	 * @param def
	 * @return
	 * @throws JSONException
	 */
	private String getJsonString(JSONObject jsonObj,String key,String def) throws JSONException{
		String str = jsonObj.getString(key);
		return str;
	}
	
	/** JSONから指摘キーの数値を取得
	 * 
	 * @param jsonObj
	 * @param key
	 * @param def
	 * @return
	 * @throws JSONException
	 */
	private int getJsonInt(JSONObject jsonObj,String key,String def) throws JSONException{
		int i = jsonObj.getInt(key);
		return i;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
}
