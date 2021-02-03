package com.kuba.dogwalk.ui.mywalk


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kuba.dogwalk.R
import com.kuba.dogwalk.adapters.MyWalkAdapter
import com.kuba.dogwalk.databinding.FragmentMyWalkListBinding
import com.kuba.dogwalk.other.Constants
import com.kuba.dogwalk.other.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyWalkListFragment : Fragment(R.layout.fragment_my_walk_list) {

    private var _binding: FragmentMyWalkListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyWalkViewModel by viewModels()
    private lateinit var myWalkAdapter: MyWalkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWalkListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(
                MyWalkListFragmentDirections.actionMyWalkListFragmentToTrackingWalkFragment()
            )
        }

        setupRecyclerView()
        subscribeObservers()

        return view
    }

    private fun subscribeObservers() {
        viewModel.myWalkItems.observe(viewLifecycleOwner, Observer {
            myWalkAdapter.myWalks = it
        })

        viewModel.insertOrDeleteMyWalkStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        Snackbar.make(
                            requireView(),
                            result.message ?: Constants.WALK_SAVE_SUCCESSFULLY_MESSAGE,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Status.ERROR -> {
                        Snackbar.make(
                            requireView(),
                            result.message ?: Constants.UNKNOWN_ERROR_MESSAGE,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerviewMyWalk.apply {
            myWalkAdapter = MyWalkAdapter(requireContext(), viewModel)
            adapter = myWalkAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}