package com.hilbing.favdish.application

import android.app.Application
import com.hilbing.favdish.model.database.FavDishRepository
import com.hilbing.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application(){

    private val database by lazy {
        FavDishRoomDatabase.getDatabase(this@FavDishApplication)
    }

    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }
}