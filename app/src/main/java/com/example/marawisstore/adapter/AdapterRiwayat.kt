package com.example.marawisstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Bank
import com.example.marawisstore.model.Transaksi

class AdapterRiwayat(var data:ArrayList<Transaksi>, var listener: Listeners): RecyclerView.Adapter<AdapterRiwayat.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvTanggal = view.findViewById<TextView>(R.id.tv_tgl)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
      
        val a = data[position]

        val name = a.details[0].product.nama_produk

        holder.tvNama.text = name
        holder.tvHarga.text = Helper().gantiRupiah(a.total_harga)
        holder.tvJumlah.text = a.total_qty + " Items"
        holder.tvStatus.text = a.status
        val formatBaru = "dd MMMM yyyy"
        holder.tvTanggal.text = Helper().convertTanggal(a.created_at, formatBaru)
        holder.layout.setOnClickListener {
            listener.onClicked(a)
        }
    }



    interface Listeners {
        fun onClicked(data: Transaksi)
    }


}