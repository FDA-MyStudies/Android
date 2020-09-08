package com.harvard.studyAppModule;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.harvard.R;

import java.util.ArrayList;


public class ReachoutFragment<T> extends Fragment {

    private RecyclerView mReachoutRecyclerView;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reachout, container, false);
        initializeXMLId(view);
        setRecyclearView();
        return view;
    }

    private void initializeXMLId(View view) {
        mReachoutRecyclerView = (RecyclerView) view.findViewById(R.id.reachoutRecyclerView);
    }


    private void setRecyclearView() {
        mReachoutRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mReachoutRecyclerView.setNestedScrollingEnabled(false);
        ArrayList<String> reachoutList = new ArrayList<>();
        reachoutList.add(getString(R.string.anonymous_feedback));
        reachoutList.add(getString(R.string.need_help));
        ReachoutListAdapter reachoutListAdapter = new ReachoutListAdapter(getActivity(), reachoutList);
        mReachoutRecyclerView.setAdapter(reachoutListAdapter);
    }

}
