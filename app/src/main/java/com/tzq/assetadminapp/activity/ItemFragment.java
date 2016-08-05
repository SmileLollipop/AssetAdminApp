package com.tzq.assetadminapp.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tzq.assetadminapp.R;
import com.tzq.assetadminapp.adapter.MyItemRecyclerViewAdapter;
import com.tzq.assetadminapp.bean.Asset;
import com.tzq.assetadminapp.service.JSONParse;
import com.tzq.assetadminapp.service.LoadAssetList;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private JSONParse jsonParser = new JSONParse();
    private static JSONObject json;
    private List<Asset> assetList = new ArrayList<>();
    private MyItemRecyclerViewAdapter mAapter;
    private OnListFragmentInteractionListener mListener;

    public ItemFragment() {

    }


    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_item_list,container,false);
        Context context = view.getContext();
        recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        new LoadAssetTask().execute();
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            Log.i("my_info","mListener..");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("my_info",context.toString());
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//            Log.i("my_info","mListener..");
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public class LoadAssetTask extends AsyncTask<String,String,List<Asset>> {

        @Override
        protected List<Asset> doInBackground(String... args) {
            assetList=new LoadAssetList().newInstance();
            return assetList;
        }

        @Override
        protected void onPostExecute(List<Asset> assetList) {
            super.onPostExecute(assetList);
            if(assetList.size()>0) {
                mAapter = new MyItemRecyclerViewAdapter(assetList,mListener);
                recyclerView.setAdapter(mAapter);
                recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
            }

        }
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Asset item);
    }
}
