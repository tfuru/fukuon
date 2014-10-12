package jp.tf_web.fukuon.network.model;

import java.io.Serializable;
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
	
	public Response(String status,int code){
		this.setStatus(status);
		this.setCode(code);
	}
	
	//レスポンスをパースして作成
	public Response(String status,int code,String jsonStr){		
		//Log.d(TAG, jsonStr);
		this.setStatus(status);
		this.setCode(code);
		this.setSrcJson(jsonStr);
		this.parse(jsonStr);
	}
	
	//レスポンスをパースする
	private void parse(String jsonStr){
		try{
			srcJsonObj = new JSONObject(jsonStr);
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
	public String getJsonString(JSONObject jsonObj,String key,String def) throws JSONException{
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
	public int getJsonInt(JSONObject jsonObj,String key,String def) throws JSONException{
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
}
