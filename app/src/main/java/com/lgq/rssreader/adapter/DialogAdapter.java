package com.lgq.rssreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lgq.rssreader.R;
import com.lgq.rssreader.model.Channel;

/**
 * @author alessandro.balocco
 */
public class DialogAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public DialogAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.simple_grid_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.textView.setText("取消订阅");
                viewHolder.imageView.setImageResource(R.mipmap.ic_action_delete);
                break;
            case 1:
                viewHolder.textView.setText("设置已读");
                viewHolder.imageView.setImageResource(R.mipmap.ic_action_mark);
                break;
            case 2:
                viewHolder.textView.setText("离线下载");
                viewHolder.imageView.setImageResource(R.mipmap.ic_action_download);
                break;
            case 3:
                viewHolder.textView.setText("重命名");
                viewHolder.imageView.setImageResource(R.mipmap.ic_action_rename);
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
