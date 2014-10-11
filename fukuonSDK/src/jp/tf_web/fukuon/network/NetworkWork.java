package jp.tf_web.fukuon.network;

import jp.tf_web.fukuon.network.model.Response;


/** 各APIのレスポンス受け取りワーカー
 * 
 * @author furukawanobuyuki
 *
 */
public interface NetworkWork {
	public void response(Response resp);
}
