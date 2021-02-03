package com.kuba.dogwalk.ui.caloriesCalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kuba.dogwalk.R
import com.kuba.dogwalk.data.local.dog.Activity
import com.kuba.dogwalk.data.local.dog.Dog
import com.kuba.dogwalk.data.local.dog.Goal
import com.kuba.dogwalk.databinding.FragmentCaloriesCalculatorBinding
import com.kuba.dogwalk.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.pow
import kotlin.math.roundToInt

@AndroidEntryPoint
class CaloriesCalculatorFragment : Fragment(R.layout.fragment_calories_calculator) {

    private var _binding: FragmentCaloriesCalculatorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaloriesCalculatorBinding.inflate(inflater, container, false)
        val view = binding.root

        subscribeObservers()
        setGoalOfCalories()
        calculateCountOfCalories()

        return view
    }


    private fun subscribeObservers() {
        viewModel.dog.observe(viewLifecycleOwner, Observer { dog ->
            if (checkThatAllDataToCalculateCaloriesIsInDb()) {
                updateDogInformationDetail(dog)
            } else {
                binding.apply {
                    constraintLayoutInformationFillFields.visibility = View.VISIBLE
                    constraintLayoutCalories.visibility = View.GONE
                }
            }
        })
    }

    private fun checkThatAllDataToCalculateCaloriesIsInDb(): Boolean {
        if (viewModel.dog.value != null) {
            if (viewModel.dog.value!!.weight != 0.0) {
                return true
            }
        }
        return false
    }

    private fun updateDogInformationDetail(dog: Dog) {
        binding.apply {
            constraintLayoutInformationFillFields.visibility = View.GONE
            constraintLayoutCalories.visibility = View.VISIBLE
            textViewCalories.text =
                if (dog.calories != 0) "${dog.calories} kcal" else ""
            if (viewModel.dog.value?.goal != null) {
                when (viewModel.dog.value?.goal) {
                    Goal.LOSS -> toggleButtonGoal.check(R.id.buttonLossWeight)
                    Goal.HOLD -> toggleButtonGoal.check(R.id.buttonHoldWeight)
                    else -> toggleButtonGoal.check(R.id.buttonGainWeight)
                }
            } else {
                toggleButtonGoal.check(R.id.buttonHoldWeight)
            }
        }
    }

    private fun calculateCountOfCalories() {
        binding.buttonCountCalories.setOnClickListener {
            if (viewModel.dog.value?.goal != null) {
                val restingEnergyRequirements = 70 * (viewModel.dog.value?.weight!!).pow(0.75)
                val goal = when (viewModel.dog.value!!.goal) {
                    Goal.HOLD -> 1.4
                    Goal.LOSS -> 1.0
                    else -> 1.7
                }
                val activity = when (viewModel.dog.value!!.activity) {
                    Activity.LOW -> goal - 0.2
                    Activity.MEDIUM -> goal
                    else -> goal + 0.1
                }
                viewModel.updateDogCalories((restingEnergyRequirements * activity).roundToInt())
            }
        }
    }

    private fun setGoalOfCalories() {
        binding.toggleButtonGoal.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonLossWeight -> {
                        viewModel.updateDogCaloriesGoal(Goal.LOSS)
                    }
                    R.id.buttonHoldWeight -> {
                        viewModel.updateDogCaloriesGoal(Goal.HOLD)
                    }
                    else -> {
                        viewModel.updateDogCaloriesGoal(Goal.GAIN)
                    }
                }
            }
        }

    }
}


