package jp.tf_web.fukuon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

public class RecvAudioRunnable implements Runnable {
	private static final String LOG_TAG = "RecvAudioRunnable";
	private AudioTrack track;
	private DatagramSocket sock;
	private int bufSize;
	private boolean isPlay = false;
	
	public RecvAudioRunnable(){
		try {
			// バッファサイズの計算
			bufSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLE_RATE,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT) * AudioConfig.SAMPLE_SIZE;

			track = new AudioTrack(AudioManager.STREAM_MUSIC, 
					AudioConfig.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, 
                    AudioFormat.ENCODING_PCM_16BIT, bufSize, 
                    AudioTrack.MODE_STREAM);
			
			this.sock = new DatagramSocket(AudioConfig.AUDIO_PORT);
		} catch (SocketException se) {
			Log.e(LOG_TAG, "SocketException: " + se.toString());
		} catch (IOException ie) {
			Log.e(LOG_TAG, "IOException" + ie.toString());
		}
	}
	
	@Override
	public void run() {
		this.track.play();
        this.isPlay = true;
        byte[] buf = new byte[bufSize];
        while(this.isPlay)
        {
            DatagramPacket pack = new DatagramPacket(buf, buf.length);
            try {
				sock.receive(pack);
	            Log.d(LOG_TAG, "recv pack: " + pack.getLength());
	            track.write(pack.getData(), 0, pack.getLength());
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        Log.d(LOG_TAG, "stop");
        this.track.stop();
	}

	public void stopPlay(){
		this.isPlay = false;
	}
}