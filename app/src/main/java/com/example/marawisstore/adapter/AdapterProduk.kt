package com.example.marawisstore.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.activity.DetailProdukActivity
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import java.util.*

class AdapterProduk(var activity: Activity, var data: ArrayList<Produk>): RecyclerView.Adapter<AdapterProduk.Holder>() {

    @SuppressLint("SetTextI18n")

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaProduk = view.findViewById<TextView>(R.id.tv_nama_produk)
        val tvHargaProduk = view.findViewById<TextView>(R.id.tv_harga_produk)
        val lyDiskon = view.findViewById<LinearLayout>(R.id.ly_diskon)
        val tv_diskon = view.findViewById<TextView>(R.id.tv_diskon)
        val tv_persen = view.findViewById<TextView>(R.id.tv_persen)
        val lyPromo = view.findViewById<LinearLayout>(R.id.ly_promo)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk_tb)
        val layout = view.findViewById<RelativeLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(
            R.layout.item_produk,
            parent,
            false
        )
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val a = data[position]

        val diskon = Integer.valueOf(a.diskon)
        val harga = Integer.valueOf(a.harga)
        val hargaCoret = (harga - ((a.diskon.toDouble() / 100) * harga).toInt())

         if (a.diskon != 0){
           holder.lyPromo.visibility = View.GONE
           holder.lyDiskon.visibility = View.VISIBLE
           holder.tv_diskon.text = Helper().gantiRupiah(harga)
           holder.tv_diskon.paintFlags = holder.tv_diskon.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        holder.tv_persen.text = "${diskon}%"
        holder.tvNamaProduk.text = data[position].nama_produk
        holder.tvHargaProduk.text = Helper().gantiRupiah(hargaCoret)
        var gambar = Config.produkUrl + data[position].image

        Picasso.get()
                .load(gambar)
                .placeholder(R.color.color_300)
                .error(R.color.color_300)
                .fit()
                .centerCrop()
                .into(holder.imgProduk)

            holder.layout.setOnClickListener {
                val activiti = Intent(activity, DetailProdukActivity::class.java)
                val str = Gson().toJson(data[position], Produk::class.java)

                activiti.putExtra("extra", str)
                activity.startActivity(activiti)
            }

    }

}