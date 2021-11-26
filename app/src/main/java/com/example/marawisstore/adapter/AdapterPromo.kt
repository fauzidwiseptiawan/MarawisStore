package com.example.marawisstore.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.model.Produk

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
      

    }


}