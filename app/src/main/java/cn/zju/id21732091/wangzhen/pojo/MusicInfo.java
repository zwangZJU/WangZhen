package cn.zju.id21732091.wangzhen.pojo;

import android.graphics.Bitmap;

/**
 * Created by wzlab on 2018/6/20.
 */

public class MusicInfo{
    public String artist;
    public String title;
    public String duration;
    public int albumId;
    public String album;
    public long ID;
    public String uri;
    public Bitmap albumImg;

    public MusicInfo(long ID, String artist,String title,String duration,int albumId,String album,String uri){
        this.ID = ID;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.albumId = albumId;
        this.album = album;
        this.uri = uri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Bitmap getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(Bitmap albumImg) {
        this.albumImg = albumImg;
    }
}
