package com.andbase.library.view.recycler;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2017/6/16 13:27
 * Email 396196516@qq.com
 * Info  有加载更多的RecyclerView
 */

public class AbRecyclerView extends RecyclerView {

    /** 上下文 */
    private Context context;

    /** 顶部加载更多 */
    private View headerView;

    /** 底部加载更多 */
    private View footerView;

    /** 当前加载的页 */
    private int currentPageNumber = 0;

    /**最后一个可见的item的位置 */
    private int lastVisibleItemPosition;

    /** 当前滑动的状态 */
    private int currentScrollState = 0;

    /** 当前状态 */
    private boolean loading = false;

    /** adapter */
    private AbRecyclerViewAdapter  adapter;

    /** 下一页的监听器 */
    private AbRecyclerViewLoadMoreListener recyclerViewLoadMoreListener;


    public AbRecyclerView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public AbRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public AbRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public void init(){
        MyScrollListener scrollListener = new MyScrollListener();
        this.addOnScrollListener(scrollListener);
    }

    /**
     * 设置完header和footer最后调用
     * @param adapter
     */
    public void setRecyclerViewAdapter(AbRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        if(headerView != null){
            adapter.setHeaderView(headerView);
        }
        if(footerView != null){
            adapter.setFooterView(footerView);
        }

        this.setAdapter(adapter);
    }

    public class MyScrollListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            currentScrollState = newState;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                // 加载下一页
                if(footerView instanceof  AbFooterView){
                    AbFooterView abFooterView = (AbFooterView)footerView;
                    if(!loading && recyclerViewLoadMoreListener!=null && abFooterView.getStatus() != AbFooterView.STATE_NO_MORE){
                        recyclerViewLoadMoreListener.loadMore(currentPageNumber+ 1);
                    }
                }


            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void removeFooterView() {
        this.footerView = null;
        adapter.removeFooterView();
    }

    public AbRecyclerViewLoadMoreListener getRecyclerViewLoadMoreListener() {
        return recyclerViewLoadMoreListener;
    }

    public void setRecyclerViewLoadMoreListener(AbRecyclerViewLoadMoreListener recyclerViewLoadMoreListener) {
        this.recyclerViewLoadMoreListener = recyclerViewLoadMoreListener;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setLoadSuccess(int pageNumber){
        this.currentPageNumber  = pageNumber;
    }

}
