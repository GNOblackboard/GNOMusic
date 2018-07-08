/**
 * MusicList에 대한 데이터 저장용 클래스 입니다.
 *
 * @Developer : GNOblackboard
 * @Date : June 14 18
 * @Last Edit : June 15 18
 * @Reference
 * https://bit.ly/2LGlrm6 -> MediaStore ContentResolver
 */
package com.gnomusic.application.gnoblackboard;

import java.io.Serializable;

public class MusicDTO implements Serializable {
    private String id;
    private String albumId;
    private String title;
    private String artist;

    public MusicDTO() {
    }

    public MusicDTO(String id, String albumId, String title, String artist) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MusicDTO{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}
