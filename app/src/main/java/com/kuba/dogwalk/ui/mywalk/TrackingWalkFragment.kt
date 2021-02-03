package com.kuba.dogwalk.ui.mywalk

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.databinding.FragmentTrackingWalkBinding
import com.kuba.dogwalk.other.Constants
import com.kuba.dogwalk.other.Constants.ACTION_PAUSE_SERVICE
import com.kuba.dogwalk.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.kuba.dogwalk.other.Constants.ACTION_STOP_SERVICE
import com.kuba.dogwalk.other.Constants.MAP_ZOOM
import com.kuba.dogwalk.other.Constants.PERMISSION_MESSAGE_LOCATION
import com.kuba.dogwalk.other.Constants.POLYLINE_COLOR
import com.kuba.dogwalk.other.Constants.POLYLINE_SIZE
import com.kuba.dogwalk.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.kuba.dogwalk.other.Constants.WALK_SAVE_SUCCESSFULLY_MESSAGE
import com.kuba.dogwalk.other.TrackingUtility
import com.kuba.dogwalk.services.Polyline
import com.kuba.dogwalk.services.TrackingService
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class TrackingWalkFragment : Fragment(R.layout.fragment_tracking_walk),
    EasyPermissions.PermissionCallbacks {

    private val viewModel: MyWalkViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var currentTimeInMillis = 0L
    private var currentDistance = 0L

    private var _binding: FragmentTrackingWalkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingWalkBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMyTrackingWalk)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = ""
        }
        requestPermissions()
        subscribeToObservers()

        binding.mapView.onCreate(savedInstanceState)

        binding.buttonStart.setOnClickListener {
            buttonRun()
        }

        binding.buttonEnd.setOnClickListener {
            zoomToSeeWholeTrack()
            endWalkAndSaveDataToDatabase()
        }
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis)
            binding.textViewCurrentTime.text = formattedTime
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            currentDistance = 0
            pathPoints = it
            if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
                for (polyline in pathPoints) {
                    currentDistance += TrackingUtility.calculatePolylineLength(polyline).toInt()
                    binding.textViewCurrentDistance.text = "${currentDistance.div(1000f)}"
                }
            }
            addLatestPolyline()
            moveCameraToUser()
        })


    }

    private fun buttonRun() {
        if (requestPermissions()) {
            if (isTracking) {
                sendCommandToService(ACTION_PAUSE_SERVICE)
            } else {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }
        }

    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            binding.buttonStart.text = getString(R.string.start_label)
            if (TrackingService.timeRunInMillis.value == 0L) {
                binding.buttonEnd.visibility = View.GONE
            } else {
                binding.buttonEnd.visibility = View.VISIBLE
            }
        } else {
            binding.buttonStart.text = getString(R.string.stop_label)
            binding.buttonEnd.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            val bounds = LatLngBounds.Builder()
            for (polyline in pathPoints) {
                for (pos in polyline) {
                    bounds.include(pos)
                }
            }
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    binding.mapView.width,
                    binding.mapView.height,
                    (binding.mapView.height * 0.05f).toInt()
                )
            )
        }

    }

    private fun endWalkAndSaveDataToDatabase() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val myWalk = MyWalk(bmp, distanceInMeters, currentTimeInMillis, dateTimestamp)
            viewModel.insertMyWalkItemIntoDb(myWalk)
            binding.buttonEnd.visibility = View.GONE
            stopWalk()
        }
    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_SIZE)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }

    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_SIZE)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun stopWalk() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingWalkFragment_to_myWalkListFragment)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
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


    @AfterPermissionGranted(REQUEST_CODE_LOCATION_PERMISSION)
    private fun requestPermissions(): Boolean {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return true
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                PERMISSION_MESSAGE_LOCATION,
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                PERMISSION_MESSAGE_LOCATION,
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return false
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