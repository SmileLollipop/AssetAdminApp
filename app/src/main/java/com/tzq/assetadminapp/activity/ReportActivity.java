package com.tzq.assetadminapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.tzq.assetadminapp.R;

import com.tzq.assetadminapp.adapter.MyItemRecyclerViewAdapter;
import com.tzq.assetadminapp.bean.Asset;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;



import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    private PieChart mChart;
    private RecyclerView recyclerView;
    private List<Asset> assetList = new ArrayList<>();
    private List<Asset> uncheckedList = new ArrayList<>();
    private MyItemRecyclerViewAdapter mAapter;
    private  int cheakedCount=0;
    private int totalCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("固定资产盘点报告");
        setSupportActionBar(toolbar);


        assetList=(List<Asset>)(getIntent().getSerializableExtra("list"));

        totalCount=assetList.size();
        for(int i=0;i<totalCount;i++){
            if(assetList.get(i).getStatus()==1){
                cheakedCount++;
            }else if(assetList.get(i).getStatus()==0){
                uncheckedList.add(assetList.get(i));
            }

        }
        Log.i("my_info", uncheckedList.size()+"");

       //饼状图
        mChart = (PieChart) findViewById(R.id.pie_chart);
        PieData mPieData = getPieData(2,totalCount);
        showChart(mChart, mPieData);
       //未盘点到的资产列表
        recyclerView = (RecyclerView)findViewById(R.id.reportlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(uncheckedList.size()>0){
            mAapter = new MyItemRecyclerViewAdapter(uncheckedList);
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
            recyclerView.setAdapter(mAapter);

        }


    }
    private void showChart(PieChart pieChart, PieData pieData) {

        pieChart.setHoleRadius(60f);  //半径
        pieChart.setHoleRadius(0);//实心圆
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setUsePercentValues(true);  //显示成百分比
        pieChart.setDescription("");

        //设置数据
        pieChart.setData(pieData);
        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);  //最右边显示

        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

        pieChart.animateXY(1000, 1000);  //设置动画

    }

    /**
     *
     * @param count 分成几部分
     * @param range
     */
    private PieData getPieData(int count, float range) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        xValues.add("未盘点到的资产");
        xValues.add("已盘点到的资产");


        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据

        float quarterly1 =totalCount-cheakedCount;
        float quarterly2 =cheakedCount;

        Log.i("my_info", totalCount+"  "+cheakedCount);

        yValues.add(new Entry(quarterly1, 0));
        yValues.add(new Entry(quarterly2, 1));


        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues,"资产盘点数据(%)"/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色

        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));

//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));

        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);

        return pieData;
    }
      }