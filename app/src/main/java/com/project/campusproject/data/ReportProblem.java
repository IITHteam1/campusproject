package com.project.campusproject.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportProblem {

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Issue getDrainage() {
        return drainage;
    }

    public void setDrainage(Issue drainage) {
        this.drainage = drainage;
    }

    public Issue getElectrical() {
        return electrical;
    }

    public void setElectrical(Issue electrical) {
        this.electrical = electrical;
    }

    public Issue getGarbage() {
        return garbage;
    }

    public void setGarbage(Issue garbage) {
        this.garbage = garbage;
    }

    public Issue getOther() {
        return other;
    }

    public void setOther(Issue other) {
        this.other = other;
    }

    @SerializedName("drainage")
    @Expose
    private Issue drainage;

    @SerializedName("electrical")
    @Expose
    private Issue electrical;

    @SerializedName("garbage")
    @Expose
    private Issue garbage;

    @SerializedName("other")
    @Expose
    public Issue other;





}
