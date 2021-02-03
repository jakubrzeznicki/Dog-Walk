package com.kuba.dogwalk.ui.notification.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.notification.Notification
import com.kuba.dogwalk.databinding.FragmentWalkNotificationBinding
import com.kuba.dogwalk.other.Constants.DEFAULT_FIRST_HOUR
import com.kuba.dogwalk.other.Constants.DEFAULT_LAST_HOUR
import com.kuba.dogwalk.other.Constants.NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE
import com.kuba.dogwalk.other.Constants.TIME_FORMAT_HOUR_AND_MINUTES
import com.kuba.dogwalk.other.Constants.UNKNOWN_ERROR_MESSAGE
import com.kuba.dogwalk.other.Converters
import com.kuba.dogwalk.other.Status
import com.kuba.dogwalk.ui.notification.NotificationFragmentUtility
import com.kuba.dogwalk.ui.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class NotificationWalkFragment : Fragment(R.layout.fragment_walk_notification) {

    private var _binding: FragmentWalkNotificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkNotificationBinding.inflate(inflater, container, false)
        val view = binding.root

        subscribeObservers()
        enableOrDisableNotification()

        setHourOfFirstAndLastWalk(binding.textViewFirstWalkValue)
        setHourOfFirstAndLastWalk(binding.textViewLastWalkValue)

        binding.buttonSaveSettings.setOnClickListener {
            saveWalkNotificationIntoDatabase()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enableOrDisableNotification() {
        binding.switchNotificationEnable.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.walkNotification.value != null) {
                viewModel.updateWalkNotificationEnable(isChecked, 0, requireContext())
            }
            if (isChecked) {
                binding.constraintLayoutNotificationAll.visibility = View.VISIBLE
            } else {
                binding.constraintLayoutNotificationAll.visibility = View.GONE
            }
        }
    }

    private fun setHourOfFirstAndLastWalk(textView: MaterialTextView) {
        textView.setOnClickListener {
            NotificationFragmentUtility()
                .setHourOfWalkDialog(requireContext(), textView)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveWalkNotificationIntoDatabase() {
        val firstHour =
            SimpleDateFormat(TIME_FORMAT_HOUR_AND_MINUTES).parse(binding.textViewFirstWalkValue.text.toString())
        val endHour =
            SimpleDateFormat(TIME_FORMAT_HOUR_AND_MINUTES).parse(binding.textViewLastWalkValue.text.toString())
        viewModel.upsertWalkNotification(
            true,
            firstHour.time,
            endHour.time,
            binding.editTextSetGapWalk.text.toString().toInt(),
            requireContext()
        )
    }


    private fun subscribeObservers() {
        viewModel.walkNotification.observe(viewLifecycleOwner, Observer { notification ->
            if (notification != null) {
                updateNotificationView(notification)
            } else {
                binding.constraintLayoutNotificationAll.visibility = View.GONE
            }
        })

        viewModel.notificationStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        Snackbar.make(
                            requireView(),
                            result.message ?: NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE,
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

    private fun updateNotificationView(notification: Notification) {
        binding.apply {
            switchNotificationEnable.isChecked = notification.enabled
            constraintLayoutNotificationAll.visibility =
                if (notification.enabled) View.VISIBLE else View.GONE

            if (notification.enabled) {
                textViewLastWalkValue.text =
                    if (notification.lastHours != null) Converters().convertTimeInMillisIntoStringTime(
                        notification.lastHours!!
                    ) else DEFAULT_FIRST_HOUR
                textViewFirstWalkValue.text =
                    if (notification.firstHours != null) Converters().convertTimeInMillisIntoStringTime(
                        notification.firstHours!!
                    ) else DEFAULT_LAST_HOUR
                notification.gap?.let { editTextSetGapWalk.setText(it.toString()) }
            }
        }
    }
}