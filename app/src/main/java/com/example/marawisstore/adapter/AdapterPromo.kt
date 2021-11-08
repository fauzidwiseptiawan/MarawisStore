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
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.activity.AllProdukLainActivity
import com.example.marawisstore.activity.DetailKategoriActivity
import com.example.marawisstore.activity.DetailProdukActivity
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.Promo
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterPromo(var activity: Activity, var data:ArrayList<Produk>): RecyclerView.Adapter<AdapterPromo.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val layout = view.findViewById<CardView>(R.id.layout_kategori)
        val imgPromo = view.findViewById<ImageView>(R.id.imagePromo)
        val namaPromo = view.findViewById<TextView>(R.id.tv_namaPromo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_promo,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.namaPromo.text = data[position].posisi
        var gambar = Config.produkUrl + data[position].img

        Picasso.get()
            .load(gambar)
            .placeholder(R.drawable.blank)
            .error(R.drawable.blank)
            .fit()
            .centerCrop()
            .into(holder.imgPromo)


        holder.layout.setOnClickListener {
            val activiti = Intent(activity, DetailKategoriActivity::class.java)
            val str = Gson().toJson(data[position], Produk::class.java)

            activiti.putExtra("extra",str)
            activity.startActivity(activiti)
        }

    }


}