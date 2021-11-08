package com.example.marawisstore.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.marawisstore.MainActivity
import com.example.marawisstore.R
import com.example.marawisstore.activity.AllProdukLainActivity
import com.example.marawisstore.activity.FavoritProdukActivity
import com.example.marawisstore.activity.PengirimanActivity
import com.example.marawisstore.activity.PengirimanBeliActivity
import com.example.marawisstore.adapter.AdapterKeranjang
import com.example.marawisstore.model.Produk
import com.example.marawisstore.room.MyDatabase
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_keranjang.*
import kotlinx.android.synthetic.main.toolbar_costume.*

class KeranjangFragment : Fragment() {

    lateinit var myDb: MyDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_keranjang,container,false)

        myDb = MyDatabase.getInstance(requireActivity())!!
        init(view)
        mainButton()
        refreshApp()

        return view
    }

    private fun refreshApp() {
       swRefresh.setOnRefreshListener {
           displayProduk()
           swRefresh.isRefreshing = false
       }
    }

    lateinit var adapter : AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })

        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    var totalHarga = 0
    var totalBerat = 0
    private fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList

        totalHarga = 0
        var isSelectedAll = true
        for (produk in listProduk){
            if(produk.selected){
                val berat = Integer.valueOf(produk.berat)
                totalBerat += (berat * produk.jumlah)
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            }else{
                isSelectedAll = false
            }
        }
        cbAll.isChecked = isSelectedAll
        tvTotal.text = com.example.marawisstore.helper.Helper().gantiRupiah(totalHarga)
    }

    private fun showFooter(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        for (produk in listProduks)
        if (produk.selected){
            rvProduk.visibility  = View.VISIBLE
            divFooter.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
            divHeader.visibility = View.VISIBLE
        }else{
            btnDelete.visibility = View.GONE
            divHeader.visibility = View.GONE
            divFooter.visibility = View.GONE
        }
    }

    private fun EmptyKeranjang(){
        val listProduks = myDb.daoKeranjang().getAll() as ArrayList

        if (listProduks.isEmpty()){
            emptyKeranjang.visibility = View.VISIBLE
        } else {
            emptyKeranjang.visibility = View.GONE
        }
    }

    private fun mainButton(){
        btnBayar.setOnClickListener {
            val intent = Intent(activity,PengirimanActivity::class.java)
            intent.putExtra("extraa","" + totalBerat)
            intent.putExtra("extra","" + totalHarga)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {

        }

        btnBelanja.setOnClickListener {
            startActivity(Intent(activity, AllProdukLainActivity::class.java))
        }


        cbAll.setOnClickListener {
            for (i in listProduk.indices){
                val produk = listProduk[i]
                produk.selected = cbAll.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }


    lateinit var btnDelete: ImageView
    lateinit var rvProduk: RecyclerView
    lateinit var tvTotal: TextView
    lateinit var btnBayar: TextView
    lateinit var btnBelanja: RelativeLayout
    lateinit var emptyKeranjang: LinearLayout
    lateinit var divHeader: RelativeLayout
    lateinit var divFooter: RelativeLayout
    lateinit var swRefresh: SwipeRefreshLayout
    lateinit var cbAll: CheckBox

    private fun init(view: View){
        btnDelete = view.findViewById(R.id.btn_delete)
        btnBelanja = view.findViewById(R.id.btn_belanja)
        rvProduk = view.findViewById(R.id.rv_produk)
        tvTotal = view.findViewById(R.id.tv_total)
        swRefresh = view.findViewById(R.id.swipeToRefresh)
        btnBayar = view.findViewById(R.id.btn_bayar)
        divFooter = view.findViewById(R.id.div_footer)
        divHeader = view.findViewById(R.id.div_header)
        emptyKeranjang = view.findViewById(R.id.empty_keranjang)
        cbAll = view.findViewById(R.id.cb_all)
    }

    override fun onResume() {
        hitungTotal()
        showFooter()
        EmptyKeranjang()
        displayProduk()
        super.onResume()
    }

}