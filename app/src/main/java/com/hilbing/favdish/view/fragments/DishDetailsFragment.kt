package com.hilbing.favdish.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hilbing.favdish.R
import com.hilbing.favdish.application.FavDishApplication
import com.hilbing.favdish.databinding.FragmentDishDetailsBinding
import com.hilbing.favdish.viewmodel.FavDishViewModel
import com.hilbing.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOError
import java.io.IOException
import java.util.*


class DishDetailsFragment : Fragment() {
    private var mBinding: FragmentDishDetailsBinding? = null

    // TODO Step 5: Create an ViewModel instance to access the methods.
    // START
    /**
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }
    // END

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()

        args.let {

            try {
                // Load the dish image in the ImageView.
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            // log exception
                            Log.e("TAG", "Error loading image", e)
                            return false // important to return false so the error placeholder can be placed
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            Palette.from(resource.toBitmap())
                                .generate { palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                                }
                            return false
                        }
                    })
                    .into(mBinding!!.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text =
                it.dishDetails.type.capitalize(Locale.ROOT) // Used to make first letter capital
            mBinding!!.tvCategory.text = it.dishDetails.category
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)

            // TODO Step 10: Set the favorite icon based on the value.
            // START
            if (args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
            }
            // END
        }

        // TODO Step 4: Assign the event to the favorite button.
        // START
        mBinding!!.ivFavoriteDish.setOnClickListener {

            // TODO Step 6: Update the favorite dish variable based on the current selection. i.e If it true then make it false vice-versa.
            // START
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            // END

            // TODO Step 7: Pass the updated values to ViewModel
            // START
            mFavDishViewModel.update(args.dishDetails)
            // END

            // TODO Step 8: Update the icons and display the toast message accordingly.
            // START
            if (args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_removed_from_favorite),
                    Toast.LENGTH_SHORT
                ).show()
            }
            // END
        }
        // END
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}