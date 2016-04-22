package com.kaplan.aclteslimsample.fragments;

import android.app.Activity;
import android.widget.TextView;

import com.kaplan.aclteslimsample.R;
import com.kaplan.aclteslimsample.interfaces.OnMainFragmentListener;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by kaplanfatt on 01/03/16.
 */
@EFragment(R.layout.main_fragment)
public class MainFragment extends BaseFragment {

    private OnMainFragmentListener onMainFragmentListener;

    @ViewById(R.id.firstText)
    TextView firstText;

    @ViewById(R.id.secondText)
    TextView secondText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onMainFragmentListener = (OnMainFragmentListener) activity;
    }

    @Click(R.id.firstText)
    void firstClick() {
        getMainActivity().toMapFragment();
    }

    @Click(R.id.secondText)
    void secondClick() {
        getMainActivity().toImageListFragment();
    }

}
