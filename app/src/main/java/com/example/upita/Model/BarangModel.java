package com.example.upita.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class BarangModel implements Parcelable {

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    private String nama;
    private String harga;
    private int jumlah;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nama);
        dest.writeString(this.harga);
        dest.writeInt(this.jumlah);
    }

    public BarangModel() {
    }

    protected BarangModel(Parcel in) {
        this.nama = in.readString();
        this.harga = in.readString();
        this.jumlah = in.readInt();
    }

    public static final Creator<BarangModel> CREATOR = new Creator<BarangModel>() {
        @Override
        public BarangModel createFromParcel(Parcel source) {
            return new BarangModel(source);
        }

        @Override
        public BarangModel[] newArray(int size) {
            return new BarangModel[size];
        }
    };
}