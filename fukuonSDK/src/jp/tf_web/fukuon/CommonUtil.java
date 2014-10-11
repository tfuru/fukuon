package jp.tf_web.fukuon;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class CommonUtil {

	// JPG画像を Base64 文字列に変換
	public static String convertJpgStringFromBitmap(String fileName)
			throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(fileName);
		byte[] bytes;
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		bytes = output.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	// BASE64文字列からJPG画像を作る
	public static Bitmap convertBitmapFromJpeg(String data) {
		data = data.replaceFirst("data:image/jpeg;base64,", "");
		byte[] bytes = Base64.decode(data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}
}
