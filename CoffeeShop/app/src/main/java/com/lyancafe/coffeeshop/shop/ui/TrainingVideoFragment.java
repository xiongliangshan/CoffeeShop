package com.lyancafe.coffeeshop.shop.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.shop.presenter.TrainingVideoPresenter;
import com.lyancafe.coffeeshop.shop.presenter.TrainingVideoPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.TrainingVideoView;
import com.lyancafe.coffeeshop.utils.SpaceItemDecoration;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingVideoFragment extends BaseFragment implements TrainingVideoView{


    @BindView(R.id.rv_videolist)
    RecyclerView rvVideolist;
    Unbinder unbinder;
    private TrainingVideoPresenter trainingVideoPresenter;
    private VideoListAdpater mAdapter;

    public TrainingVideoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trainingVideoPresenter = new TrainingVideoPresenterImpl(getContext(),this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL, false);
        rvVideolist.setLayoutManager(gridLayoutManager);
        rvVideolist.addItemDecoration(new SpaceItemDecoration(3, OrderHelper.dip2Px(20, getActivity()), false));
        mAdapter = new VideoListAdpater(new ArrayList<VideoBean>(),getContext());
        mAdapter.setRecyclerViewListener(new VideoListAdpater.RecyclerViewListener() {
            @Override
            public void needToScroll() {
                rvVideolist.smoothScrollToPosition(mAdapter.getItemCount()-1);
            }
        });
        rvVideolist.setAdapter(mAdapter);
    }

    @Override
    public void bindDataToView(List<VideoBean> list) {
        mAdapter.setVideos(list);
    }


    @Override
    public void showToast(String promptStr) {
        ToastUtil.show(getContext(),promptStr);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if(!isResumed()){
            return;
        }
        trainingVideoPresenter.loadVideos();
    }

    @Override
    public void onInVisible() {
        super.onInVisible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private List<VideoBean> createTestData(){
        List<VideoBean> list = new ArrayList<>();
        VideoBean v1 = new VideoBean();
        v1.setTitle("王亭文同仁堂");
        v1.setUrl("http://qaimages.lyancafe.com/training/videos/1505381240699.mp4");
        list.add(v1);
        VideoBean v2 = new VideoBean();
        v2.setTitle("工会经费鸡飞狗叫和");
        v2.setUrl("http://qaimages.lyancafe.com/training/videos/1505376240236");
        list.add(v2);
        VideoBean v3 = new VideoBean();
        v3.setTitle("3热污染翁");
        v3.setUrl("http://qaimages.lyancafe.com/training/videos/1505382140099.flv");
        list.add(v3);
        return list;
    }
}
