package com.example.marawisstore.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.example.marawisstore.model.Produk

@Dao
interface DaoFavorit {
    @Insert(onConflict = REPLACE)
    fun insertfav(data: Produk)

    @Delete
    fun deletefav(data: Produk)

    @Update
    fun updatefav(data: Produk): Int

    @Query("SELECT * from keranjang ORDER BY id_produk ASC")
    fun getAllFav(): List<Produk>

    @Query("SELECT * FROM keranjang WHERE id_produk = :id LIMIT 1")
    fun getProdukFav(id: Int): Produk

    @Query("DELETE FROM keranjang")
    fun deleteAllFav(): Int
}