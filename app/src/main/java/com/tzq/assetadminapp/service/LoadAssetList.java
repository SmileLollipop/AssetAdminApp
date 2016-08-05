package com.tzq.assetadminapp.service;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.tzq.assetadminapp.adapter.MyItemRecyclerViewAdapter;
import com.tzq.assetadminapp.bean.Asset;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rachel on 2016/3/25.
 */
public class LoadAssetList {

    private JSONParse jsonParser = new JSONParse();
    private static JSONObject json;
    private List<Asset> assetList = new ArrayList<>();
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String ASSETS = "assets";
    private static final String ID = "assetID";
    private static final String NAME = "assetName";
    private static final String MODLE = "assetModle";
    private static final String TYPE = "assetType";
    private static final String EPC = "epc";
    private static final String LOCATION = "location";
    private static final String TIME= "createTime";
    private static String assetInfoURL = "http://192.168.1.109/assetAdmin/getAssetInfo.php";

    public List<Asset> newInstance() {
        List<NameValuePair> params = new ArrayList();
        json = jsonParser.makeHttpRequest("POST",assetInfoURL,params);
        try {
            if (json.getInt(SUCCESS) == 1) {
                JSONArray assets = json.getJSONArray(ASSETS);
                for (int i = 0; i < assets.length();i++) {
                    JSONObject obj = assets.getJSONObject(i);
                    Asset asset=new Asset();
                    asset.setAssetID(obj.getString(ID));
                    asset.setAssetName(obj.getString(NAME));
                    asset.setAssetModle(obj.getString(MODLE));
                    asset.setAssetType(obj.getString(TYPE));
                    asset.setEpc(obj.getString(EPC));
                    asset.setLocation(obj.getString(LOCATION));
                    asset.setCreateTime(obj.getString(TIME));

                    assetList.add(i,asset);
                }
                return assetList;
            } else {
                Log.i("my_info", "else");
                //   Toast.makeText(AssetListActivity.this, json.getString(MESSAGE), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return assetList;
    }






}
