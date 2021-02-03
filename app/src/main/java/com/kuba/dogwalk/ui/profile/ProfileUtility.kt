package com.kuba.dogwalk.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.dog.Activity
import com.kuba.dogwalk.data.local.dog.Gender
import com.kuba.dogwalk.other.Constants
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class ProfileUtility {
    @SuppressLint("SetTextI18n")
    fun setMaterialDesignForEditTextDialog(
        context: Context,
        idOfLayout: Int,
        viewModel: ProfileViewModel,
        idOfField: Int,
        parse: Boolean,
        layout: Int,
        currentValue: String
    ) {
        val view = LayoutInflater.from(context).inflate(idOfLayout, null).apply {
            if (parse) {
                findViewById<EditText>(idOfField).setText("" + viewModel.dog.value?.weight)
            } else {
                findViewById<EditText>(idOfField).setText(currentValue)
            }
        }

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setNeutralButton(context.getString(R.string.cancel_label)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.save_label)) { _, _ ->
                if (parse) {
                    viewModel.updateDogWeight(
                        view.findViewById<EditText>(idOfField).text.toString()
                    )
                } else {
                    if (layout == 1) {
                        viewModel.updateDogName(view.findViewById<EditText>(idOfField).text.toString())
                    } else {
                        viewModel.updateDogBreed(view.findViewById<EditText>(idOfField).text.toString())
                    }
                }
                if (view.parent != null) {
                    (view.parent as ViewGroup).removeView(view)
                }
            }
            .show()
            .window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        view.findViewById<EditText>(idOfField).apply {
            isFocusableInTouchMode = true
            requestFocus()
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun setDatePickerDialog(context: Context, viewModel: ProfileViewModel) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_YEAR)
                val date = sdf.parse("$dayOfMonth.${month+1}.$year")
                viewModel.updateDogBirthDate(date.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun setCheckboxDialog(
        context: Context,
        viewModel: ProfileViewModel,
        items: Array<String>?,
        layout: Int
    ) {
        var item = ""
        val defaultValue = when (layout) {
            1 -> if (viewModel.dog.value?.gender == Gender.DOG) 0 else 1
            else -> when (viewModel.dog.value?.activity) {
                Activity.MEDIUM -> 1
                Activity.LOW -> 0
                else -> 2
            }
        }

        MaterialAlertDialogBuilder(context)
            .setNeutralButton(context.getString(R.string.cancel_label)) { _, _ ->

            }
            .setPositiveButton(context.getString(R.string.save_label)) { _, _ ->
                when (layout) {
                    1 -> viewModel.updateDogGender(if (item == context.getString(R.string.bitch_label)) Gender.BITCH else Gender.DOG)
                    else -> viewModel.updateDogActivity(
                        when (item) {
                            context.getString(R.string.normal_label) -> Activity.MEDIUM
                            context.getString(R.string.low_label) -> Activity.LOW
                            else -> Activity.HIGH
                        }
                    )

                }
            }
            .setSingleChoiceItems(
                items,
                defaultValue
            ) { dialog, which ->
                item = items?.get(which) ?: ""
            }
            .show()
    }


    fun hasLocationPermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
}
