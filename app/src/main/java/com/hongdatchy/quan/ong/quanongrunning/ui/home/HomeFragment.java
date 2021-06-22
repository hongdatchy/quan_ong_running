package com.hongdatchy.quan.ong.quanongrunning.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hongdatchy.quan.ong.quanongrunning.Common;
import com.hongdatchy.quan.ong.quanongrunning.LocalStorage;
import com.hongdatchy.quan.ong.quanongrunning.MainActivity;
import com.hongdatchy.quan.ong.quanongrunning.Product;
import com.hongdatchy.quan.ong.quanongrunning.ProductListViewAdapter;
import com.hongdatchy.quan.ong.quanongrunning.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<Product> listProduct;
    ListView listViewProduct;
    ProductListViewAdapter productListViewAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        listProduct = new ArrayList<>();
        LocalStorage db = new LocalStorage();
        db.load(requireContext());
        Common.status = "default";
        productListViewAdapter = new ProductListViewAdapter(listProduct);
        listViewProduct = view.findViewById(R.id.list_product);
        listViewProduct.setAdapter(productListViewAdapter);

        for(int i =0; i< db.listDistance.size(); i++){
            listProduct.add(new Product(db.listDate.get(i), db.listDistance.get(i)));
        }
        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.gotoCurrentRunning();
                Common.indexProductClicked = position;
                Common.status = "review_running";
            }
        });
        productListViewAdapter.notifyDataSetChanged();

        return view;
    }
}