package com.project.campusproject.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Issue {
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @SerializedName("desc")
    @Expose
    private String desc;

    @SerializedName("status")
    @Expose
    private int status;

}
