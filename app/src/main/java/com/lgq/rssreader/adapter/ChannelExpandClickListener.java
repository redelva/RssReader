package com.lgq.rssreader.adapter;

import com.lgq.rssreader.model.Channel;

/**
 * Created by redel on 2015-11-21.
 */
public interface ChannelExpandClickListener {
    void onExpandChildren(Channel channel, int position);

    void onHideChildren(Channel channel, int position);
}
