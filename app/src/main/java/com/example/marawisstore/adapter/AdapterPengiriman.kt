package com.example.marawisstore.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.util.Config
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_costume.*

class AdapterPengiriman(var activity: Activity, var data: ArrayList<Produk>): RecyclerView.Adapter<AdapterPengiriman.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_pengiriman,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)

        holder.tvNama.text = produk.nama_produk
        holder.tvHarga.text = Helper().gantiRupiah(harga * produk.jumlah)

        var jumlah = data[position].jumlah
        holder.tvJumlah.text = jumlah.toString() + " barang " + "(" + produk.berat +" gram)"

        var gambar = Config.produkUrl + data[position].image
        Picasso.get()
            .load(gambar)
            .placeholder(R.color.color_300)
            .error(R.color.color_300)
            .into(holder.imgProduk)

    }
}