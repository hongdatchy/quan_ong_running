package com.hongdatchy.quan.ong.quanongrunning;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductListViewAdapter extends BaseAdapter {

    final ArrayList<Product> listProduct;

    public ProductListViewAdapter(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public Object getItem(int position) {
        return listProduct.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.notification_view, null);
        } else viewProduct = convertView;

        Product product = (Product) getItem(position);
        ((TextView) viewProduct.findViewById(R.id.date)).setText(product.getDate().toString().split("GMT")[0]);

        String distance = product.getDistance() + " km";
        ((TextView) viewProduct.findViewById(R.id.distance)).setText(distance);
        return viewProduct;
    }

}
