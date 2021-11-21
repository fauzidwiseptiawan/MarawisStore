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
import com.example.marawisstore.adapter.AdapterProdukLainnya
import com.example.marawisstore.adapter.AdapterProdukTerbaru
import com.example.marawisstore.adapter.AdapterPromo
import com.example.marawisstore.adapter.AdapterSlider
import com.example.marawisstore.app.ApiConfig
import com.example.marawisstore.model.Produk
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.room.MyDatabaseFavorit
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_produk_terbaru.*
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        myDbFav = MyDatabaseFavorit.getInstance(requireActivity())!!

        init(view)
        getProduk()
        getProdukLainnya()
        mainButton()
        refreshApp()

        return view
    }

    private fun refreshApp() {
        swRefresh.setOnRefreshListener {
            getProduk()
            getProdukLainnya()
            swRefresh.isRefreshing = false
        }
    }

    private fun mainButton(){
        btnFavorit.setOnClickListener {
            startActivity(Intent(activity, FavoritProdukActivity::class.java))
        }

        tvProdukTerbaru.setOnClickListener {
            startActivity(Intent(activity, AllProdukTerbaruActivity::class.java))
        }

//        tvProdukLain.setOnClickListener {
//            startActivity(Intent(activity, AllProdukLainActivity::class.java))
//        }
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

        rvProduk.adapter = AdapterProdukTerbaru(requireActivity(), listProduk)
        rvProduk.layoutManager = layoutManager

        rvProdukLainnya.adapter = AdapterProdukLainnya(requireActivity(), listProdukLainnya)
        rvProdukLainnya.layoutManager = layoutManager2
    }

    private var listProduk: ArrayList<Produk> = ArrayList()
    private var listProdukLainnya: ArrayList<Produk> = ArrayList()

    fun getProduk() {
        ApiConfig.instanceRetrofit.getProduct().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
                pb.visibility = View.GONE
                Toast.makeText(activity,"Error:"+t.message, Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (response.isSuccessful) {
                    pb.visibility = View.GONE
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProduk = res.produk
                        displayProduk()
                    }
                }else {
                    Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    fun getProdukLainnya() {
        ApiConfig.instanceRetrofit.getProduct().enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                //Handle ketika gagal
                pbProduk.visibility = View.GONE
                Toast.makeText(activity,"Error:"+t.message, Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                if (response.isSuccessful) {
                    pbProduk.visibility = View.GONE
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