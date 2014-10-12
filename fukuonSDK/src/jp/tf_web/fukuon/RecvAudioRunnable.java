package jp.tf_web.fukuon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.content.Context;
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

	public RecvAudioRunnable(Context context) {
		// バッファサイズの計算
		bufSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLE_RATE,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
				* AudioConfig.SAMPLE_SIZE;

		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL /*AudioManager.STREAM_MUSIC*/,
				AudioConfig.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STREAM);
		
		//音量を最大に
		AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		int vol = manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
		manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, vol, 0);
		
		try {
			this.sock = new DatagramSocket(AudioConfig.AUDIO_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.track.play();
		this.isPlay = true;
		byte[] buf = new byte[bufSize];
		while (this.isPlay) {
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			try {
				if(sock != null){
					sock.receive(pack);
					Log.d(LOG_TAG, "recv pack: " + pack.getLength());
					track.write(pack.getData(), 0, pack.getLength());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		Log.d(LOG_TAG, "stop");
		this.track.stop();
		this.sock = null;
	}

	public synchronized void stopPlay() {
		this.isPlay = false;
	}
}
