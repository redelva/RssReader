package com.lgq.rssreader.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;
import com.lgq.rssreader.BlogListActivity;
import com.lgq.rssreader.ContentActivity;
import com.lgq.rssreader.MainActivity;
import com.lgq.rssreader.R;
import com.lgq.rssreader.abstraction.FeedlyParser;
import com.lgq.rssreader.abstraction.RssParser;
import com.lgq.rssreader.adapter.BaseRecyclerViewAdapter;
import com.lgq.rssreader.adapter.ChannelRecyclerViewAdapter;
import com.lgq.rssreader.adapter.DialogAdapter;
import com.lgq.rssreader.adapter.OnScrollToListener;
import com.lgq.rssreader.core.Constant;
import com.lgq.rssreader.core.ReaderApp;
import com.lgq.rssreader.model.Channel;
import com.lgq.rssreader.model.RssAction;
import com.lgq.rssreader.task.ChannelMarkTask;
import com.lgq.rssreader.task.DownloadTask;
import com.lgq.rssreader.util.PreferencesUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by redel on 2015-09-03.
 */
public class ChannelListFragment extends BaseListFragment<Channel> {

    private static String CHANNELS = "channels";
    private ChannelRecyclerViewAdapter mAdapter;
    private List<Channel> mChannels;

    private static ExecutorService FULL_TASK_EXECUTOR;

    static {
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
    }

    public static final ChannelListFragment newInstance()
    {
        ChannelListFragment fragment = new ChannelListFragment();
        fragment.setLayout(R.id.channelList);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mChannels = PreferencesUtil.getChannels();
        mAdapter = new ChannelRecyclerViewAdapter(this.getContext(), mChannels, new ChannelRecyclerViewAdapter.ChannelTextViewHolderFactory());
        mAdapter.setOnScrollToListener(new OnScrollToListener() {

            public void scrollVerticallyToPosition(int position) {
                RecyclerView.LayoutManager lm = getRecyclerView().getLayoutManager();

                if (lm != null && lm instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, 0);
                } else {
                    lm.scrollToPosition(position);
                }
            }

            @Override
            public void scrollTo(int position) {
                scrollVerticallyToPosition(position - 1);
            }
        });
        setLayout(R.id.channelList);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public List<Channel> loadData() {
        return mChannels;
    }

