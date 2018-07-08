package com.gnomusic.application.gnoblackboard.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gnomusic.application.gnoblackboard.MusicDTO;

import java.util.ArrayList;

public class MusicService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private int position = -1;
    private ArrayList<MusicDTO> list;

    public static String TAG = "Test";

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        //재생 중 미디어 끝에 왔을 때 사용될 메소드입니다.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position < list.size()) {
                    play(list.get(position));
                    position++;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /////////////////////////////////
    // MediaPlayer 컨트롤 메소드
    /////////////////////////////////

    // 기본적인 플레이와 셋팅을 담당합니다.
    public void play(MusicDTO musicDTO) {
       try {
           Uri musicURI = Uri.withAppendedPath(
                   MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+ musicDTO.getId()
           );

           //MediaPlayer 세팅
           mediaPlayer.reset();
           mediaPlayer.setDataSource(this, musicURI);
           mediaPlayer.prepare();
           mediaPlayer.start();
           isPlaying = true;

       } catch (Exception e) {
           Toast.makeText(this, "Error : " + e, Toast.LENGTH_SHORT).show();
           stopSelf();
       }
    }

    public void play() {
        if (!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public void pause() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void prev() {
        if (position - 1 >= 0) {
            position--;
            play(list.get(position));
            isPlaying = true;
        }
    }

    public void next() {
        if (position + 1 >= list.size()) {
            position++;
            play(list.get(position));
            isPlaying = false;
        }
    }
}
