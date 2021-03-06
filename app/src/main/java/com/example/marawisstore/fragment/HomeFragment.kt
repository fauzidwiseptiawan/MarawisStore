package com.example.marawisstore.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.example.marawisstore.R
import com.example.marawisstore.activity.AllProdukLainActivity
import com.example.marawisstore.activity.AllProdukTerbaruActivity
import com.example.marawisstore.activity.FavoritProdukActivity
import com.example.marawisstore.adapter.AdapterProduk
import com.example.marawisstore.adapter.AdapterSlider
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.room.MyDatabaseFavorit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    lateinit var vpSlider: ViewPager
    lateinit var rvProduk: RecyclerView
    lateinit var rvProdukLainnya: RecyclerView
    lateinit var tvProdukTerbaru: TextView
    lateinit var tvProdukLain: TextView
    lateinit var swRefresh: SwipeRefreshLayout
    lateinit var btnFavorit: Button
    lateinit var myDbFav: MyDatabaseFavorit

    private var listProduk: ArrayList<Produk> = ArrayList()
    private var listProdukLainnya: ArrayList<Produk> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        myDbFav = MyDatabaseFavorit.getInstance(requireActivity())!!

        init(view)
        displayProduk()
        getProdukTerbaru()
        getProdukLainnya()
        mainButton()
        refreshApp()
        return view
    }

    private fun refreshApp() {
        swRefresh.setOnRefreshListener {
            getProdukTerbaru()
            getProdukLainnya()
            mainButton()
        }
    }

    private fun mainButton(){
        btnFavorit.setOnClickListener {
            startActivity(Intent(activity, FavoritProdukActivity::class.java))
        }
        tvProdukTerbaru.setOnClickListener {
            startActivity(Intent(activity, AllProdukTerbaruActivity::class.java))
        }
        tvProdukLain.setOnClickListener {
            startActivity(Intent(activity, AllProdukLainActivity::class.java))
        }
    }

    fun displayProduk() {
        val arrSlider = ArrayList<Int> ()
        arrSlider.add(R.drawable.banner_test)
        arrSlider.add(R.drawable.banner_test)

        val adapterSlider = AdapterSlider(arrSlider,activity)
        vpSlider.adapter= adapterSlider

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        rvProduk.adapter = AdapterProduk(requireActivity(), listProduk)
        rvProduk.layoutManager = layoutManager

        rvProdukLainnya.adapter = AdapterProduk(requireActivity(), listProdukLainnya)
        rvProdukLainnya.layoutManager = layoutManager2
    }

    fun getProdukTerbaru() {
        ApiConfig.instanceRetrofit.getProductTerbaru().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
                if (swRefresh.isRefreshing){
                    swRefresh.isRefreshing = false
                    Toast.makeText(activity,"Error:"+t.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (swRefresh.isRefreshing){
                    swRefresh.isRefreshing = false
                }
                if (response.isSuccessful) {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProduk = res.produk
                        displayProduk()
                    }
                }else {
                    swRefresh.isRefreshing = true
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    fun getProdukLainnya() {
        ApiConfig.instanceRetrofit.getProductLainnya().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
                if (swRefresh.isRefreshing){
                    swRefresh.isRefreshing = false
                    Toast.makeText(activity,"Error:"+t.message, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (swRefresh.isRefreshing){
                    swRefresh.isRefreshing = false
                }
                if (response.isSuccessful) {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProdukLainnya = res.produk
                        displayProduk()
                    }
                }else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun init(view: View) {
        vpSlider        = view.findViewById(R.id.vp_slider)
        swRefresh       = view.findViewById(R.id.swipeToRefresh)
        tvProdukTerbaru = view.findViewById(R.id.tv_ProdukTerbaru)
        rvProduk        = view.findViewById(R.id.rv_produk)
        tvProdukLain    = view.findViewById(R.id.tv_ProdukLain)
        rvProdukLainnya = view.findViewById(R.id.rv_produk_lainnya)
        btnFavorit      = view.findViewById(R.id.btn_favorit)
    }

}