package com.lgq.rssreader.util;

import com.lgq.rssreader.model.Channel;

import java.util.List;

/**
 * Created by redel on 2015-11-22.
 */
public class ChannelUtil {
    public static boolean isChildren(Channel channel){
        if(channel.getIsDirectory())
            return false;

        List<Channel> channels = PreferencesUtil.getChannels();

        boolean isChild = false;
        for (Channel c : channels) {
            if(c.getIsDirectory() && c.getChannelId().equals(channel.getTagId())){
                for (Channel child : c.getChildren()) {
                    if(child.getChannelId().equals(child.getChannelId())){
                        isChild = true;
                    }
                }
            }
        }

        return isChild;
    }

    public static void updateCount(Channel channel, int count){
        if(channel == null || count < 0)
            return;

        List<Channel> channels = PreferencesUtil.getChannels();

        Channel parent;
        Channel current;
        if(channel.getIsDirectory()){
            for (Channel c : channels) {
                if(c.getIsDirectory() && c.getChannelId().equals(channel.getTagId())){
                    int cnt = c.getUnreadCount() - count;

                    if(cnt < 0) cnt = 0;

                    c.setUnreadCount(cnt);
                }
            }
        }else{
            for (Channel c : channels) {
                if(channel.getTagId() == null){
                    if(c.getChannelId().equals(channel.getChannelId())){
                        int cnt = c.getUnreadCount() - count;
                        if(cnt < 0) cnt = 0;
                        c.setUnreadCount(cnt);
                    }
                }
                else{
                    if(c.getChannelId().equals(channel.getTagId())){
                        for (Channel child : c.getChildren()) {
                            if(child.getChannelId().equals(child.getChannelId())){
                                int cnt = c.getUnreadCount() - count;
                                if(cnt < 0) cnt = 0;
                                c.setUnreadCount(cnt);

                                cnt = child.getUnreadCount() - count;
                                if(cnt < 0) cnt = 0;
                                child.setUnreadCount(cnt);
                            }
                        }
                    }
                }
            }
        }

        PreferencesUtil.saveChannels(channels);

    }
}
