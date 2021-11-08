package com.example.marawisstore.app

import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.rajaongkir.ResponOngkir
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("Api_pelanggan/register")
    fun register(
        @Field("nama") name : String,
        @Field("email") email : String,
        @Field("telpon") telpon : String,
        @Field("password") password : String
    ):Call<ResponModel>

    @FormUrlEncoded
    @POST("Api_pelanggan/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ):Call<ResponModel>

    @GET("Api_produk/allProduk")
    fun getallproduk():Call<ResponModel>

    @GET("Api_produk/allProdukTerbaru")
    fun getprodukterbaru():Call<ResponModel>

    @GET("Api_kategori/allKategori")
    fun getkategori():Call<ResponModel>

    @GET("Api_produk/allProdukLainnya")
    fun getproduklainnya():Call<ResponModel>

    @GET("Api_kategori/allProdukLainnya")
    fun getprodukkategori(
            @Query("produk_kategori") id: Int
    ):Call<ResponModel>

    @GET("province")
    fun getProvinsi(
            @Header("key") key: String
    ): Call<ResponModel>

    @GET("city")
    fun getKota(
            @Header("key") key: String,
            @Query("province") id: String
    ): Call<ResponModel>

    @FormUrlEncoded
    @POST("cost")
    fun ongkir(
            @Header("key") key: String,
            @Field("origin") origin: String,
            @Field("destination") destination: String,
            @Field("weight") weight: Int,
            @Field("courier") courier: String
    ):Call<ResponOngkir>
}