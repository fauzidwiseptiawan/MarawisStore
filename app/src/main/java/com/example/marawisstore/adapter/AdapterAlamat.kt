package com.example.marawisstore.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.activity.AllProdukLainActivity
import com.example.marawisstore.activity.DetailKategoriActivity
import com.example.marawisstore.activity.DetailProdukActivity
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.model.Alamat
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.Promo
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.util.Config
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterAlamat(var data: ArrayList<Alamat>, var listener: Listeners): RecyclerView.Adapter<AdapterAlamat.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvTelpon = view.findViewById<TextView>(R.id.tv_telpon)
        val tvAlamat = view.findViewById<TextView>(R.id.tv_alamat)
        val layout = view.findViewById<CardView>(R.id.layout2)
        val rd = view.findViewById<RadioButton>(R.id.rd_alamat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_alamat,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        holder.rd.isChecked = a.isSelected
        holder.tvNama.text = a.nama
        holder.tvTelpon.text = a.telpon
        holder.tvAlamat.text = a.alamat + ", " + a.kota + ", " + a.kodepos + ", (" + a.type + ")"

        holder.rd.setOnClickListener {
            a.isSelected = true
            listener.onClicked(a)
        }

        holder.layout.setOnClickListener {
            a.isSelected = true
            listener.onClicked(a)
        }
    }

    interface Listeners {
        fun onClicked(data: Alamat)
    }
}