package com.kuba.dogwalk.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.notification.Notification
import com.kuba.dogwalk.databinding.ItemCustomNitificationBinding
import com.kuba.dogwalk.other.Constants.DATE_FORMAT_DAY_MONTH_YEAR
import com.kuba.dogwalk.ui.notification.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

class CustomNotificationAdapter(
    private val viewModel: NotificationViewModel,
    private val context: Context
) :
    RecyclerView.Adapter<CustomNotificationAdapter.CustomNotificationViewHolder>() {
    inner class CustomNotificationViewHolder(val binding: ItemCustomNitificationBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var customNotifications: List<Notification>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomNotificationViewHolder {
        val binding = ItemCustomNitificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomNotificationViewHolder, position: Int) {
        holder.binding.apply {
            val customNotification = customNotifications[position]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = customNotification.date!!
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault())
            textViewDate.text = dateFormat.format(calendar.time)

            textViewName.text = customNotification.message

            when (customNotification.enabled) {
                true -> {
                    toggleButtonTurnCustomNotification.check(R.id.button_turn_on_custom_notification)
                }
                false -> {
                    toggleButtonTurnCustomNotification.check(R.id.button_turn_off_custom_notification)
                }
            }
            turnOnOrOffCustomNotification(holder, customNotification)

            holder.itemView.setOnLongClickListener {
                showDeleteItemAlertDialog(customNotification)
            }
        }
    }

    private fun showDeleteItemAlertDialog(customNotification: Notification): Boolean {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.delete_item_title))
            .setMessage(context.resources.getString(R.string.delete_item_message))

            .setNegativeButton(context.resources.getString(R.string.cancel_label)) { _, _ ->
            }
            .setPositiveButton(context.resources.getString(R.string.yes_label)) { _, _ ->
                viewModel.deleteNotification(customNotification)
            }
            .show()
        notifyDataSetChanged()
        return true
    }

    override fun getItemCount(): Int = customNotifications.size

    private fun turnOnOrOffCustomNotification(
        holder: CustomNotificationViewHolder,
        notification: Notification
    ) {

        holder.apply {
            binding.buttonTurnOnCustomNotification.setOnClickListener {
                viewModel.updateCustomNotificationEnable(true, notification.id!!, context)
            }
            binding.buttonTurnOffCustomNotification.setOnClickListener {
                viewModel.updateCustomNotificationEnable(false, notification.id!!, context)
            }
        }
    }

}