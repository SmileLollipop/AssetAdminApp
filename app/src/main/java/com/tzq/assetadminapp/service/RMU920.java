package com.tzq.assetadminapp.service;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Rachel on 2016/2/6.
 */
public class RMU920 {

    private static final String TAG = "RMU920";
    private static final  int MAX_PACKET_LEN=1024;

    private Context mContext;
    private InputStream tmpIn=null;
    private OutputStream tmpOut=null;


    public RMU920( Context context) {
        mContext= context;
    }


    //读写器数据读取
    public int commRead(byte []datas)
    {
        byte []b = new byte[MAX_PACKET_LEN];
        int len=0;
        try {
            if (tmpIn==null){ tmpIn=BTLink.btSocket.getInputStream();}
            len=tmpIn.read(b);
            if(len>0){
                for(int i=0;i<len;i++)//通过函数参数returndata返回读取的数据
                    datas[i]= b[i];
            }
//            Log.i("my_info", bytesToHexString(datas)+"  ok");
        } catch (IOException e){
            Log.e(TAG, "disconnected", e);
            return 0;
        }
//        finally {
//            try {
//                tmpIn.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return len;
    }

    //读写器数据写入
    public boolean  commwrite(byte[] buffer,int off, int len)
    {
        try {
            if(tmpOut==null){tmpOut=BTLink.btSocket.getOutputStream();}
            Log.i("my_info", "commwrite");
            tmpOut.write(buffer,off,len);
//            //测试用代码
//            String readMessage =bytesToHexString(buffer);
//            Log.i("my_info", "成功写入"+readMessage);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
            Log.i("my_info", "失败写入");
            return false;
        }
        finally {
            try {
                tmpOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //单标签循环识别
    public boolean startModle1(){
        boolean res = false;
        if (!(BTLink.isConnceted)) {
            Toast.makeText(mContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
            return res;
        }else {
            Log.i("my_info", "开始发送指令");
            byte writepk[] = new byte[MAX_PACKET_LEN];
            int pklen = 0;
            // 命令打包
            writepk[0] = (byte) 0xAA;
            writepk[1] = (byte) 0x02;
            writepk[2] = (byte) 0x10;
            writepk[3] = (byte) 0x55;
            // 向读写器发送命令
            pklen = 4;
            res = commwrite(writepk, 0, pklen);
            return res;
        }
    }
    //单标签单步识别
    public boolean startModle2(){
        boolean res = false;
        if (!(BTLink.isConnceted)) {
            Toast.makeText(mContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
            return res;
        }else {
            Log.i("my_info", "开始发送指令");
            byte writepk[] = new byte[MAX_PACKET_LEN];
            int pklen = 0;
            // 命令打包
            writepk[0] = (byte) 0xAA;
            writepk[1] = (byte) 0x02;
            writepk[2] = (byte) 0x18;
            writepk[3] = (byte) 0x55;
            // 向读写器发送命令
            pklen = 4;
            res= commwrite(writepk,0,pklen);
            return res;
        }
    }
    //多标签循环识别（防冲撞识别）
    public boolean startModle3(){
        boolean res = false;
        if (!(BTLink.isConnceted)) {
            Toast.makeText(mContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
            return res;
        }else {
            Log.i("my_info", "开始发送指令");
            byte writepk[] = new byte[MAX_PACKET_LEN];
            int pklen = 0;
            // 命令打包
            writepk[0] = (byte) 0xAA;
            writepk[1] = (byte) 0x03;
            writepk[2] = (byte) 0x11;
            writepk[3] = (byte) 0x03;
            writepk[4] = (byte) 0x55;
            // 向读写器发送命令
            pklen = 5;
            res= commwrite(writepk,0,pklen);
            return res;
        }
    }
    public boolean stop() {// RUM900连续读数据函数停止
        boolean res = false;
        if (!(BTLink.isConnceted)) {
            Toast.makeText(mContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
        }else {
            Log.i("my_info", "停止扫描指令指令");
            byte writepk[] = new byte[MAX_PACKET_LEN];
            int pklen = 0;
            // 命令打包
            writepk[0] = (byte) 0xAA;
            writepk[1] = (byte) 0x02;
            writepk[2] = (byte) 0x12;
            writepk[3] = (byte) 0x55;
            // 向读写器发送命令
            pklen = 4;
            res= commwrite(writepk, 0, pklen);

        }
        return  res;
    }

    //解析标签识别返回命令
    public int rumSingleParse(byte[] returndata){

        boolean res = false;
        int datalen=0;
        int len=0;
        int index=0;
        int totallen=0;
        byte[] datatemp=new byte[MAX_PACKET_LEN];
        byte[] data=new byte[MAX_PACKET_LEN];
        do{

            len= commRead(datatemp);
            totallen=totallen+len;
            for(int i=0;i<len;i++){
                data[index+i]=datatemp[i];
            }
            index=index+len;

        }while(totallen<(data[1]+2));


        if (data[0] == (byte) 0xAA && data[2] == (byte) 0x10
                && data[3] == (byte) 0x00)     {                 // 读写器没有返回错误
            Log.i("my_info", "读写器没有返回错误");


            for (int i = 6; i < ( data[1] + 1); i++){
                // 通过函数参数returndata返回读取的数据
                returndata[i - 6] = data[i];
            }
            datalen = data[1] - 5 ;

        }
        return datalen ;
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString((int)(0xFF & bArray[i]));
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


}
