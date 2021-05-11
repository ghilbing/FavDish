package com.hilbing.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hilbing.favdish.R
import com.hilbing.favdish.databinding.FragmentRandomDishBinding
import com.hilbing.favdish.viewmodel.NotificationsViewModel
import com.hilbing.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var mBinding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container,false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this@RandomDishFragment).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromAPI()
        randomDishViewModelObserver()
        
    }
    
    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(
            viewLifecycleOwner, 
            {randomDishResponse -> randomDishResponse?.let{
                Log.i("RANDOM DISH RESPONSE...", "${randomDishResponse.recipes[0]}")
            }}
        )
        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            {dataError -> dataError?.let {
                Log.i("RANDOM DISH ERROR....", "$dataError")
            }}
        )
        mRandomDishViewModel.loadRandomDish.observe(
            viewLifecycleOwner,
            { loadRandomDish -> loadRandomDish?.let {
                Log.i("RANDOM DISH LOADING...", "$loadRandomDish")
            }}
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}