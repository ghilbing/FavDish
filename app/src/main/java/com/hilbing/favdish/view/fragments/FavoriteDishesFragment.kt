package com.hilbing.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hilbing.favdish.R
import com.hilbing.favdish.application.FavDishApplication
import com.hilbing.favdish.viewmodel.DashboardViewModel
import com.hilbing.favdish.viewmodel.FavDishViewModel
import com.hilbing.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    private val  mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorite_dishes, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner){
            dishes ->
            dishes.let{
                if(it.isNotEmpty()){
                    for(dish in it){
                        Log.i("Favorite Dishes", "${dish.id} :: ${dish.title}")
                    }
                } else {
                    Log.i("Favorites is empty", "empty: ")
                }
            }
        }
    }
}