package com.tocet.championlam.mylibs.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by 成彬 on 2015/9/24.
 */
public abstract class SmartAdapter<T> extends BaseAdapter {

    private Context mContext;
    private int mLayoutRes;
    private List<T> mListData;


    public SmartAdapter(Context context, List<T> listData, int layoutRes) {
        this.mLayoutRes = layoutRes;
        this.mContext = context;
        this.mListData = listData;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public T getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = ViewHolder.get(mContext, convertView, parent, position, mLayoutRes);

        setView(vh, getItem(position));

        return vh.getConvertView();
    }

    protected abstract void setView(ViewHolder viewHolder, T item);
}
