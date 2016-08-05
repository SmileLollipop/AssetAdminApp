package com.tzq.assetadminapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tzq.assetadminapp.R;
import com.tzq.assetadminapp.bean.Asset;
import com.tzq.assetadminapp.service.BTLink;

public class MainActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener {
    private FragmentManager fm;
    private Fragment fragment;
    private Context mainContext;
    private BTLink BT=null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("固定资产管理");
        setSupportActionBar(toolbar);
        if(fragment==null){
            fragment=new ItemFragment();
            fm= getFragmentManager();
            fm.beginTransaction()
              .add(R.id.fragment, fragment)
              .addToBackStack(null)
              .commit();
        }
        mainContext=getApplicationContext();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_info:
                fragment=new ItemFragment();
                fm= getFragmentManager();
                fm.beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
                toolbar.setTitle("固定资产管理");
                break;
            case R.id.toolbar_btlink:
                startActivity(new
                        Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                if(BT.btSocket==null){
                    BT= new BTLink(mainContext);
                }
                break;
            case R.id.toolbar_scan:
                fragment=new ScanFragment();
                fm= getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
                toolbar.setTitle("自动资产操作");
                if (!(BTLink.isConnceted)) {
                    Toast.makeText(mainContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.toolbar_check:
                fragment=new CheckFragment();
                fm= getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment,fragment)
                        .addToBackStack(null)
                        .commit();
                toolbar.setTitle("自动资产盘点");
                if (!(BTLink.isConnceted)) {
                    Toast.makeText(mainContext, "暂还没有连接蓝牙射频模块，请核实", Toast.LENGTH_LONG).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Asset item) {
        Log.i("my_info", " onListFragmentInteraction");
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("asset",item);
        intent.putExtras(bundle);
        intent.setClass(this, DetailActivity.class);
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        toolbar.setTitle("固定资产管理");
        if (1== requestCode) {
            if (1== resultCode) {
                Toast.makeText(mainContext, "修改资产成功", Toast.LENGTH_LONG).show();


            } else if (2== resultCode) {
                Toast.makeText(mainContext, "报废资产成功", Toast.LENGTH_LONG).show();

            }
            fragment=new ItemFragment();
            fm= getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();

        }

    }

}
