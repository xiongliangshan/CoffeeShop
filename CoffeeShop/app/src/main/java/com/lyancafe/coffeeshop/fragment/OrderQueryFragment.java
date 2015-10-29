package com.lyancafe.coffeeshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lyancafe.coffeeshop.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2015/9/1.
 */
public class OrderQueryFragment extends Fragment {

    private static final String TAG  ="OrderQueryFragment";
    private View mContentView;
    private Spinner dateSpinner;
    private Activity mContext;
    private static String[]  dateArray;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_order_query,container,false);
        initSpinner(mContext, mContentView);
        return mContentView;
    }

    private void initSpinner(Context context,View contentView){
        dateSpinner = (Spinner) contentView.findViewById(R.id.spinner_date);
        final ArrayAdapter< String> adapter_date = new ArrayAdapter< String>(context,android.R.layout.simple_spinner_item);
        adapter_date.add(context.getResources().getString(R.string.sort_by_produce_effect));
        adapter_date.add(context.getResources().getString(R.string.sort_by_order_time));
        adapter_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter_date);
        dateSpinner.setSelection(0, true);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private String[] getDatesArray(Context context){
        String[] array = new String[3];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.roll(java.util.Calendar.DAY_OF_YEAR,-1);
        array[0] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        array[1] = sdf.format(cal.getTime());
        cal.roll(java.util.Calendar.DAY_OF_YEAR, -1);
        array[2] = sdf.format(cal.getTime());
        for(int i = 0;i<array.length;i++){
            Log.d(TAG,"array["+i+"] = "+array[i]);
        }
        return array;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDatesArray(mContext);
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
