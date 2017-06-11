package com.wuruoye.note.model;

import com.droi.sdk.core.DroiExpose;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiObject;
import com.droi.sdk.core.DroiReference;

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

public class UpNote extends DroiObject {
    @DroiExpose
    private String user;
    @DroiExpose
    private String content;
    @DroiExpose
    private int direct;
    @DroiExpose
    private int color;
    @DroiExpose
    private int year;
    @DroiExpose
    private int month;
    @DroiExpose
    private int day;
    @DroiExpose
    private DroiFile bkFile;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public DroiFile getBkFile() {
        return bkFile;
    }

    public void setBkFile(DroiFile bkFile) {
        this.bkFile = bkFile;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }
}
