package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.widget.ListTabButton;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrdersFragment extends Fragment {

    private static final String TAG  ="OrdersFragment";
    private View mContentView;
    private Activity mContext;
    public static int subTabIndex = 0;

    private ListTabButton toDoTab;
    private ListTabButton haveDoneTab;
    private ListTabButton deliveringTab;
    private ListTabButton deliveryFinishedTab;

    private ListTabButtonListener ltbListener;

    private Spinner sortSpinner;
    private Spinner categorySpinner;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        Log.d(TAG, "onAttach");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ltbListener = new ListTabButtonListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_orders,container,false);
        initViews(mContentView);
        return mContentView;
    }

    private void initViews(View contentView){
        initTabButtons(contentView);
        initSpinner(contentView,mContext);
    }

    private void initTabButtons(View contentView){
        toDoTab = (ListTabButton) contentView.findViewById(R.id.tab_to_do);
        haveDoneTab = (ListTabButton) contentView.findViewById(R.id.tab_have_done);
        deliveringTab = (ListTabButton) contentView.findViewById(R.id.tab_delivering);
        deliveryFinishedTab = (ListTabButton) contentView.findViewById(R.id.tab_delivery_finished);

        toDoTab.setClickBg(true);
        haveDoneTab.setClickBg(false);
        deliveringTab.setClickBg(false);
        deliveryFinishedTab.setClickBg(false);

        toDoTab.setOnClickListener(ltbListener);
        haveDoneTab.setOnClickListener(ltbListener);
        deliveringTab.setOnClickListener(ltbListener);
        deliveryFinishedTab.setOnClickListener(ltbListener);
    }

    private void initSpinner(View contentView,Context context){
        sortSpinner = (Spinner) contentView.findViewById(R.id.spinner_sort);
        categorySpinner = (Spinner) contentView.findViewById(R.id.spinner_category);

        final ArrayAdapter< String> adapter_sort = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_sort.add(context.getResources().getString(R.string.sort_by_produce_effect));
        adapter_sort.add(context.getResources().getString(R.string.sort_by_order_time));
        adapter_sort.add(context.getResources().getString(R.string.sort_by_delivery_effect));
        adapter_sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter_sort);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("xiong","排序：positon ="+position+"选择了 "+adapter_sort.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter< String> adapter_category = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_category.add(context.getResources().getString(R.string.category_all));
        adapter_category.add(context.getResources().getString(R.string.category_now));
        adapter_category.add(context.getResources().getString(R.string.category_order));
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter_category);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("xiong","排序：positon ="+position+"选择了 "+adapter_category.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }


    class ListTabButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tab_to_do:
                    toDoTab.setClickBg(true);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    break;
                case R.id.tab_have_done:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(true);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(false);
                    break;
                case R.id.tab_delivering:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(true);
                    deliveryFinishedTab.setClickBg(false);
                    break;
                case R.id.tab_delivery_finished:
                    toDoTab.setClickBg(false);
                    haveDoneTab.setClickBg(false);
                    deliveringTab.setClickBg(false);
                    deliveryFinishedTab.setClickBg(true);
                    break;
            }
        }
    }

}
