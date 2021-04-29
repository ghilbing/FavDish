package com.hilbing.favdish.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hilbing.favdish.R
import com.hilbing.favdish.databinding.ActivityAddUpdateBinding
import com.hilbing.favdish.databinding.DialogCustomImageSelectionBinding

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupActionBar()
        mBinding.ivAddDishImage.setOnClickListener(this)
    }

    private fun setupActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                }
            }
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener {
            Toast.makeText(this, "Camera Clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Toast.makeText(this, "Gallery Clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }




        dialog.show()

    }
}