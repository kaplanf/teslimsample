package com.kaplan.aclteslimsample;

import com.kaplan.aclteslimsample.activity.BaseActivity;
import com.kaplan.aclteslimsample.fragments.ImageListFragment;
import com.kaplan.aclteslimsample.fragments.ImageListFragment_;
import com.kaplan.aclteslimsample.fragments.MainFragment;
import com.kaplan.aclteslimsample.fragments.MainFragment_;
import com.kaplan.aclteslimsample.fragments.MapACLFragment;
import com.kaplan.aclteslimsample.fragments.MapACLFragment_;
import com.kaplan.aclteslimsample.interfaces.OnMainFragmentListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements OnMainFragmentListener {

    @AfterViews
    protected void afterViews() {
        toMainFragment();
    }

    @Override
    public void onCloseFragment(String tag) {

    }

    @Override
    public void onStartFragment(String tag) {

    }

    public void toImageListFragment() {
        ImageListFragment fragment = new ImageListFragment_();
        replaceFragment(R.id.main_frame, fragment, true);
    }

    public void toMainFragment() {
        MainFragment fragment = new MainFragment_();
        replaceFragmentAndClearBackStack(R.id.main_frame, fragment, true);
    }

    public void toMapFragment()
    {
        MapACLFragment fragment = new MapACLFragment_();
        replaceFragmentAndClearBackStack(R.id.main_frame, fragment, true);
    }
}
