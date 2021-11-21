package com.example.marawisstore.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marawisstore.fragment.HomeFragment
import com.example.marawisstore.model.Produk

@Database(entities = [Produk::class] /* List model Ex:NoteModel */, version = 1)
abstract class MyDatabaseFavorit : RoomDatabase() {
    abstract fun daoFavorit(): DaoFavorit // DaoNote

    companion object {
        private var INSTANCE: MyDatabaseFavorit? = null

        fun getInstance(context: Context): MyDatabaseFavorit? {
            if (INSTANCE == null) {
                synchronized(MyDatabaseFavorit::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabaseFavorit::class.java, "MyDatabase14" // Database Name
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}