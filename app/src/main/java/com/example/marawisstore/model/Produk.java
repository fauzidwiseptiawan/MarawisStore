package com.example.marawisstore.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "keranjang") // the name of tabel
public class Produk implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    public int idTb;

    public int id_produk;
    public int id_user;
    public int id_katergori;
    public String kode_produk;
    public String nama_produk;
    public String slug_produk;
    public String keterangan;
    public String keywords;
    public String telepon;
    public String posisi;
    public String harga;
    public int diskon;
    public String stok;
    public String berat;
    public String status_produk;
    public String tanggal_post;
    public String tanggal_update;
    public String gambar;
    public String img;
    public String nama_kategori;

    public int jumlah = 1;
    public int jumlahfav = 1;
    public boolean selected = true;
}
