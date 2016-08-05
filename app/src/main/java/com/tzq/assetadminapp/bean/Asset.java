package com.tzq.assetadminapp.bean;

import java.io.Serializable;

/**
 * Created by Rachel on 2016/3/21.
 */
public class Asset implements Serializable {
    private String assetID;
    private String assetName;
    private String assetModle;
    private String assetType;
    private String epc;
    private String location;
    private String createTime;
    private int status;

    public Asset() {
    }

    public Asset(String assetID, String assetName, String assetModle, String assetType, String epc, String location, String createTime,int status) {
        this.assetID = assetID;
        this.assetName = assetName;
        this.assetModle = assetModle;
        this.assetType = assetType;
        this.epc = epc;
        this.location = location;
        this.createTime = createTime;
        this.status = status;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }


    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetModle(String assetModle) {
        this.assetModle = assetModle;
    }

    public void setAssetType(String assetType) {
        this.assetType= assetType;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAssetID() {
        return assetID;
    }


    public String getAssetName() {
        return assetName;
    }

    public String getAssetModle() {
        return assetModle;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getEpc() {
        return epc;
    }

    public String getLocation() {
        return location;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getStatus() {
        return status;
    }
}
