package com.TQFramework;

import android.content.Context;

public class C3AudioEngine {
	private static C3MusicEngine backgroundMusicPlayer;
    private static C3SoundEngine soundPlayer;
    private static Context mContext;
    private static String sResSourcePath;
    
    public static void setContext(Context context){
    	mContext = context;
    }
    
    public C3AudioEngine(Context context){
    	this.mContext = context;
    	
    }
    
    public static void init(){
    	backgroundMusicPlayer = new C3MusicEngine(mContext);
			backgroundMusicPlayer.SetResPath(sResSourcePath);
    	soundPlayer = new C3SoundEngine(mContext);
		  soundPlayer.SetResPath(sResSourcePath);
    }

	public static void SetResPath(String sResPath) {
		sResSourcePath=sResPath;
	}
	
	public static void preloadBackgroundMusic(String path){
    	backgroundMusicPlayer.preloadBackgroundMusic(path);
    }
    
    public static void playBackgroundMusic(String path, boolean isLoop){
    	backgroundMusicPlayer.playBackgroundMusic(path, isLoop);
    }
    
    public static void stopBackgroundMusic(){
    	backgroundMusicPlayer.stopBackgroundMusic();
    }
    
    public static void pauseBackgroundMusic(){
    	backgroundMusicPlayer.pauseBackgroundMusic();
    }
    
    public static void resumeBackgroundMusic(){
    	backgroundMusicPlayer.resumeBackgroundMusic();
    }
    
    public static void rewindBackgroundMusic(){
    	backgroundMusicPlayer.rewindBackgroundMusic();
    }
    
    public static boolean isBackgroundMusicPlaying(){
    	return backgroundMusicPlayer.isBackgroundMusicPlaying();
    }
    
    public static float getBackgroundMusicVolume(){
    	return backgroundMusicPlayer.getBackgroundVolume();
    }
    
    public static void setBackgroundMusicVolume(float volume){
    	backgroundMusicPlayer.setBackgroundVolume(volume);
    }
    
    public static int playEffect(String path, boolean isLoop){
    	return soundPlayer.playEffect(path, isLoop);
    }
    
    public static void stopEffect(int soundId){
    	soundPlayer.stopEffect(soundId);
    }
    
    public static void pauseEffect(int soundId){
    	soundPlayer.pauseEffect(soundId);
    }
    
    public static void resumeEffect(int soundId){
    	soundPlayer.resumeEffect(soundId);
    }
    
    public static float getEffectsVolume(){
    	return soundPlayer.getEffectsVolume();
    }
    
    public static void setEffectsVolume(float volume){
    	soundPlayer.setEffectsVolume(volume);
    }
    
    public static void preloadEffect(String path){
    	soundPlayer.preloadEffect(path);
    }
    
    public static void unloadEffect(String path){
    	soundPlayer.unloadEffect(path);
    }
    
    public static void stopAllEffects(){
    	soundPlayer.stopAllEffects();
    }
    
    public static void pauseAllEffects(){
    	soundPlayer.pauseAllEffects();
    }
    
    public static void resumeAllEffects(){
    	soundPlayer.resumeAllEffects();
    }
    
    public static void end(){
    	backgroundMusicPlayer.end();
    	soundPlayer.end();
    }
}
