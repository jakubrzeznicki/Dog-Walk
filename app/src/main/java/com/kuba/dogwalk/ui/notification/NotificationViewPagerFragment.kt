package com.kuba.dogwalk.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kuba.dogwalk.R
import com.kuba.dogwalk.databinding.FragmentNotificationViewPagerBinding
import com.kuba.dogwalk.other.Constants.TAB_CUSTOM_NOTIFICATION
import com.kuba.dogwalk.other.Constants.TAB_WALK_NOTIFICATION
import com.kuba.dogwalk.ui.notification.fragments.NotificationCustomFragment
import com.kuba.dogwalk.ui.notification.fragments.NotificationWalkFragment

class NotificationViewPagerFragment : Fragment(R.layout.fragment_notification_view_pager) {

    private var _binding: FragmentNotificationViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationViewPagerBinding.inflate(inflater, container, false)
        val view = binding.root

        val notificationFragmentList = arrayListOf<Fragment>(
            NotificationWalkFragment(),
            NotificationCustomFragment()
        )

        val notificationViewPagerAdapter = NotificationViewPagerAdapter(
            notificationFragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPagerNotifications.adapter = notificationViewPagerAdapter

        TabLayoutMediator(
            binding.tabLayoutNotifications, binding.viewPagerNotifications
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = TAB_WALK_NOTIFICATION
                }
                1 -> {
                    tab.text = TAB_CUSTOM_NOTIFICATION
                }
            }
        }.attach()

        return view
    }
}