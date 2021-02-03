package com.kuba.dogwalk.ui.profile

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.dog.Dog
import com.kuba.dogwalk.data.local.dog.Gender
import com.kuba.dogwalk.data.local.dog.Goal
import com.kuba.dogwalk.databinding.FragmentProfileBinding
import com.kuba.dogwalk.other.Constants.DATA_STORAGE_TYPE
import com.kuba.dogwalk.other.Constants.IMAGE_PICK_CODE
import com.kuba.dogwalk.other.Constants.PERMISSION_CAMERA_STORAGE_MESSAGE
import com.kuba.dogwalk.other.Constants.PROFILE_SUCCESSFULLY_UPDATE
import com.kuba.dogwalk.other.Constants.REQUEST_CODE_STORAGE_IMAGE_PERMISSION
import com.kuba.dogwalk.other.Constants.UNKNOWN_ERROR_MESSAGE
import com.kuba.dogwalk.other.Converters
import com.kuba.dogwalk.other.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile),
    EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var genderItems: Array<String>? = null
    private var activityItems: Array<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        genderItems = resources.getStringArray(R.array.gender_array)
        activityItems = resources.getStringArray(R.array.activity_array)
        subscribeObservers()

        editNameOfDog()
        editBreedOfDog()
        editGenderOfDog()
        editBirthDateOfDog()
        editWeightOfDog()
        editPhotoOfDog()
        editActivityOfDog()

        return view
    }


    private fun subscribeObservers() {
        viewModel.dog.observe(viewLifecycleOwner, Observer { dog ->
            if (dog == null) {
                insertFirstItemIfDbIsEmpty()
            }
            updateDogInformationDetail(dog)
        })

        viewModel.updateDogStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        Snackbar.make(
                            requireView(),
                            result.message ?: PROFILE_SUCCESSFULLY_UPDATE,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Status.ERROR -> {
                        Snackbar.make(
                            requireView(),
                            result.message ?: UNKNOWN_ERROR_MESSAGE,
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    Status.LOADING -> {
                        /* NO-OP*/
                    }
                }
            }
        })
    }

    private fun insertFirstItemIfDbIsEmpty() {
        viewModel.insertDog(
            Dog(
                getString(R.string.first_name_label),
                getString(R.string.breed_label),
                Gender.DOG,
                Calendar.getInstance().timeInMillis,
                0.0,
                null ,
                Goal.HOLD,
                com.kuba.dogwalk.data.local.dog.Activity.MEDIUM,
                0,
                0
            )
        )

    }

    private fun updateDogInformationDetail(dog: Dog?) {
        binding.apply {
            if (dog?.photo.isNullOrEmpty()) {
                val bitmap = (binding.imageViewMainPhoto.drawable as BitmapDrawable).bitmap
                Glide.with(requireContext()).load(bitmap).into(imageViewMainPhoto)
            } else {
                Glide.with(requireContext()).load(Uri.parse(dog?.photo)).into(imageViewMainPhoto)
            }
            textViewNameProfile.text = dog?.name
            textViewBreedProfile.text = dog?.breed
            textViewGenderProfile.text =
                if (dog?.gender == Gender.DOG) getString(R.string.dog_label) else getString(
                    R.string.bitch_label
                )
            textViewBirthdayProfile.text = dog?.birthDate?.let {
                Converters().convertTimeInMillisIntoStringDate(
                    it
                )
            }
            textViewWeightProfile.text = "${dog?.weight.toString()} kg"
            textViewActivityProfile.text =
                when (dog?.activity) {
                    com.kuba.dogwalk.data.local.dog.Activity.MEDIUM -> getString(R.string.normal_label)
                    com.kuba.dogwalk.data.local.dog.Activity.LOW -> getString(R.string.low_label)
                    else -> getString(R.string.high_label)
                }
        }
    }


    private fun editNameOfDog() {
        binding.constraintLayoutNameProfile.setOnClickListener {
            ProfileUtility().setMaterialDesignForEditTextDialog(
                requireContext(),
                R.layout.dog_name,
                viewModel,
                R.id.edit_text_dog_name_edit,
                parse = false,
                1,
                binding.textViewNameProfile.text.toString()
            )
        }
    }

    private fun editBreedOfDog() {
        binding.constraintLayoutBreedProfile.setOnClickListener {
            ProfileUtility().setMaterialDesignForEditTextDialog(
                requireContext(),
                R.layout.dog_breed,
                viewModel,
                R.id.edit_text_dog_breed_edit,
                parse = false,
                2,
                binding.textViewBreedProfile.text.toString()
            )
        }
    }

    private fun editGenderOfDog() {
        binding.constraintLayoutGenderProfile.setOnClickListener {
            ProfileUtility().setCheckboxDialog(requireContext(), viewModel, genderItems, 1)
        }
    }

    private fun editActivityOfDog() {
        binding.constraintLayoutActivityProfile.setOnClickListener {
            ProfileUtility().setCheckboxDialog(requireContext(), viewModel, activityItems, 3)
        }
    }


    private fun editBirthDateOfDog() {
        binding.constraintLayoutBirthdayProfile.setOnClickListener {
            ProfileUtility().setDatePickerDialog(requireContext(), viewModel)
        }
    }

    private fun editWeightOfDog() {
        binding.constraintLayoutWeightProfile.setOnClickListener {
            ProfileUtility().setMaterialDesignForEditTextDialog(
                requireContext(),
                R.layout.dog_weight,
                viewModel,
                R.id.edit_text_dog_weight_edit,
                parse = true,
                3,
                binding.textViewWeightProfile.text.toString()
            )
        }
    }


    private fun editPhotoOfDog() {
        binding.floatingActionBarSelectImage.setOnClickListener {
            MainScope().launch {
                requestPermissions()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = DATA_STORAGE_TYPE
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.imageViewMainPhoto.setImageURI(data?.data)
            val bitmap = (binding.imageViewMainPhoto.drawable as BitmapDrawable).bitmap
            viewModel.updateDogPhoto(bitmap.saveImage(requireContext()).toString())
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            requestPermissions()
        }
    }


    private fun Bitmap.saveImage(context: Context): Uri? {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/dogWalkPictures")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_${SystemClock.uptimeMillis()}")

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(this, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
                return uri
            }
        } else {
            val directory =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + separator + "dogWalkPictures"
                )
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "img_${SystemClock.uptimeMillis()}" + ".jpeg"
            val file = File(directory, fileName)
            saveImageToStream(this, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                return Uri.fromFile(file)
            }
        }
        return null
    }


    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @AfterPermissionGranted(REQUEST_CODE_STORAGE_IMAGE_PERMISSION)
    private fun requestPermissions() {
        if (ProfileUtility().hasLocationPermissions(requireContext())) {
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                this,
                PERMISSION_CAMERA_STORAGE_MESSAGE,
                REQUEST_CODE_STORAGE_IMAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}