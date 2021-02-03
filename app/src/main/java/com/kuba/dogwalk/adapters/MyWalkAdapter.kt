package com.kuba.dogwalk.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.databinding.ItemMyWalkBinding
import com.kuba.dogwalk.other.Constants.DATE_FORMAT_DAY_MONTH_YEAR
import com.kuba.dogwalk.other.TrackingUtility
import com.kuba.dogwalk.ui.mywalk.MyWalkListFragmentDirections
import com.kuba.dogwalk.ui.mywalk.MyWalkViewModel
import java.text.SimpleDateFormat
import java.util.*

class MyWalkAdapter(private val context: Context, private val viewModel: MyWalkViewModel) :
    RecyclerView.Adapter<MyWalkAdapter.WalkViewHolder>() {

    inner class WalkViewHolder(val binding: ItemMyWalkBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<MyWalk>() {
        override fun areItemsTheSame(oldItem: MyWalk, newItem: MyWalk): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MyWalk, newItem: MyWalk): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var myWalks: List<MyWalk>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkViewHolder {
        val binding = ItemMyWalkBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return WalkViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WalkViewHolder, position: Int) {

        holder.binding.apply {
            val myWalk = myWalks[position]
            Glide.with(holder.itemView).load(myWalk.photo).into(imageViewMyDog)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = myWalk.timestamp
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault())
            textViewDate.text = dateFormat.format(calendar.time)

            val distanceInKm = "${myWalk.distance / 1000f} km"
            textViewDistance.text = distanceInKm

            textViewTime.text = TrackingUtility.getFormattedStopWatchTime(myWalk.time)

            holder.itemView.setOnClickListener {
                val action =
                    MyWalkListFragmentDirections.actionMyWalkListFragmentToMyWalkDetailFragment(
                        myWalk.id!!
                    )
                it.findNavController().navigate(action)
            }

            holder.itemView.setOnLongClickListener {
                showDeleteItemAlertDialog(myWalk)
            }
        }

    }

    override fun getItemCount(): Int = myWalks.size

    private fun showDeleteItemAlertDialog(myWalk: MyWalk): Boolean {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.resources.getString(R.string.delete_item_title))
            .setMessage(context.resources.getString(R.string.delete_item_message))

            .setNegativeButton(context.resources.getString(R.string.cancel_label)) { _, _ ->
            }
            .setPositiveButton(context.resources.getString(R.string.yes_label)) { _, _ ->
                viewModel.deleteMyWalkItem(myWalk)
            }
            .show()
        notifyDataSetChanged()
        return true
    }
}