    @Override
    public void onItemClick(View view, Channel data) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BlogListActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("channel", data);
        intent.putExtras(mBundle);
        startActivityForResult(intent, Constant.BLOG_LIST);
    }

    @Override
    public void onItemLongClick(View view, final Channel data) {
//        DialogPlus dialog = DialogPlus.newDialog(getContext())
//                .setAdapter(new DialogAdapter(getContext()))
//                .setContentHolder(new GridHolder(4))
//                .setGravity(Gravity.BOTTOM)
//                .setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//                        //Channel channel = (Channel)item;
//                        switch(position){
//                            case 0:
//                                //unSubscribe
//                                //new FeedlyParser(PreferencesUtil.getAccessToken()).markTag(PreferencesUtil.getProfile().getId(), data, RssAction.UnSubscribe );
//                                new ChannelMarkTask(data, RssAction.UnSubscribe, ChannelListFragment.this.mAdapter).executeOnExecutor(
//                                        FULL_TASK_EXECUTOR,
//                                        PreferencesUtil.getAccessToken(),
//                                        PreferencesUtil.getProfile().getId());
//                                break;
//                            case 1:
//                                //mark all as read
//                                new ChannelMarkTask(data, RssAction.AllAsRead, ChannelListFragment.this.mAdapter).executeOnExecutor(
//                                        FULL_TASK_EXECUTOR,
//                                        PreferencesUtil.getAccessToken(),
//                                        PreferencesUtil.getProfile().getId());
//                                break;
//                            case 2:
//                                //download
//
//                                break;
//                            case 3:
//                                //rename
//                                break;
//                        }
//
//                        Toast.makeText(ChannelListFragment.this.getContext(), "You had click " + position, Toast.LENGTH_SHORT).show();
//
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(true)
//                .setContentHeight(FrameLayout.LayoutParams.WRAP_CONTENT)
//                .setExpanded(true, FrameLayout.LayoutParams.WRAP_CONTENT)  // This will enable the expand feature, (similar to android L share dialog)
//                .create();
//        dialog.show();

//        BottomSheetBehavior behavior = ((MainActivity)getActivity()).getBottomSheetBehavior();
//
//        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        } else {
//            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }

        showBSDialog(data);
    }

    private void showBSDialog(final Channel channel) {
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.channal_action, null);
        dialog.setContentView(contentView);

        View parent = (View) contentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        contentView.measure(0, 0);
        behavior.setPeekHeight(contentView.getMeasuredHeight());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.action_unsubscribe:
                        dialog.dismiss();
                        new ChannelMarkTask(channel, RssAction.UnSubscribe, ChannelListFragment.this.mAdapter).executeOnExecutor(
                                        FULL_TASK_EXECUTOR,
                                        PreferencesUtil.getAccessToken(),
                                        PreferencesUtil.getProfile().getId());
                        break;
                    case R.id.action_mark:
                        dialog.dismiss();
                        new ChannelMarkTask(channel, RssAction.AllAsRead, ChannelListFragment.this.mAdapter).executeOnExecutor(
                                        FULL_TASK_EXECUTOR,
                                        PreferencesUtil.getAccessToken(),
                                        PreferencesUtil.getProfile().getId());
                        break;
                    case R.id.action_download:
                        dialog.dismiss();

                        CheckPermission
                                .from(getContext())
                                //.setPackageName(getActivity().getPackageName())
                                .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
                                .setRationaleConfirmText("Request SYSTEM_ALERT_WINDOW")
                                .setDeniedMsg("The SYSTEM_ALERT_WINDOW Denied")
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void permissionGranted() {
                                        new DownloadTask(getContext(), channel).executeOnExecutor(
                                            FULL_TASK_EXECUTOR,
                                            PreferencesUtil.getAccessToken(),
                                            PreferencesUtil.getProfile().getId());
                                    }

                                    @Override
                                    public void permissionDenied() {
                                        Toast.makeText(getContext(), "SYSTEM_ALERT_WINDOW Permission Denied", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .check();
                        break;
                    case R.id.action_move:
                        if(channel.getIsDirectory()){
                            Toast.makeText(getContext(), getContext().getString(R.string.action_can_not_move) + channel.getTitle(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dialog.dismiss();

                        final ArrayList<String> choices = new ArrayList<String>();
                        List<Channel> channels = PreferencesUtil.getChannels();
                        for(int i= 0; i < channels.size(); i++){
                            Channel t = channels.get(i);
                            if(t != null && t.getIsDirectory()){
                                choices.add(t.getTitle());
                            }
                        }

                        Channel parent = PreferencesUtil.findParentChannel(channel);
                        if(parent != null) {
                            choices.add(getActivity().getResources().getString(R.string.uncategory));
                            //else
                            choices.remove(parent.getTitle());
                        }

                        boolean[] chsBool = new boolean[choices.size()];
                        for(int i = 0; i < chsBool.length; i++){
                            chsBool[i] = false;
                        }
                        DialogInterface.OnMultiChoiceClickListener multiClick = new DialogInterface.OnMultiChoiceClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if(isChecked){
                                    if(choices.get(which).equals(getActivity().getResources().getString(R.string.uncategory))){
                                        channel.setTag(Constant.UNCATEGORY);
                                    }else{
                                        channel.setTag(choices.get(which));
                                    }
                                }
                            }
                        };
                        DialogInterface.OnClickListener onselect = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                channel.setTag(choices.get(which));
                            }
                        };

                        AlertDialog moveDialog = new AlertDialog.Builder(getActivity())
                                .setIcon(R.mipmap.ic_action_move)
                                .setTitle(getActivity().getResources().getString(R.string.action_move))
                                .setMultiChoiceItems((String[]) choices.toArray(new String[0]), chsBool, multiClick)
                                //.setItems((String[]) choices.toArray(new String[0]), onselect)
                                .setPositiveButton(getActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new ChannelMarkTask(channel, RssAction.MoveTag, ChannelListFragment.this.mAdapter).executeOnExecutor(
                                                FULL_TASK_EXECUTOR,
                                                PreferencesUtil.getAccessToken(),
                                                PreferencesUtil.getProfile().getId());

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(getActivity().getResources().getString(R.string.cancel),  null).create();
                        moveDialog.show();
                        break;
                    case R.id.action_rename:
                        dialog.dismiss();

                        final EditText input = new EditText(getActivity());
                        input.setId(0);
                        AlertDialog newDialog = new AlertDialog.Builder(getActivity())
                                .setIcon(R.mipmap.ic_action_rename)
                                .setView(input)
                                .setTitle(getActivity().getResources().getString(R.string.action_rename))
                                .setPositiveButton(getActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String value = input.getText().toString();

                                        channel.setTag(value);

                                        new ChannelMarkTask(channel, RssAction.Rename, ChannelListFragment.this.mAdapter).executeOnExecutor(
                                                FULL_TASK_EXECUTOR,
                                                PreferencesUtil.getAccessToken(),
                                                PreferencesUtil.getProfile().getId());

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(getActivity().getResources().getString(R.string.cancel),  null).create();

                        newDialog.show();

                        break;
                }
            }
        };

        for (int index = 0; index < ((ViewGroup)contentView).getChildCount(); index++) {
            View view = ((ViewGroup)contentView).getChildAt(index);
            if(view instanceof ViewGroup){
                for (int j = 0; j < ((ViewGroup)view).getChildCount(); j++) {
                    View v = ((ViewGroup) view).getChildAt(j);
                    v.setOnClickListener(listener);
                }
            }
        }

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == Constant.WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(getContext(), "Contact permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission Denied
                Toast.makeText(getContext(), "Contact permission is not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        this.getSwipeRefreshLayout().setRefreshing(true);

        new ChannelTask(this.getSwipeRefreshLayout()).execute(getToken());
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public BaseRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    class ChannelTask extends AsyncTask<String, Void, List<Channel>>{
        private SwipeRefreshLayout mSwipeRefreshLayout;

        public ChannelTask(SwipeRefreshLayout mSwipeRefreshLayout){
            this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        }

        protected List<Channel> doInBackground(String... urls) {

            RssParser parser = new FeedlyParser(urls[0]);
            try {
                List<Channel> channels = parser.loadData();

                PreferencesUtil.saveChannels(channels);

                return channels;
            }catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(List<Channel> channels) {
            if(channels != null){
                mChannels.clear();
                mChannels.addAll(channels);
                mAdapter.notifyDataSetChanged();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}