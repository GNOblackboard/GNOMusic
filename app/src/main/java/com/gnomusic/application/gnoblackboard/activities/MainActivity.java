/**
 * activity_main을 다루는 액티비티 클래스입니다.
 *
 * @Developer : GNOblackboard
 * @Date : June 9 18
 * @Last Edit : June 24 18
 * @Reference
 * https://bit.ly/2LGlrm6 -> MediaStore ContentResolver
 * http://gun0912.tistory.com/55 || https://bit.ly/2MmBsPd -> Permission
 */

package com.gnomusic.application.gnoblackboard.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gnomusic.application.gnoblackboard.MusicDTO;
import com.gnomusic.application.gnoblackboard.R;
import com.gnomusic.application.gnoblackboard.adapters.MusicAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    //Permissions
    private final int READ_PER_CODE = 1;
    // 뷰 객체 변수
    private ListView MusicListView;
    // 음악 리스트 변수
    private ArrayList<MusicDTO> MusicArrayList;
    // 디버그
    public static String TAG  = "Test";

    /***
     * onCreate
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissons();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("list", MusicArrayList);
        startActivity(intent); /** 100KB 넘을시 강제로 VM이 셧다운 됩니다. 압축, URI로 전달하는 방식이 필요합니다. */
    }

    protected void checkPermissons() {
        int ReadPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (ReadPermissionCheck == PackageManager.PERMISSION_DENIED) {
            //권한 없음
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PER_CODE);
        } else if(ReadPermissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission Granted");
            //권한 있음
            initMusicListView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case READ_PER_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허가
                    //해당 권한을 사용해서 작업을 진행 할 수 있습니다.
                    initMusicListView();
                } else {
                    //권한 거부시
                    //사용자가 거부를 눌렀을 시 과정을 수행합니다.
                    finish();
                }
        }
    }

    /**
     * getMusicList()
     * 음악 리스트를 불러옵니다.
     */
    protected void getMusicList() {
        MusicArrayList = new ArrayList<>();
        String[] list = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, list, null, null, null);

        if (cursor == null) return;

        while(cursor.moveToNext()) {
            MusicDTO musicDTO = new MusicDTO();
            if (cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)).contains("Hangout")) // 행아웃이란 글꼴이 보이면 컨티뉴
                continue;
            musicDTO.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            musicDTO.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            musicDTO.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            musicDTO.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            MusicArrayList.add(musicDTO);
            Log.i(TAG, "ADD SONG " + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) );
        }
        cursor.close();
    }

    public void initMusicListView() {
        getMusicList(); // 디바이스 안에 있는 mp3 파일 리스트를 조회하여 List를 만듭니다.
        MusicListView = findViewById(R.id.MusicListView);
        MusicAdapter adapter = new MusicAdapter(this,MusicArrayList);
        MusicListView.setAdapter(adapter);
        MusicListView.setOnItemClickListener(this);
    }
}
