package com.hilbing.favdish.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hilbing.favdish.R
import com.hilbing.favdish.databinding.ActivityAddUpdateBinding
import com.hilbing.favdish.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hilbing.favdish.application.FavDishApplication
import com.hilbing.favdish.databinding.DialogCustomListBinding
import com.hilbing.favdish.model.entities.FavDish
import com.hilbing.favdish.utils.Constants
import com.hilbing.favdish.view.adapters.CustomListItemAdapter
import com.hilbing.favdish.viewmodel.FavDishViewModel
import com.hilbing.favdish.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateBinding

    private var mImagePath: String = ""

    private lateinit var mCustomListDialog: Dialog

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()

        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)

        mBinding.ivAddDishImage.setOnClickListener(this@AddUpdateDishActivity)

        mBinding.btnAddDish.setOnClickListener(this@AddUpdateDishActivity)
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.iv_add_dish_image -> {

                customImageSelectionDialog()
                return
            }

            R.id.et_type -> {
                customItemsDialog(resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE)
                return
            }
            R.id.et_category -> {
                customItemsDialog(resources.getString(R.string.title_select_category),
                Constants.dishCategories(),
                Constants.DISH_CATEGORY)
                return
            }
            R.id.et_cooking_time -> {
                customItemsDialog(resources.getString(R.string.title_select_cooking_time),
                Constants.dishCookingTimes(),
                Constants.DISH_COOKING_TIME)
                return
            }

            R.id.btn_add_dish -> {
                val title = mBinding.etTitle.text.toString().trim() { it <= ' ' } //trimming empty spaces
                val type = mBinding.etType.text.toString().trim() { it <= ' ' }
                val category = mBinding.etCategory.text.toString().trim() { it <= ' ' }
                val ingredients = mBinding.etIngredients.text.toString().trim() { it <= ' ' }
                val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim() { it <= ' ' }
                val cookingDirection = mBinding.etDirectionToCook.text.toString().trim() { it <= ' ' }

                when {
                    TextUtils.isEmpty(mImagePath) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_select_dish_image), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(title) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_title), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(type) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_type), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(category) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_category), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(ingredients) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_ingredients), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingTimeInMinutes) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_cooking_time), Toast.LENGTH_SHORT).show()
                    }
                    TextUtils.isEmpty(cookingDirection) -> {
                        Toast.makeText(this@AddUpdateDishActivity, resources.getString(R.string.msg_enter_dish_cooking_instructions), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val favDishDetails: FavDish = FavDish(
                            mImagePath,
                            Constants.DISH_IMAGE_SOURCE_LOCAL,
                            title,
                            type,
                            category,
                            ingredients,
                            cookingTimeInMinutes,
                            cookingDirection,
                            false
                        )
                        
                        mFavDishViewModel.insert(favDishDetails)
                        Toast.makeText(this@AddUpdateDishActivity, "You successfully added your favorite dish details", Toast.LENGTH_SHORT).show()
                        Log.e("Insertion", "Success")
                        finish()
                    }
                }


            }
        }
    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            else -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {

                data?.extras?.let {
                    val thumbnail: Bitmap =
                        data.extras!!.get("data") as Bitmap // Bitmap from camera
                    //mBinding.ivDishImage.setImageBitmap(thumbnail) // Set to the imageView.
                    Glide.with(this).load(thumbnail)
                            .centerCrop()
                            .into(mBinding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.i("IMAGE", mImagePath)

                    // Replace the add icon with edit icon once the image is selected.
                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
            // TODO Step 3: Get the selected image from gallery. The selected will be in form of URI so set it to the Dish ImageView.
            // START
            else if (requestCode == GALLERY) {

                data?.let {
                    // Here we will get the select image URI.
                    val selectedPhotoUri = data.data

                    //mBinding.ivDishImage.setImageURI(selectedPhotoUri) // Set the selected image from GALLERY to imageView.
                    Glide.with(this)
                            .load(selectedPhotoUri)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                    Log.e("ERROR", "error loading image", e)
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    resource?.let{
                                        val bitmap: Bitmap = resource.toBitmap()
                                        mImagePath = saveImageToInternalStorage(bitmap)
                                        Log.i("IMAGE GALLERY", mImagePath)
                                    }
                                    return false
                                }

                            })
                            .into(mBinding.ivDishImage)



                    // Replace the add icon with edit icon once the image is selected.
                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddUpdateDishActivity,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
            // END
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function for ActionBar setup.
     */
    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }


    /**
     * A function to launch the custom image selection dialog.
     */
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)

        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {

            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Here after all the permission are granted launch the CAMERA to capture an image.
                        report?.let {
                            if (report.areAllPermissionsGranted()) {

                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()

           dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {

            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        // TODO Step 2: Launch the gallery for Image selection using the constant.
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            R.string.you_have_denied_permissions_for_using_the_camera,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalDialogForPermissions()
                    }
                })
                .onSameThread()
                .check()

            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }


    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(R.string.turned_off_permissions_message)
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun customItemsDialog(title: String, itemsList: List<String>, selection: String){
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, itemsList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()

    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"

    }
}