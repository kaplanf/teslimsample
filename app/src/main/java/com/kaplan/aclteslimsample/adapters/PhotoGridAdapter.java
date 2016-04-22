package com.kaplan.aclteslimsample.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kaplan.aclteslimsample.ACLTeslimApplication;
import com.kaplan.aclteslimsample.R;
import com.kaplan.aclteslimsample.components.SquareImageView;
import com.kaplan.aclteslimsample.restful.model.Image;

import java.util.List;

/**
 * Created by kaplanfatt on 02/03/16.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private List<Image> imageList;
    private LayoutInflater inflater;
    private Context context;

    public PhotoGridAdapter(Context context, List<Image> imageList) {
        this.imageList = imageList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Image getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_photo_grid, null, false);
            viewHolder.setPhoto((SquareImageView) view.findViewById(R.id.squareGridImageView));

            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();
        Image image = getItem(position);
        ACLTeslimApplication.getInstance().getPicassoInstance().with(context).
                load(ACLTeslimApplication.getInstance().createFlickrImageURL(image)).into(viewHolder.getPhoto());
        return view;
    }

    static class ViewHolder {
        private SquareImageView photo;


        public ImageView getPhoto() {
            return photo;
        }

        public void setPhoto(SquareImageView photo) {
            this.photo = photo;
        }


    }
}