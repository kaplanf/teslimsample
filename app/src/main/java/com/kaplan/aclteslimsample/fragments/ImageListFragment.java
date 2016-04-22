package com.kaplan.aclteslimsample.fragments;

import android.app.Activity;
import android.widget.GridView;

import com.kaplan.aclteslimsample.ACLTeslimApplication;
import com.kaplan.aclteslimsample.R;
import com.kaplan.aclteslimsample.adapters.PhotoGridAdapter;
import com.kaplan.aclteslimsample.interfaces.OnMainFragmentListener;
import com.kaplan.aclteslimsample.restful.model.Image;
import com.kaplan.aclteslimsample.restful.model.Response;
import com.kaplan.aclteslimsample.restful.restclient.RestClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.rest.RestService;

import java.util.ArrayList;

/**
 * Created by kaplanfatt on 25/02/16.
 */
@EFragment(R.layout.fragment_image_list)
public class ImageListFragment extends BaseFragment {

    @RestService
    RestClient restClient;

    Response fetchedObject;

    private OnMainFragmentListener onMainFragmentListener;

    @ViewById(R.id.gridView)
    GridView gridView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onMainFragmentListener = (OnMainFragmentListener) activity;
    }

    @AfterViews
    protected void afterViews() {
        showProgressDialog();
        fetchImages();
    }

    @Background
    protected void fetchImages() {
        fetchedObject = restClient.
                getPhotos(ACLTeslimApplication.getInstance().FLICKR_API_KEY, ACLTeslimApplication.getInstance().FLICKR_API_SUFFIX, "1");
        hideProgressDialog();
        if(fetchedObject!=null)
        {
            if(fetchedObject.photos!=null)
            {
                if(fetchedObject.photos.imageList!=null)
                {
                    updateUI(fetchedObject.photos.imageList);
                }
            }
        }
    }

    @UiThread
    protected void updateUI(ArrayList<Image> images)
    {
        PhotoGridAdapter adapter = new PhotoGridAdapter(getActivity(),images);
        gridView.setAdapter(adapter);
    }
}
