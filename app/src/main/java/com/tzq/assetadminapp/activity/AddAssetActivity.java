package com.tzq.assetadminapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.tzq.assetadminapp.service.LoadAssetList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddAssetActivity extends AppCompatActivity implements View.OnClickListener{
    private Button addButt;
    private Button cancelButt;

    private static String addAssetURL = "http://192.168.1.103/assetAdmin/addAsset.php";
    private JSONParse jsonParser = new JSONParse();
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String ID = "assetID";
    private static final String NAME = "assetName";
    private static final String MODLE = "assetModle";
    private static final String TYPE = "assetTypeID";
    private static final String EPC = "epc";
    private static final String LOCATION = "location";
    private static final String TIME= "createTime";
    private static final String STATUS = "status";
    private String inputName;
    private String inputModle;
    private String inputType;
    private String inputEpc;
    private String inputLocation;
    private String inputTime;
    private JSONObject json;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("资产创建");
        setSupportActionBar(toolbar);
        context=this;
        addButt=(Button)findViewById(R.id.addButt);
        cancelButt=(Button)findViewById(R.id.cancelButt);
        addButt.setOnClickListener(this);
        cancelButt.setOnClickListener(this);
        inputEpc=getIntent().getExtras().get("epc").toString();
        Log.i("my_info", "输入" + inputEpc);
        ((TextView) findViewById(R.id.add_epc)).setText(inputEpc);
        SimpleDateFormat  sDateFormat =   new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        inputTime =  sDateFormat.format(new java.util.Date());
        ((TextView) findViewById(R.id.add_time)).setText(inputTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addButt:
                inputName=((EditText) findViewById(R.id.add_name)).getText().toString();
                inputModle=((EditText) findViewById(R.id.add_model)).getText().toString();
                inputType=((EditText) findViewById(R.id.add_type)).getText().toString();
                inputLocation=((EditText) findViewById(R.id.add_location)).getText().toString();
                try {
                    boolean res=new AddAssetTask().execute(addAssetURL).get();
                    if(res){
                        Toast.makeText(context, "资产成功创建", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent();
                        intent.setClass(this,MainActivity.class);
                         setResult(0,intent);
                        AddAssetActivity.this.finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.cancelButt:
                Intent intent=new Intent();
                intent.setClass(this, MainActivity.class);
                AddAssetActivity.this.finish();
                break;
        }
    }
    public class AddAssetTask extends AsyncTask<String,String,Boolean> {
        @Override
        protected Boolean doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(NAME,inputName));
            params.add(new BasicNameValuePair(MODLE,inputModle));
            params.add(new BasicNameValuePair(TYPE,inputType));
            params.add(new BasicNameValuePair(EPC,inputEpc));
            params.add(new BasicNameValuePair(LOCATION,inputLocation));
            params.add(new BasicNameValuePair(TIME, inputTime));
             json = jsonParser.makeHttpRequest("POST",args[0],params);
            Log.i("my_info",json.toString());
            try {
                int success = json.getInt(SUCCESS);

                if (success == 1) {

                   return true;
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }
    }



}
