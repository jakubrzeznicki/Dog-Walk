package com.kuba.dogwalk.ui.mywalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.databinding.FragmentMyWalkDetailBinding
import com.kuba.dogwalk.other.Constants.DATE_FORMAT_DAY_MONTH_YEAR
import com.kuba.dogwalk.other.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MyWalkDetailFragment : Fragment(R.layout.fragment_my_walk_detail) {

    private var _binding: FragmentMyWalkDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyWalkViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMyWalkItem(MyWalkDetailFragmentArgs.fromBundle(requireArguments()).myItemId)
        subscribeObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyWalkDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMyWalkDetail)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }

        return view
    }

    private fun subscribeObservers() {
        viewModel.myWalkItem.observe(viewLifecycleOwner, Observer { myWalk ->
            updateMyWalkDetails(myWalk)
        })
    }

    private fun updateMyWalkDetails(myWalk: MyWalk?) {
        binding.apply {
            Glide.with(requireContext()).load(myWalk?.photo).into(imageViewMyDog)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = myWalk?.timestamp!!
            }
            val dateFormat = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR, Locale.getDefault())
            textViewDate.text = dateFormat.format(calendar.time)

            val distanceInKm = "${myWalk?.distance?.div(1000f)} km"
            textViewDistance.text = distanceInKm

            textViewTime.text = myWalk?.time?.let { TrackingUtility.getFormattedStopWatchTime(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}