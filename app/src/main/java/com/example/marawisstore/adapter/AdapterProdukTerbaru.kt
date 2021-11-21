package com.example.marawisstore.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.activity.DetailProdukActivity
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

class AdapterProdukTerbaru(var activity: Activity, var data:ArrayList<Produk>): RecyclerView.Adapter<AdapterProdukTerbaru.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaProduk = view.findViewById<TextView>(R.id.tv_nama_produk)
        val tvHargaProduk = view.findViewById<TextView>(R.id.tv_harga_produk)
        val tvDiskon = view.findViewById<TextView>(R.id.tv_diskon)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk_tb)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_produk_terbaru,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position]

//        if (a.diskon != 0){
//            holder.tvDiskon.visibility = View.VISIBLE
//            holder.tvDiskon.text = Helper().gantiRupiah(data[position].diskon)
//            holder.tvDiskon.paintFlags = holder.tvDiskon.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//        } else{
//            holder.tvDiskon.visibility = View.INVISIBLE
//        }
        holder.tvNamaProduk.text = data[position].nama_produk
        holder.tvHargaProduk.text = Helper().gantiRupiah(data[position].harga)
        var gambar = Config.produkUrl + data[position].image

        Picasso.get()
            .load(gambar)
            .placeholder(R.drawable.blank)
            .error(R.drawable.blank)
            .fit()
            .centerCrop()
            .into(holder.imgProduk)

        holder.layout.setOnClickListener {
            val activiti = Intent(activity, DetailProdukActivity::class.java)
            val str = Gson().toJson(data[position], Produk::class.java)

            activiti.putExtra("extra",str)
            activity.startActivity(activiti)
        }

    }

}