package cn.zju.id21732091.wangzhen.pojo;

import android.graphics.Bitmap;

/**
 * Created by wzlab on 2018/6/21.
 */

public class StatusInfo {

    public String userId;
    public String creatAt;
    public String content;
    public Bitmap userImg;

    public StatusInfo(String userId, String creatAt, String content,Bitmap userImg ) {
        this.userId = userId;
        this.creatAt = creatAt;
        this.content = content;
        this.userImg = userImg;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(String creatAt) {
        this.creatAt = creatAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getUserImg() {
        return userImg;
    }

    public void setUserImg(Bitmap userImg) {
        this.userImg = userImg;
    }


}
