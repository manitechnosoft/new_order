package com.mobile.order.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mobile.order.R;
import com.mobile.order.activity.SalesOrderLandActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReprintFragment extends Fragment {
    private View mViewHolder;
    Button nextButton;
    Button prevButton;
    @BindView(R.id.new_order)
    Button newOrder;

    @BindView(R.id.re_print)
    Button rePrint;

    FrameLayout container;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        // Inflate the xml file for the fragment
        if (mViewHolder == null) {
            mViewHolder = inflater.inflate(R.layout.fragment_reprint, parent, false);
            ButterKnife.bind(this, mViewHolder);
            final SalesOrderLandActivity baseActivity = (SalesOrderLandActivity)getActivity();
            container = baseActivity.findViewById(R.id.flContainer);
            nextButton = getActivity().findViewById(R.id.next_button);
            prevButton = getActivity().findViewById(R.id.prev_button);
            nextButton.setText("Print  ");
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.GONE);
            newOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    baseActivity.nextClick();
                }
            });
        }
        return mViewHolder;
    }

}
