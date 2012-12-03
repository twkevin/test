package com.TQFramework;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class C3MusicEngine {
	private String sResourcePath;
	private static final String TAG = "C3MusicEngine";
	private float mLeftVolume;
	private float mRightVolume;
	private MediaPlayer mBackgroundMediaPlayer;
	private boolean mIsPaused;
	private String mCurrentPath;
	private AudioManager mAudioManager = null;
	private boolean mIsMute = false;
	private int mPreVolume = 0;
	
	//@Override
	public void SetResPath(String sResPath) {
		sResourcePath = sResPath;
	}
	
	public C3MusicEngine(Context context){
		initData();
		
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		mIsMute = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0 ? true : false;
		mPreVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}
	
	public void preloadBackgroundMusic(String path){
		if ((mCurrentPath == null) || (! mCurrentPath.equals(path))){
			// preload new background music
			
			// release old resource and create a new one
			if (mBackgroundMediaPlayer != null){
				mBackgroundMediaPlayer.release();				
			}				

			mBackgroundMediaPlayer = createMediaplayerFromPath(path);
			
			// record the path
			mCurrentPath = path;
		}
	}
	
	public void playBackgroundMusic(String path, boolean isLoop){
		if (mCurrentPath == null){
			// it is the first time to play background music
			// or end() was called
			mBackgroundMediaPlayer = createMediaplayerFromPath(path);	
			mCurrentPath = path;
		} 
		else {
			if (! mCurrentPath.equals(path)){
				// play new background music
				
				// release old resource and create a new one
				if (mBackgroundMediaPlayer != null){
					mBackgroundMediaPlayer.release();				
				}				
				mBackgroundMediaPlayer = createMediaplayerFromPath(path);
				
				// record the path
				mCurrentPath = path;
			}
		}
		
		if (mBackgroundMediaPlayer == null){
			Log.e(TAG, "playBackgroundMusic: background media player is null");
		} else {		
			// if the music is playing or paused, stop it
			mBackgroundMediaPlayer.stop();			
			
			mBackgroundMediaPlayer.setLooping(isLoop);
			
			try {
				mBackgroundMediaPlayer.prepare();
				mBackgroundMediaPlayer.seekTo(0);
				mBackgroundMediaPlayer.start();
				
				this.mIsPaused = false;
			} catch (Exception e){
				Log.e(TAG, "playBackgroundMusic: error state");
			}			
		}
	}
	
	public void stopBackgroundMusic(){
		if (mBackgroundMediaPlayer != null){
			mBackgroundMediaPlayer.stop();
			
			// should set the state, if not , the following sequence will be error
			// play -> pause -> stop -> resume
			this.mIsPaused = false;
		}
	}
	
	public void pauseBackgroundMusic(){		
		if (mBackgroundMediaPlayer != null && mBackgroundMediaPlayer.isPlaying()){
			mBackgroundMediaPlayer.pause();
			this.mIsPaused = true;
		}
	}
	
	public void resumeBackgroundMusic(){
		if (mBackgroundMediaPlayer != null && this.mIsPaused){
			mBackgroundMediaPlayer.start();
			this.mIsPaused = false;
		}
	}
	
	public void rewindBackgroundMusic(){		
		if (mBackgroundMediaPlayer != null){
			mBackgroundMediaPlayer.stop();			
			
			try {
				mBackgroundMediaPlayer.prepare();
				mBackgroundMediaPlayer.seekTo(0);
				mBackgroundMediaPlayer.start();
				
				this.mIsPaused = false;
			} catch (Exception e){
				Log.e(TAG, "rewindBackgroundMusic: error state");
			}			
		}
	}
	
	public boolean isBackgroundMusicPlaying(){
		boolean ret = false;
		
		if (mBackgroundMediaPlayer == null){
			ret = false;
		} else {
			ret = mBackgroundMediaPlayer.isPlaying();
		}
		
		return ret;
	}
	
	public void end(){
		if (mBackgroundMediaPlayer != null){
			mBackgroundMediaPlayer.release();
		}
		
		initData();
		
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mPreVolume, 0);
		if(mIsMute){
			mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}
	}
	
	public float getBackgroundVolume(){
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int CurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		this.mLeftVolume=this.mRightVolume=(float)CurVolume/(float)maxVolume;
		if (this.mBackgroundMediaPlayer != null){
			return (this.mLeftVolume + this.mRightVolume) / 2;
		} else {
			return 0.0f;
		}
	}
	
	public void setBackgroundVolume(float volume){
		volume = volume / 100;
		if (volume < 0.0f){
			volume = 0.0f;
		}
		
		if (volume > 1.0f){
			volume = 1.0f;
		}

	    this.mLeftVolume = this.mRightVolume = volume;
		if (this.mBackgroundMediaPlayer != null){
			int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVolume * volume), 0);
			//this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
		}
	}
	
	private void initData(){
		mLeftVolume =0.5f;
		mRightVolume = 0.5f;
		mBackgroundMediaPlayer = null;
		mIsPaused = false;
		mCurrentPath = null;
	}
	
	/**
	 * create mediaplayer for music
	 * @param path the path relative to assets
	 * @return 
	 */
	private MediaPlayer createMediaplayerFromPath(String path){
		MediaPlayer mediaPlayer = null;
		
		try{		
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        mediaPlayer.setDataSource(sResourcePath+"/"+ path);
	        mediaPlayer.prepare();
	        
	        //mediaPlayer.setVolume(mLeftVolume, mRightVolume);
	        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int CurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			this.mLeftVolume=this.mRightVolume=(float)CurVolume/(float)maxVolume;
	        float fVolume = (mLeftVolume + mRightVolume) / 2;
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVolume * fVolume), 0);
		}catch (Exception e) {
			mediaPlayer = null;
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
		
        return mediaPlayer;
	}
}
