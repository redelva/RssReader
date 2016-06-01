package com.lgq.rssreader.adapter;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ScrollingView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lgq.rssreader.R;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ViewHolderFactory;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.util.ChannelUtil;
import com.lgq.rssreader.util.PreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ChannelRecyclerViewAdapter extends BaseRecyclerViewAdapter<Channel, ChannelRecyclerViewAdapter.ChannelTextViewHolder>{

    private OnScrollToListener onScrollToListener;

    public ChannelRecyclerViewAdapter(Context context,  List<Channel> data, ViewHolderFactory<ChannelTextViewHolder> factory){
        super(context, data, factory, R.layout.channel_text);
    }

    @Override
    public void bindItemViewHolder(ChannelTextViewHolder holder, int position) {
        Channel channel = getData().get(position);

        ParentViewHolder imageViewHolder = (ParentViewHolder) holder;
        imageViewHolder.bindView(channel, position, imageClickListener);

        holder.mTextView.setText(this.getData().get(position).getTitle());
        holder.mTextView.setTag(this.getData().get(position));

        holder.mCountView.setText(String.valueOf(this.getData().get(position).getUnreadCount()));
        holder.mCountView.setTag(this.getData().get(position));

        holder.mIconView.setImageResource(this.getData().get(position).getIsDirectory() ? R.mipmap.folder : R.mipmap.rss);
        holder.mIconView.setTag(this.getData().get(position));

        holder.parent.setTag(this.getData().get(position));
    }

    public void setOnScrollToListener(OnScrollToListener scrollToListener){
        this.onScrollToListener = scrollToListener;
    }

    public void add(Channel channel, int position) {
        getData().add(position, channel);
        notifyItemInserted(position);
    }

    public void resetData(List<Channel> list) {
        getData().clear();
        getData().addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(List<Channel> list, int position) {
        getData().addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }

    protected void removeAll(int position, int itemCount) {
        for (int i = 0; i < itemCount; i++) {
            getData().remove(position);
        }
        notifyItemRangeRemoved(position, itemCount);
    }

    private int getChildrenCount(Channel item) {
        List<Channel> list = new ArrayList<Channel>();
        printChild(item, list);
        return list.size();
    }

    private void printChild(Channel item, List<Channel> list) {
        list.add(item);
        if (item.getChildren() != null) {
            for (int i = 0; i < item.getChildren().size(); i++) {
                printChild(item.getChildren().get(i), list);
            }
        }
    }

    private ChannelExpandClickListener imageClickListener = new ChannelExpandClickListener() {

        @Override
        public void onExpandChildren(Channel channel, int position) {
            //int position = getCurrentPosition(itemData.getUuid());
            List<Channel> children = channel.getChildren();
            if (children == null) {
                return;
            }
            addAll(children, position + 1);// 插入到点击点的下方
            //itemData.setChildren(children);
            if (onScrollToListener != null) {
                onScrollToListener.scrollTo(position + 1);
            }
        }

        @Override
        public void onHideChildren(Channel channel, int position) {
            List<Channel> children = channel.getChildren();
            if (children == null) {
                return;
            }
            removeAll(position + 1, getChildrenCount(channel) - 1);
            if (onScrollToListener != null) {
                onScrollToListener.scrollTo(position);
            }
//            itemData.setChildren(null);
        }
    };

    public static class ChannelTextViewHolder extends RecyclerView.ViewHolder  {
        TextView mTextView;
        TextView mCountView;
        ImageView mIconView;
        View parent;

        public ChannelTextViewHolder(View view) {
            super(view);
            parent = view;
            mTextView = (TextView)view.findViewById(R.id.channel_title);
            mCountView = (TextView)view.findViewById(R.id.channel_count);
            mIconView = (ImageView)view.findViewById(R.id.channel_icon);
        }
    }

    public static class ChannelTextViewHolderFactory implements ViewHolderFactory<ChannelTextViewHolder>{
        @Override
        public ChannelTextViewHolder create(View v) {
            return new ParentViewHolder(v);
        }
    }


    private static int lastOpenPosition = -1;
    private static Channel lastChannel = null;
    private static ImageView lastImageView = null;

    public static class ParentViewHolder extends ChannelTextViewHolder {
        public RelativeLayout relativeLayout;
        private int itemMargin;

        public ParentViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.container);
            itemMargin = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.dip10);
        }

        public void bindView(final Channel channel, final int position, final ChannelExpandClickListener imageClickListener) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIconView.getLayoutParams();
            //boolean isChild = ChannelUtil.isChildren(channel);
            //Log.i("RssReader", channel.getTitle() + "is " + isChild + " and hasparent" + channel.getHasParent());
            if(channel.getHasParent())
                params.leftMargin = itemMargin;
            else
                params.leftMargin = 0;
            mIconView.setLayoutParams(params);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                @Override
                public void onClick(View v) {
                    parent.callOnClick();
                }
            });

            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                @Override
                public boolean onLongClick(View v) {
                    return parent.performLongClick();
                }
            });

            mIconView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (imageClickListener != null && channel.getIsDirectory()) {
                        boolean isExpand = false;
                        if(channel.getTag() != null)
                            isExpand = (boolean)channel.getTag();

                        if(lastOpenPosition == -1){

                            if (isExpand) {
                                imageClickListener.onHideChildren(channel, position);
                                mIconView.setImageResource(R.mipmap.folder);
                                channel.setTag(false);
                                //rotationExpandIcon(45, 0);
                            } else {
                                imageClickListener.onExpandChildren(channel, position);
                                mIconView.setImageResource(R.mipmap.opened_folder);
                                channel.setTag(true);
                                //rotationExpandIcon(0, 45);
                            }

                            lastOpenPosition = position;
                            lastChannel = channel;
                            lastImageView = mIconView;
                        }else{

                            imageClickListener.onHideChildren(lastChannel, lastOpenPosition);
                            lastChannel.setTag(false);
                            lastImageView.setImageResource(R.mipmap.folder);

                            lastOpenPosition = -1;
                            lastChannel = null;
                            lastImageView= null;
                        }
                    }
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void rotationExpandIcon(float from, float to) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
                valueAnimator.setDuration(150);
                valueAnimator.setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        //expand.setRotation((Float) valueAnimator.getAnimatedValue());
                    }
                });
                valueAnimator.start();
            }
        }
    }
}
