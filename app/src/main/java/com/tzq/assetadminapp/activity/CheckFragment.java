package com.tzq.assetadminapp.activity;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tzq.assetadminapp.R;
import com.tzq.assetadminapp.adapter.CheckRecyclerViewAdapter;
import com.tzq.assetadminapp.adapter.MyItemRecyclerViewAdapter;
import com.tzq.assetadminapp.bean.Asset;
import com.tzq.assetadminapp.service.LoadAssetList;
import com.tzq.assetadminapp.service.RMU920;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class CheckFragment extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;

    private List<Asset> assetList;
    private CheckRecyclerViewAdapter checkAdapter;

    private RMU920 readerRMU;
    private boolean isRunning = false;//决定能不能定时读卡
    private Timer mTimer = new Timer();//定时器相关
    private TimerTask mTimerTask;
    private static  String  readMessage=null;
    private static final  int MAX_PACKET_LEN=1024;
    public static final int MESSAGE_READ = 1;
    private int flag=0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readerRMU=new RMU920(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_check, container, false);
        Context context = view.getContext();
        recyclerView = (RecyclerView)view.findViewById(R.id.check_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        new LoadCheckAsstTask().execute();
        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1);
        fab1.setVisibility(View.VISIBLE);
        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab1:
               if(flag==0){
                   isRunning = readerRMU.startModle3();
                   if (isRunning) {
                       flag=1;
                       fab1.setImageResource(android.R.drawable.ic_media_pause);
                       mTimerTask = new TimerTask() {
                           public void run() {
                               if (isRunning) {
                                   byte[] returndatas = new byte[MAX_PACKET_LEN];
                                   int len = 0;
                                   len = readerRMU.rumSingleParse(returndatas);
                                   mHandler.obtainMessage(MESSAGE_READ, len, -1, returndatas).sendToTarget();//读取到数据，发送接收数据消息

                               }
                           }
                       };
                       mTimer.schedule(mTimerTask, 100, 50);
                   }

               }else if(flag==1){
                   boolean res=readerRMU.stop();
                   if(res){
                       flag=0;
                       fab1.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);

                   }
               }
                break;
            case R.id.fab2:
                Intent intent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("list", (Serializable) assetList);
                intent.putExtras(bundle);
                intent.setClass(getActivity().getApplicationContext(), ReportActivity.class);
                startActivityForResult(intent,4);
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
                Log.i("my_info", readMessage );
                for(int i=0;i<assetList.size();i++){
                    if (readMessage.equals(assetList.get(i).getEpc())){
                        assetList.get(i).setStatus(1);
                        CheckRecyclerViewAdapter.getIsSelected().put(i,true);
                        checkAdapter.notifyItemChanged(i);
                    }
                }

            }

        };

    public class LoadCheckAsstTask extends AsyncTask<String,String,List<Asset>> {

        @Override
        protected List<Asset> doInBackground(String... args) {
            assetList=new LoadAssetList().newInstance();
            return assetList;
        }

        @Override
        protected void onPostExecute(List<Asset> assetList) {
            super.onPostExecute(assetList);
            if(assetList.size()>0) {
                checkAdapter= new CheckRecyclerViewAdapter(assetList);
                recyclerView.setAdapter(checkAdapter);
                recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
            }

        }
    }


}
