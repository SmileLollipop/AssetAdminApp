package com.tzq.assetadminapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tzq.assetadminapp.R;
import com.tzq.assetadminapp.activity.ItemFragment;
import com.tzq.assetadminapp.bean.Asset;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Rachel on 2016/3/29.
 */
public class CheckRecyclerViewAdapter extends RecyclerView.Adapter<CheckRecyclerViewAdapter.ViewHolder>  {
    private final List<Asset> mValues;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer,Boolean> isSelected;


    public CheckRecyclerViewAdapter(List<Asset> items) {
        mValues = items;
        isSelected = new HashMap<>();
        for(int i=0; i<mValues.size();i++) {
            getIsSelected().put(i,false);
        }

    }

    public static HashMap<Integer, Boolean> getIsSelected() {

        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        CheckRecyclerViewAdapter.isSelected = isSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_check_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getAssetName());
        holder.mModleView.setText(mValues.get(position).getAssetModle());
        holder.mLocationView.setText(mValues.get(position).getLocation());
        holder.mCheckBox.setChecked(getIsSelected().get(position));

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
     //   public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mModleView;
        public final TextView mLocationView;
        public final CheckBox mCheckBox;
        public Asset mItem;

        public ViewHolder(View view) {
            super(view);
          //  mIdView = (TextView) view.findViewById(R.id.check_id);
            mNameView = (TextView) view.findViewById(R.id.check_name);
            mModleView = (TextView) view.findViewById(R.id.check_modle);
            mLocationView= (TextView) view.findViewById(R.id.check_location);
            mCheckBox=(CheckBox)view.findViewById(R.id.checkBox);
        }

    }
}
