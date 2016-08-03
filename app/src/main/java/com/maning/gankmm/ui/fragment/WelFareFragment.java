package com.maning.gankmm.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.maning.gankmm.R;
import com.maning.gankmm.app.MyApplication;
import com.maning.gankmm.bean.GankEntity;
import com.maning.gankmm.ui.adapter.RecyclePicAdapter;
import com.maning.gankmm.ui.base.BaseFragment;
import com.maning.gankmm.ui.iView.IWelFareView;
import com.maning.gankmm.ui.presenter.impl.WelFarePresenterImpl;
import com.maning.gankmm.utils.IntentUtils;
import com.maning.gankmm.utils.MySnackbar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 福利Fragment
 */
public class WelFareFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IWelFareView {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private RecyclePicAdapter recyclePicAdapter;

    private WelFarePresenterImpl welFarePresenter;

    private ArrayList<String> imagesList;

    private ArrayList<String> headLines;

    public static WelFareFragment newInstance() {
        return new WelFareFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wel_fare, container, false);
        ButterKnife.bind(this, view);

        initRefresh();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        welFarePresenter = new WelFarePresenterImpl(getActivity(), this);

        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        }, 100);
    }

    private void initRecycleView(List<GankEntity> welFareList) {
        if (recyclePicAdapter == null) {
            headLines = new ArrayList<>();

            recyclePicAdapter = new RecyclePicAdapter(context, welFareList, headLines);
            swipeTarget.setAdapter(recyclePicAdapter);
            //点击事件
            recyclePicAdapter.setOnItemClickLitener(new RecyclePicAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    List<GankEntity> allDatas = recyclePicAdapter.getAllDatas();
                    imagesList = new ArrayList<>();
                    for (int i = 0; i < allDatas.size(); i++) {
                        imagesList.add(allDatas.get(i).getUrl());
                    }
                    IntentUtils.startToImageShow(context, imagesList, position);
                }
            });

        } else {
            recyclePicAdapter.updateDatas(welFareList);
        }

    }

    private void initRefresh() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        swipeTarget.setLayoutManager(staggeredGridLayoutManager);
        swipeTarget.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onRefresh() {
        welFarePresenter.getNewDatas();
    }

    @Override
    public void setWelFareList(List<GankEntity> welFareList) {
        initRecycleView(welFareList);
    }

    @Override
    public void showToast(String msg) {
        MySnackbar.makeSnackBarRed(swipeTarget, msg);
    }

    @Override
    public void overRefresh() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void setLoadMoreEnabled(boolean flag) {
        swipeToLoadLayout.setLoadMoreEnabled(flag);
    }

    @Override
    public void onLoadMore() {
        welFarePresenter.getMoreDatas();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WelFareFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WelFareFragment");
    }

    @Override
    public void onDestroyView() {
        welFarePresenter.detachView();
        if (recyclePicAdapter != null) {
            recyclePicAdapter.destroyList();
            RecyclerView.ViewHolder childViewHolder = swipeTarget.getChildViewHolder(swipeTarget.getChildAt(0));
            RecyclePicAdapter.MyViewHolderHeader viewHolder = (RecyclePicAdapter.MyViewHolderHeader) childViewHolder;
            viewHolder.destroyHeadLines();
        }
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
