package com.example.marawisstore.app

import com.example.marawisstore.model.Checkout
import com.example.marawisstore.model.ResponModel
import com.example.marawisstore.model.rajaongkir.ResponOngkir
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("costumer/register")
    fun register(
        @Field("username") username : String,
        @Field("email") email : String,
        @Field("telpon") telpon : String,
        @Field("password") password : String
    ):Call<ResponModel>

    @FormUrlEncoded
    @POST("costumer/login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String
    ):Call<ResponModel>

    @POST("checkout")
    fun checkout(
            @Body data: Checkout
    ):Call<ResponModel>

    @GET("product/terkait")
    fun getProductTerkait():Call<ResponModel>

    @GET("product/terbaru")
    fun getProductTerbaru():Call<ResponModel>

    @GET("product/lainnya")
    fun getProductLainnya():Call<ResponModel>

    @GET("product/terbaruAll")
    fun getProductTerbaruAll():Call<ResponModel>

    @GET("product/lainnyaAll")
    fun getProductLainnyaAll():Call<ResponModel>

    @GET("checkout/user/{id}")
    fun getRiwayat(
            @Path("id") id: String
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