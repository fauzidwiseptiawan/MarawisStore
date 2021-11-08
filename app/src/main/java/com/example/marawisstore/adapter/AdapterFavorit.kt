package com.example.marawisstore.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.marawisstore.R
import com.example.marawisstore.activity.FavoritProdukActivity
import com.example.marawisstore.helper.Helper
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import com.example.marawisstore.room.MyDatabaseFavorit
import com.example.marawisstore.util.Config
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.toolbar_costume.*

class AdapterFavorit(var activity: Activity, var data: ArrayList<Produk>, var listener: Listeners): RecyclerView.Adapter<AdapterFavorit.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)

        val btnDelete = view.findViewById<ImageView>(R.id.btn_delete)
        val btnTambahKeranjang = view.findViewById<RelativeLayout>(R.id.btn_tambah_keranjang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_favorit,parent,false)
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

        holder.btnTambahKeranjang.setOnClickListener { view ->
            listener.onItemClick(produk)
        }

        var gambar = Config.produkUrl + data[position].gambar
        Picasso.get()
            .load(gambar)
            .placeholder(R.drawable.blank)
            .error(R.drawable.blank)
            .into(holder.imgProduk)

        holder.btnDelete.setOnClickListener {
            delete(produk)
            listener.onDelete(position)
            Toast.makeText(activity,"Barang berhasil dihapus dari wishlist", Toast.LENGTH_SHORT).show()
        }
    }

    interface Listeners{
        fun onInsert(position: Int)
        fun onUpdate()
        fun onDelete(position: Int)
        fun onItemClick(produk: Produk)
    }

    private fun delete(data: Produk){
        val myDbFavorit = MyDatabaseFavorit.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDbFavorit!!.daoFavorit().deletefav(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                })
    }
}