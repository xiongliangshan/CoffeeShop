package com.lyancafe.coffeeshop.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.DeliverFragmentPagerAdapter;
import com.lyancafe.coffeeshop.event.RefreshDeliveringDataEvent;
import com.lyancafe.coffeeshop.event.RefreshToFetchDataEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeliverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeliverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliverFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DeliverFragmentPagerAdapter mPagerAdapter;

    public DeliverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliverFragment newInstance(String param1, String param2) {
        DeliverFragment fragment = new DeliverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xls","DeliverFragment-onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_deliver, container, false);
        initViews(contentView);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("xls","DeliverFragment-onResume, postion="+tabLayout.getSelectedTabPosition());

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("xls","DeliverFragment-onPause");
    }

    private void initViews(View contentView) {
        tabLayout  = (TabLayout) contentView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) contentView.findViewById(R.id.vp_container);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ToFetchFragment());
        fragments.add(new DeliveringFragment());
        mPagerAdapter = new DeliverFragmentPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);



    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        if(tab.getPosition()==0){
            Log.d("xls","ToFetch is selected");
            EventBus.getDefault().post(new RefreshToFetchDataEvent());
        }else if(tab.getPosition()==1){
            Log.d("xls","Delivering is selected");
            EventBus.getDefault().post(new RefreshDeliveringDataEvent());
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("xls","isVisibleToUser = "+isVisibleToUser);
    }
}
