package com.tocet.championlam.mylibs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tocet.championlam.mylibs.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ChampionLam on 2015/9/17.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {


    private MODEL mCurrModel = MODEL.NORMAL;    //当前模式
    private REFRESH_STATUS mCurrRefreshStatus = REFRESH_STATUS.DOWN_REFRESH;
    private View mHeaderView;   //当前头布局
    private View mFooterView;   //当前脚布局
    private Context mContext;
    private LayoutInflater mInflater;
    private int headerViewHeight;   //头布局高度
    private int footerViewHeight;   //脚布局高度
    private headerViewHolder mHeaderViewHolder; //头布局控件集
    private OnRefreshListener mOnRefreshListener;   //下拉刷新回调接口
    private OnLoadingMoreListener mOnLoadingMoreListener; //加载更多回调接口


    private RotateAnimation upAnimation;    // 箭头向上旋转的动画
    private RotateAnimation downAnimation;    // 箭头向下旋转的动画
    private int mFirstVisibleItem;  //首个滚动可视Item
    private int startY;             //按下手指触发点Y轴坐标
    private boolean IsLoadingMore = false;  //是否正在加载更多


    /**
     * 头布局控件集
     */
    private class headerViewHolder {

        private ImageView iv_HeaderArrow;
        private ProgressBar pb_Header;
        private TextView tv_HeaderState;
        private TextView tv_HeaderLastUpdateTime;

    }


    /**
     * 初始化头布局控件
     *
     * @return
     */
    private headerViewHolder getHeaderViewHolder() {
        if (null == mHeaderView)
            return null;
        headerViewHolder vh = new headerViewHolder();
        vh.iv_HeaderArrow = (ImageView) mHeaderView.findViewById(R.id.iv_listview_header_arrow);
        vh.pb_Header = (ProgressBar) mHeaderView.findViewById(R.id.pb_listview_header);
        vh.tv_HeaderState = (TextView) mHeaderView.findViewById(R.id.tv_listview_header_state);
        vh.tv_HeaderLastUpdateTime = (TextView) mHeaderView.findViewById(R.id.tv_listview_header_last_update_time);
        vh.tv_HeaderLastUpdateTime.setText(getLastUpdateTime());
        return vh;
    }

    /**
     * 刷新回调接口
     */
    public interface OnRefreshListener {
        void OnRefreshing();
    }

    /**
     * 设置下拉刷新回调事件
     *
     * @param refreshListener
     */
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.mOnRefreshListener = refreshListener;
    }

    public interface OnLoadingMoreListener {
        void OnLoadingMore();
    }

    public void setOnLoadingMoreListener(OnLoadingMoreListener loadingMoreListener) {
        this.mOnLoadingMoreListener = loadingMoreListener;
    }


    /**
     * 模式枚举
     * NORMAL       ->普通ListView
     * LOADINGMORE  ->加载更多
     * REFRESHING   ->下拉刷新
     * BOTO         ->加载更多和下拉刷新都有
     */
    public enum MODEL {
        NORMAL,
        LOADINGMORE,
        REFRESH,
        BOTH
    }

    /**
     * 下拉刷新状态
     * DOWN_REFRESH     ->下拉刷新初始状态
     * REFRESHING       ->刷新中
     * RELEASE_REFRESH  ->释放刷新
     */
    private enum REFRESH_STATUS {
        DOWN_REFRESH,
        REFRESHING,
        RELEASE_REFRESH
    }

    /**
     * 构造
     *
     * @param context
     * @param attrs
     */
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }


    /**
     * 设置模式
     *
     * @param model MODEL.NORMAL         -> 默认模式,不采用下拉刷新和加载更多的功能
     *              MODEL.LOADINGMORE    -> 下拉刷新默认,采用下拉刷新功能
     *              MODEL.REFRESH        -> 加载更多模式,采用加载更多功能
     *              MODEL.BOTH           -> 全开模式,采用加载更多和下拉刷新功能
     */
    public void setModel(MODEL model) {
        mCurrModel = model;
        reset();
        switch (model) {
            case LOADINGMORE:
                initFooterView();
                break;
            case REFRESH:
                initHeaderView();
                break;
            case BOTH:
                initHeaderView();
                initFooterView();
                break;
        }
    }

    /**
     * 重设状态,清除头布局和脚布局
     */
    private void reset() {
        removeFooterView();
        removeHeaderView();
    }

    /**
     * 初始化头布局完成下拉刷新
     */
    private void initHeaderView() {
        mHeaderView = mInflater.inflate(R.layout.refreshlist_header, null);
        mHeaderView.measure(0, 0);
        headerViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -headerViewHeight, 0, 0);
        initArrow();
        mHeaderViewHolder = getHeaderViewHolder();
        this.addHeaderView(mHeaderView);
        this.setOnScrollListener(this);
    }

    /**
     * 初始化脚布局完成加载更多
     */
    private void initFooterView() {
        mFooterView = mInflater.inflate(R.layout.refreshlist_footer, null);
        mFooterView.measure(0, 0);
        footerViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -footerViewHeight, 0, 0);
        this.addFooterView(mFooterView);
        this.setOnScrollListener(this);
    }

    private String getLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 初始化头布局箭头动画
     */
    private void initArrow() {
        upAnimation = new RotateAnimation(
                0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        upAnimation.setDuration(500);
        upAnimation.setFillAfter(true);    // 让控件停留在动画结束的状态下

        downAnimation = new RotateAnimation(
                -180, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        downAnimation.setDuration(500);
        downAnimation.setFillAfter(true);    // 让控件停留在动画结束的状态下
    }

    /**
     * 删除头布局
     */
    public void removeHeaderView() {
        if (null != mHeaderView) {
            this.removeHeaderView(mHeaderView);
            mHeaderView = null;
        }
    }

    /**
     * 删除脚布局
     */
    public void removeFooterView() {
        if (null != mFooterView) {
            this.removeFooterView(mFooterView);
            mFooterView = null;
        }
    }

    /**
     * 隐藏头布局
     */
    public void hideHeaderView() {
        mHeaderView.setPadding(0, -headerViewHeight, 0, 0);
        mCurrRefreshStatus = REFRESH_STATUS.DOWN_REFRESH;
        changeHeaderViewStatus();
        mHeaderViewHolder.tv_HeaderLastUpdateTime.setText(getLastUpdateTime());
    }

    public void hideFooterView() {
        mFooterView.setPadding(0, -footerViewHeight, 0, 0);
        IsLoadingMore = false;
    }

    /**
     * 改变头布局状态处理事件
     */
    private void changeHeaderViewStatus() {
        switch (mCurrRefreshStatus) {
            case DOWN_REFRESH:
                mHeaderViewHolder.tv_HeaderState.setText(R.string.refresh_text_down_refresh);
                mHeaderViewHolder.iv_HeaderArrow.setVisibility(VISIBLE);
                mHeaderViewHolder.pb_Header.setVisibility(INVISIBLE);
                mHeaderViewHolder.iv_HeaderArrow.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                mHeaderViewHolder.tv_HeaderState.setText(R.string.refresh_text_release_fresh);
                mHeaderViewHolder.iv_HeaderArrow.startAnimation(upAnimation);
                break;
            case REFRESHING:
                mHeaderViewHolder.tv_HeaderState.setText(R.string.refresh_text_refreshing);
                mHeaderViewHolder.iv_HeaderArrow.setVisibility(View.INVISIBLE);
                mHeaderViewHolder.iv_HeaderArrow.clearAnimation();
                mHeaderViewHolder.pb_Header.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 重写触摸事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        super.onTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下时记录当前Y轴坐标
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //当下拉刷新功能启用时调用
                if (mCurrModel == MODEL.REFRESH || mCurrModel == MODEL.BOTH) {
                    int moveY = (int) ev.getY();
                    //偏移量
                    int diff = moveY - startY;
                    //必须是在头部并且偏移量大于0时,才可以拉下头布局
                    if (mFirstVisibleItem == 0 && diff > 0) {
                        int top = -headerViewHeight + diff;
                        if (mCurrRefreshStatus == REFRESH_STATUS.REFRESHING)
                            break;
                        //当拉伸偏移量大于头布局高度,进入释放刷新状态
                        if (top > 0 && mCurrRefreshStatus == REFRESH_STATUS.DOWN_REFRESH) {
                            mCurrRefreshStatus = REFRESH_STATUS.RELEASE_REFRESH;
                            changeHeaderViewStatus();
                        } else if (top < 0 && mCurrRefreshStatus == REFRESH_STATUS.RELEASE_REFRESH) {
                            mCurrRefreshStatus = REFRESH_STATUS.DOWN_REFRESH;
                            changeHeaderViewStatus();
                        }
                        //展现头布局
                        mHeaderView.setPadding(0, top, 0, 0);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //松开手指触发
                if (mCurrModel == MODEL.REFRESH || mCurrModel == MODEL.BOTH) {
                    if (mCurrRefreshStatus == REFRESH_STATUS.DOWN_REFRESH) {
                        //当在下拉刷新状态松开手指,不做事件操作,将头布局隐藏
                        mHeaderView.setPadding(0, -headerViewHeight, 0, 0);
                    } else if (mCurrRefreshStatus == REFRESH_STATUS.RELEASE_REFRESH) {
                        //当在释放刷新状态松开手指,进入刷新状态.调用接口事件
                        mHeaderView.setPadding(0, 0, 0, 0);
                        mCurrRefreshStatus = REFRESH_STATUS.REFRESHING;
                        changeHeaderViewStatus();
                        if (null != mOnRefreshListener)
                            mOnRefreshListener.OnRefreshing();
                    }
                }
                break;
        }

        return true;
//        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mCurrModel == MODEL.LOADINGMORE || mCurrModel == MODEL.BOTH) {
            if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_IDLE) {
                int lastItemPosition = getLastVisiblePosition();
                if (lastItemPosition == (getCount() - 1) && !IsLoadingMore) {
                    IsLoadingMore = true;
                    mFooterView.setPadding(0, 0, 0, 0);
                    this.setSelection(getCount() - 1);
                    if (null != mOnLoadingMoreListener) {
                        mOnLoadingMoreListener.OnLoadingMore();
                    }
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

}
