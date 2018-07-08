/**
 * activity_music과 mediaPlayer를 다루는 클래스입니다.
 *
 * @Developer : GNOblackboard
 * @Date : June 15 18
 * @Last Edit : June 20 18
 * @Reference
 * https://bit.ly/2LGlrm6 -> MediaStore ContentResolver
 */

package com.gnomusic.application.gnoblackboard.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnomusic.application.gnoblackboard.MusicDTO;
import com.gnomusic.application.gnoblackboard.R;

import java.util.ArrayList;

import com.gnomusic.application.gnoblackboard.services.MusicService;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    // 뷰 객체
    private TextView title;
    private ImageView album;
    private Button prev, play, next;
    // private SeekBar seekBar;
    private Bitmap bitmap;
    // MusicList 관련
    private ArrayList<MusicDTO> list;
    // MediaPlayer
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;
    //private ProgressUpdate progressUpdate;
    private int position; // 곡의 재생 인덱스, 5곡일시 0~4
    // Service 관련
    private MusicService mService;
    boolean isService = false;
    // Debug
    public static String TAG  = "Test";

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder)service;
            mService = binder.getService();
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isService = false;
        }
    };

    /***
     * onCreate()
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Log.i(TAG, "onCreate()");

        Intent intent = getIntent();
        mediaPlayer = new MediaPlayer();
        title = findViewById(R.id.titleTextView);
        album = findViewById(R.id.albumArtImageView);
        prev = findViewById(R.id.Prev);
        play = findViewById(R.id.Play);
        next = findViewById(R.id.Next);
        // seekBar = findViewById(R.id.MusicProgressbar);

        position = intent.getIntExtra("position",0);
        list = (ArrayList<MusicDTO>) intent.getSerializableExtra("list");

        Log.i(TAG, "세팅 완료");

        prev.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);


       // progressUpdate = new ProgressUpdate();
        //progressUpdate.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "미디어 플레이어 변화");
                if(position < list.size()) {
                    play.setText("Pause");
                    setMusic(list.get(position));
                }
            }
        });

        /*
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //시크바가 움직이는 동안 호출됩니다.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            //seekBar가 터치 되는 순간 실행합니다.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            //터치가 떨어지는 순간 실행합니다.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress() >= 0) {
                    mediaPlayer.start();
                }
            }
        });
        */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Play:
                if (play.getText().equals("Play")) {
                    play.setText("Pause");
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();
                } else if (play.getText().equals("Pause")) {
                    play.setText("Play");
                    mediaPlayer.pause();
                }
                break;
            case R.id.Prev:
                if(position-1 >= 0) {
                    position--;
                    setMusic(list.get(position));
                    //seekBar.setProgress(0);
                    play.setText("Play");
                }
                break;
            case R.id.Next:
                if(position+1 < list.size()) {
                    position++;
                    setMusic(list.get(position));
                    //seekBar.setProgress(0);
                    play.setText("Play");
                }
        }
    }

    public void setMusic(MusicDTO musicDTO) {
        try {
            if (mediaPlayer.isPlaying()) {
                play.setText("Pause");
            } else {
                play.setText("Play");
            }
            bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDTO.getAlbumId()), getApplication()));
            album.setImageBitmap(bitmap);
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static String getCoverArtPath(long albumId, Context context) {
        Log.i(TAG, "getCoverArtPath");
        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );

        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if(queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    /*
    class ProgressUpdate extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                try {
                    Thread.sleep(100);
                    if(mediaPlayer!=null) {
                        //seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    Toast.makeText(MusicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(mediaPlayer!= null) { // MediaPlayer를 해제합니다.
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
