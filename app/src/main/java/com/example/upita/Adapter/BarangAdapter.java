package com.example.upita.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upita.Model.BarangModel;
import com.example.upita.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.upita.MainActivity.totalharga;

public class BarangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BarangModel> mData = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    List<BarangModel> data= Collections.emptyList();
    BarangModel current;

    public interface OnDataChangeListener{
        public void onDataChanged(int size);
    }

    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
    // create constructor to innitilize context and data sent from MainActivity
    public BarangAdapter(Context context, List<BarangModel> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.barang_rv, parent,false);
        return new MyHolder(view);
    }

    // Bind data
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in recyclerview to bind data and assign values from list
        final MyHolder myHolder= (MyHolder) holder;
        final BarangModel current = data.get(position);
        myHolder.tvnama.setText(current.getNama());
        myHolder.tvjumlah.setText(String.valueOf(current.getJumlah()));
        myHolder.tvharga.setText(String.valueOf(current.getHarga()));
        final TextView hometotal = ((Activity)context).findViewById(R.id.total_belanja);
        myHolder.ibplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int awaljumlah = current.getJumlah() * Integer.parseInt(current.getHarga());
                totalharga -= awaljumlah;
                int jumlah = current.getJumlah()+1;
                myHolder.tvjumlah.setText(String.valueOf(jumlah));
                current.setJumlah(jumlah);
                int hargakali = Integer.parseInt(current.getHarga()) * jumlah;
                myHolder.tvharga.setText(String.valueOf(hargakali));
                totalharga += hargakali;
                mOnDataChangeListener.onDataChanged(totalharga);
            }
        });

        myHolder.ibmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int awaljumlah = current.getJumlah() * Integer.parseInt(current.getHarga());
                totalharga -= awaljumlah;
                int jumlah = current.getJumlah()-1;
                if(jumlah>=0){
                    myHolder.tvjumlah.setText(String.valueOf(jumlah));
                    current.setJumlah(jumlah);
                    int hargakali = Integer.parseInt(current.getHarga()) * jumlah;
                    myHolder.tvharga.setText(String.valueOf(hargakali));
                    totalharga += hargakali;
                    mOnDataChangeListener.onDataChanged(totalharga);
                }

            }
        });
    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_item_name) TextView tvnama;
        @BindView(R.id.tv_item_harga) TextView tvharga;
        @BindView(R.id.jumlah) TextView tvjumlah;
        @BindView(R.id.min) ImageButton ibmin;
        @BindView(R.id.plus) ImageButton ibplus;

        // create constructor to get widget reference
        private MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}