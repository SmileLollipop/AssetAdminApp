package com.tzq.assetadminapp.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tzq.assetadminapp.R;
import com.tzq.assetadminapp.bean.Asset;
import com.tzq.assetadminapp.service.JSONParse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Asset asset;
    private EditText nameTv;
    private EditText modleTv;
    private EditText typeTv;
    private TextView epcTv;
    private EditText locationTv;
    private TextView timeTv;
    private Button editButton;
    private int flag1=0;
    private int flag2=0;
    private Button deleteButton;
    private static String assetInfoURL = "http://192.168.1.103/assetAdmin/updateAssetInfo.php";
    private static String deleteAssetURL = "http://192.168.1.103/assetAdmin/deleteAsset.php";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String ASSETS = "assets";
    private static final String ID = "assetID";
    private static final String NAME = "assetName";
    private static final String MODLE = "assetModle";
    private static final String TYPE = "assetType";
    private static final String EPC = "epc";
    private static final String LOCATION = "location";
    private static JSONObject json;
    private JSONParse jsonParser = new JSONParse();
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
       toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("资产详细信息");
        setSupportActionBar(toolbar);
        asset=(Asset)(getIntent().getSerializableExtra("asset"));
        nameTv=(EditText)findViewById(R.id.detail_name);
        modleTv=(EditText)findViewById(R.id.detail_model);
        typeTv=(EditText)findViewById(R.id.detail_type);
        epcTv=(TextView)findViewById(R.id.detail_epc);
        locationTv=(EditText)findViewById(R.id.detail_location);
        timeTv=(TextView)findViewById(R.id.createtime);
        editButton=(Button)findViewById(R.id.editButton);
        deleteButton=(Button)findViewById(R.id.deleteButton);
        nameTv.setText(asset.getAssetName());
        modleTv.setText(asset.getAssetModle());
        typeTv.setText(asset.getAssetType());
        epcTv.setText(asset.getEpc());
        locationTv.setText(asset.getLocation());
        timeTv.setText(asset.getCreateTime());
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editButton:
                if(flag1==0){
                    editButton.setText("修改");
                    toolbar.setTitle("资产信息修改");
                    nameTv.setFocusableInTouchMode(true);
                    modleTv.setFocusableInTouchMode(true);
                    typeTv.setFocusableInTouchMode(true);
                    locationTv.setFocusableInTouchMode(true);
                    flag1=1;
                    deleteButton.setText("取消");
                    flag2=1;
                    editButton.setText("提交");
                } else if(flag1==1){

                    String[] editParam=new String[6];
                    editParam[0]=assetInfoURL;
                    editParam[1]=asset.getAssetID();
                    editParam[2]=nameTv.getText().toString();
                    editParam[3]=modleTv.getText().toString();
                    editParam[4]=typeTv.getText().toString();
                    editParam[5]=locationTv.getText().toString();
                    new SaveAsset().execute(editParam);

                }
                break;
            case R.id.deleteButton:
                if(flag2==0){   new DeleteAsset().execute(deleteAssetURL,asset.getAssetID());}
                if(flag2==1){
//                    Intent intent=new Intent();
//                    intent.setClass(getApplicationContext(), MainActivity.class);
                    DetailActivity.this.finish();
                }
                break;


        }

    }

    class SaveAsset extends AsyncTask<String[],String,Boolean> {

        @Override
        protected Boolean doInBackground(String[]... args) {
            {
                String[] editParam=args[0];
                // Building Parameters
                List<NameValuePair> params = new ArrayList();
                params.add(new BasicNameValuePair(ID, editParam[1].toString()));
                params.add(new BasicNameValuePair(NAME,editParam[2].toString()));
                params.add(new BasicNameValuePair(MODLE,editParam[3].toString()));
                params.add(new BasicNameValuePair(TYPE, editParam[4].toString()));
                params.add(new BasicNameValuePair(LOCATION, editParam[5].toString()));

                json = jsonParser.makeHttpRequest("POST", editParam[0].toString(), params);

                try {
                    int success = json.getInt(SUCCESS);

                    if (success == 1) {
                        Intent intent=new Intent();
                        intent.setClass(getApplicationContext(), MainActivity.class);
                        setResult(1, intent);
                        DetailActivity.this.finish();
                        return  true;
                    } else {
                        // failed to update product
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }
        }
    }
    class DeleteAsset extends AsyncTask<Object, String,Boolean> {

        @Override
        protected Boolean doInBackground(Object... args) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("assetID", args[1].toString()));
            json = jsonParser.makeHttpRequest("GET",deleteAssetURL,params);
            Log.i("my_info","delete"+ json.toString());
            try {
                if (json.getInt(SUCCESS)== 1) {
                    Intent intent=new Intent();
                    intent.setClass(getApplicationContext(), MainActivity.class);
                    setResult(2,intent);
                    DetailActivity.this.finish();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;

        }

    }

}
