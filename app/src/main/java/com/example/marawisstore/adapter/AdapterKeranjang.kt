package com.example.marawisstore.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class AdapterKeranjang(var activity: Activity, var data: ArrayList<Produk>, var listener: Listeners): RecyclerView.Adapter<AdapterKeranjang.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val layout = view.findViewById<CardView>(R.id.layout)
        val lyDiskon = view.findViewById<LinearLayout>(R.id.ly_diskon)
        val tvDiskon = view.findViewById<TextView>(R.id.tv_diskon)
        val tvPersen = view.findViewById<TextView>(R.id.tv_persen)
        val btnTambah = view.findViewById<ImageView>(R.id.btn_tambah)
        val btnKurang = view.findViewById<ImageView>(R.id.btn_kurang)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_delete)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)
        val hargaCoret = (harga - ((produk.diskon.toDouble() / 100) * harga).toInt())
        val diskon = Integer.valueOf(produk.diskon)

        if (produk.diskon != 0){
            holder.lyDiskon.visibility = View.VISIBLE
            holder.tvDiskon.text = Helper().gantiRupiah(harga)
            holder.tvDiskon.paintFlags = holder.tvDiskon.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.tvPersen.text = "${diskon}%"
        holder.tvNama.text = produk.nama_produk
        holder.tvHarga.text = Helper().gantiRupiah(hargaCoret * produk.jumlah)

        var jumlah = data[position].jumlah
        holder.tvJumlah.text = jumlah.toString()

        holder.checkBox.isChecked = produk.selected
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            produk.selected = isChecked
            update(produk)
        }

        val gambar = Config.produkUrl + data[position].image
        Picasso.get()
            .load(gambar)
            .placeholder(R.color.color_300)
            .error(R.color.color_300)
            .into(holder.imgProduk)

        holder.btnTambah.setOnClickListener {
            jumlah++

            produk.jumlah = jumlah
            update(produk)

            holder.tvJumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(hargaCoret * jumlah)
        }

        holder.btnKurang.setOnClickListener {
            if(jumlah <= 1) return@setOnClickListener

            jumlah--

            produk.jumlah = jumlah
            update(produk)

            holder.tvJumlah.text = jumlah.toString()
            holder.tvHarga.text = Helper().gantiRupiah(hargaCoret * jumlah)
        }

        holder.btnDelete.setOnClickListener {
            delete(produk)
            update(produk)
            listener.onDelete(position)
            Toast.makeText(activity,"Barang berhasil dihapus dari keranjang", Toast.LENGTH_SHORT).show()
        }

    }

    interface Listeners{
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(data: Produk){
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().update(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    listener.onUpdate()
                })
    }

    private fun delete(data: Produk){
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable { myDb!!.daoKeranjang().delete(data) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                })
    }
}