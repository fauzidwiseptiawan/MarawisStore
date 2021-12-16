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

    public String kode_produk;
    public String user_id;
    public String category_id;
    public String nama_produk;
    public String keterangan;
    public String harga;
    public String stok;
    public int diskon = 0;
    public String berat;
    public String status_produk;
    public String created_at;
    public String updated_at;
    public String image;

    public int jumlah = 1;
    public int jumlahfav = 1;
    public boolean selected = true;
}
