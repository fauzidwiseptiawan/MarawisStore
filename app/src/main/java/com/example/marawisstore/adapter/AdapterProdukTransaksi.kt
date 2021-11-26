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
import com.example.marawisstore.model.DetailTransaksi
import com.example.marawisstore.model.Transaksi
import com.example.marawisstore.util.Config
import com.squareup.picasso.Picasso

class AdapterProdukTransaksi(var data:ArrayList<DetailTransaksi>): RecyclerView.Adapter<AdapterProdukTransaksi.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val tvBerat = view.findViewById<TextView>(R.id.tv_berat)
        val tvTotalHarga = view.findViewById<TextView>(R.id.tv_totalHarga)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_produk_transaksi,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
      
        val a = data[position]

        val name = a.product.nama_produk
        val p = a.product

        holder.tvNama.text = name
        holder.tvHarga.text = Helper().gantiRupiah(p.harga)
        holder.tvTotalHarga.text = Helper().gantiRupiah(a.harga)
        holder.tvJumlah.text = a.qty.toString() + " Items"
        holder.tvBerat.text = a.berat.toString() + " Gram"

        holder.layout.setOnClickListener {
//            listener.onClicked(a)
        }

        var gambar = Config.produkUrl + p.image
        Picasso.get()
                .load(gambar)
                .placeholder(R.drawable.blank)
                .error(R.drawable.blank)
                .into(holder.imgProduk)
    }

    interface Listeners {
        fun onClicked(data: DetailTransaksi)
    }


}