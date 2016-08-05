package com.tzq.assetadminapp.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tzq.assetadminapp.R;

import com.tzq.assetadminapp.bean.Asset;

import com.tzq.assetadminapp.service.JSONParse;
import com.tzq.assetadminapp.service.LoadAssetList;
import com.tzq.assetadminapp.service.RMU920;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class ScanFragment extends Fragment implements View.OnClickListener{

    private Button scanButt;
    private Button stopButt;
    private TextView resultTv;
    private Button operateButt;
    private int buttonFlag;
    private RMU920 readerRMU;
    private boolean isRunning = false;//决定能不能定时读卡
    private Timer mTimer = new Timer();//定时器相关
    private TimerTask mTimerTask;
    private static final  int MAX_PACKET_LEN=1024;
    public static final int MESSAGE_READ = 1;
    private Button cancelButt;

    private List<Asset> assetList = new ArrayList<>();
    private Context context;
    private static  String  readMessage=null;
    private static  String  readMsg=null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String ASSET = "asset";
    private static final String ID = "assetID";
    private static final String NAME = "assetName";
    private static final String MODLE = "assetModle";
    private static final String TYPE = "assetType";
    private static final String EPC = "epc";
    private static final String LOCATION = "location";
    private static final String TIME= "createTime";
    private static final String STATUS = "status";

    private static JSONObject json;
    private JSONParse jsonParser = new JSONParse();
    private static String findAssetByEpcURL = "http://192.168.1.103/assetAdmin/findAssetByEpc.php";
    private Asset asset=new Asset();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readerRMU=new RMU920(getActivity());
        context=getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_scan, container,false);

        scanButt=(Button)view.findViewById(R.id.scan);
        stopButt=(Button)view.findViewById(R.id.stop);
        resultTv=(TextView)view.findViewById(R.id.result);
        operateButt=(Button)view.findViewById(R.id.operate);
        cancelButt=(Button)view.findViewById(R.id.cancel);
        scanButt.setOnClickListener(this);
        stopButt.setOnClickListener(this);
        operateButt.setOnClickListener(this);
        cancelButt.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan:
                isRunning=readerRMU.startModle1();
                if (isRunning) {  Toast.makeText(context, "正在读卡", Toast.LENGTH_LONG).show();}

                 mTimerTask = new TimerTask() {
                    public void run() {
                        if (isRunning) {
                            byte []returndatas = new byte[MAX_PACKET_LEN];
                            int len=0;
                            len= readerRMU.rumSingleParse(returndatas);
                            mHandler.obtainMessage(MESSAGE_READ, len, -1,returndatas).sendToTarget();//读取到数据，发送接收数据消息
                            returndatas=null;
                        }
                     }
                 };
                  mTimer.schedule(mTimerTask, 100, 50);
                 break;
            case R.id.stop:
                boolean res=readerRMU.stop();
                if(res){
                    readMessage=null;
                    Toast.makeText(context, "已停止读卡", Toast.LENGTH_LONG).show();
                    resultTv.setText("");
                    operateButt.setVisibility(View.INVISIBLE);
                    cancelButt.setVisibility(View.INVISIBLE);

                }
                break;
            case R.id.operate:
                try {
                    if(buttonFlag==1){
                        asset=new FindAssetByEPC().execute(readMsg).get();
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("asset",asset);
                        intent.putExtras(bundle);
                        intent.setClass(context,DetailActivity.class);
                        startActivity(intent);
                    }
                    if(buttonFlag==2){
                        Intent intent=new Intent();
                        intent.putExtra("epc",readMsg);
                        intent.setClass(context, AddAssetActivity.class);
                        startActivityForResult(intent, 0);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.cancel:
                resultTv.setText("");
                operateButt.setVisibility(View.INVISIBLE);
                cancelButt.setVisibility(View.INVISIBLE);
                break;

        }

    }
    //接收到数据后的处理
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int len=msg.arg1;
            byte[] readBuf =new byte[len];
            byte[] readBuf1 =(byte[]) msg.obj;
            for(int i=0;i<len;i++)
            {
                readBuf[i] = readBuf1[i];
            }
            readMessage =readerRMU.bytesToHexString(readBuf);
            boolean res=false;
            try {
                res= new ScanAssetTask().execute(readMessage).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(res){
                resultTv.setText("已扫描到RFID标签:" + readMessage + " \n " + "该标签对应的资产已入库");
                operateButt.setVisibility(View.VISIBLE);
                operateButt.setText("查看详情");
                buttonFlag=1;
                cancelButt.setVisibility(View.VISIBLE);
                readMsg=readMessage;
                readMessage=null;

            } else if(!readMessage.isEmpty()){
                resultTv.setText("已扫描到RFID标签:" + readMessage + " \n " + "该标签没有对应的资产已入库");
                operateButt.setVisibility(View.VISIBLE);
                operateButt.setText("创建");
                buttonFlag=2;
                cancelButt.setVisibility(View.VISIBLE);
                readMsg=readMessage;
                readMessage=null;
            }
        }

    };
    public class ScanAssetTask extends AsyncTask<String,String,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean fag=false;
            assetList=new LoadAssetList().newInstance();
            for(int i=0;i<assetList.size();i++){
                Log.i("my_info",assetList.get(i).getEpc());
                if(params[0].equals(assetList.get(i).getEpc())){
                    fag=true;
                    break;
                }else {
                    fag=false;
                }
            }
            return  fag;
        }
    }
    public class FindAssetByEPC extends AsyncTask<String,String,Asset>{
        @Override
        protected Asset doInBackground(String... params) {
            List<NameValuePair> param= new ArrayList<>();
            param.add(new BasicNameValuePair("epc",params[0]));
            json = jsonParser.makeHttpRequest("GET",findAssetByEpcURL,param);
            Log.i("my_info",json.toString());
            try {
                if (json.getInt(SUCCESS)== 1) {
                    Log.i("my_info",SUCCESS);
                  // JSONArray assetObj = json.getJSONArray(ASSET);
                    JSONArray assets = json.getJSONArray(ASSET);
                    for (int i = 0; i < assets.length();i++) {
                        JSONObject obj = assets.getJSONObject(i);
                        asset.setAssetID(obj.getString(ID));
                        asset.setAssetName(obj.getString(NAME));
                        asset.setAssetModle(obj.getString(MODLE));
                        asset.setAssetType(obj.getString(TYPE));
                        asset.setEpc(obj.getString(EPC));
                        asset.setLocation(obj.getString(LOCATION));
                        asset.setCreateTime(obj.getString(TIME));
                        return  asset;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if (0 == requestCode) {
            if (0 == resultCode) {
                resultTv.setText("");
                operateButt.setVisibility(View.INVISIBLE);
                cancelButt.setVisibility(View.INVISIBLE);
            }
        }

    }


}
