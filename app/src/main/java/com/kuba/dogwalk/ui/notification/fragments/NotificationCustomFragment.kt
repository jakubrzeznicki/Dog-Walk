package com.kuba.dogwalk.ui.notification.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kuba.dogwalk.R
import com.kuba.dogwalk.adapters.CustomNotificationAdapter
import com.kuba.dogwalk.databinding.FragmentNotificationCustomBinding
import com.kuba.dogwalk.other.Constants
import com.kuba.dogwalk.other.Constants.NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE
import com.kuba.dogwalk.other.Constants.UNKNOWN_ERROR_MESSAGE
import com.kuba.dogwalk.other.Converters
import com.kuba.dogwalk.other.Status
import com.kuba.dogwalk.ui.notification.NotificationFragmentUtility
import com.kuba.dogwalk.ui.notification.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NotificationCustomFragment : Fragment(R.layout.fragment_notification_custom) {

    private var _binding: FragmentNotificationCustomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var customNotificationAdapter: CustomNotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationCustomBinding.inflate(inflater, container, false)
        val view = binding.root

        setInitialDate()
        subscribeObservers()
        setupRecyclerView()

        setDateOfCustomNotification()
        addCustomNotificationIntoDb()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObservers() {
        viewModel.notifications.observe(viewLifecycleOwner, Observer {
            customNotificationAdapter.customNotifications = it
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

    private fun setupRecyclerView() {
        binding.recyclerviewCustomNotification.apply {
            customNotificationAdapter = CustomNotificationAdapter(viewModel, requireContext())
            adapter = customNotificationAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


    private fun setDateOfCustomNotification() {
        binding.textViewDate.setOnClickListener {
            NotificationFragmentUtility()
                .setDatePickerDialog(requireContext(), binding.textViewDate)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setInitialDate() {
        val calendar = Calendar.getInstance()
        binding.textViewDate.text =
            SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_YEAR).format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    private fun addCustomNotificationIntoDb() {
        binding.apply {
            buttonAddCustomNotification.setOnClickListener {
                val date =
                    SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_YEAR).parse(textViewDate.text.toString())
                if (viewModel.insertCustomNotification(
                        date.time,
                        editTextNameOfCustomNotification.text.toString(),
                        context = requireContext(),
                    )
                ) {
                    textViewDate.text =
                        Converters().convertTimeInMillisIntoStringDate(Calendar.getInstance().timeInMillis)
                    editTextNameOfCustomNotification.setText("")
                }
            }
        }
    }
}