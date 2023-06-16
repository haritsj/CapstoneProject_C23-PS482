package com.project.capstoneapp.ui.main.exercise.fragment

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dicoding.mystoryapp.utils.getDateWithUserLocaleFormat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.capstoneapp.R
import com.project.capstoneapp.data.remote.response.TrackingResponse
import com.project.capstoneapp.databinding.FragmentTrackingBinding
import com.project.capstoneapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private var trackingResponse = ArrayList<TrackingResponse>()
    private var exerciseList = ArrayList<String>()

    private var _trackingViewModel: TrackingViewModel? = null
    private val trackingViewModel get() = _trackingViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setViewModel()
        setListeners()
        getTrackingList()
        setSpinnerExercise()
        setExerciseHoursText()
    }

    private fun getTrackingList() {
        trackingViewModel.getTrackingList()
    }

    private fun setViewModel() {
        _trackingViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[TrackingViewModel::class.java]

        trackingViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        trackingViewModel.toastText.observe(viewLifecycleOwner){

        }

        trackingViewModel.trackingResponse.observe(viewLifecycleOwner){
            it?.let{
                trackingResponse = ArrayList(it)
                val exerciseNames: List<String?> =
                    it.map {exerciseList -> exerciseList.judul}.distinct()
                exerciseList = ArrayList(exerciseNames.filterNotNull())

                setSpinnerExercise()
            }
        }

        trackingViewModel.exerciseHistoryResponse.observe(viewLifecycleOwner) {
            it?.let {
                if (!it.id.isNullOrEmpty()) {
                    val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
                    val menuItemId = R.id.navigation_history
                    bottomNavigationView.selectedItemId = menuItemId
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnOk.isEnabled = false

            spinnerExercise.addTextChangedListener(watcher)
            edExerciseHours.addTextChangedListener(watcher)

            btnOk.setOnClickListener {
                if (spinnerExercise.text.toString().equals("Select Exercise")) {
                    Toast.makeText(requireContext(), "Please select an exercise to continue", Toast.LENGTH_SHORT).show()
                } else {
                    val jenis = "exercise"
                    val name = spinnerExercise.text.toString()
                    val duration = edExerciseHours.text.toString().toInt()
                    val calorie = trackingResponse.filter {
                        it.judul == name
                    }.map { it.calorie_per_kg }.first()
                    val createdAt = getDateWithUserLocaleFormat()
                    viewLifecycleOwner.lifecycleScope.launch {
                        trackingViewModel.addExerciseHistory(jenis, name, duration, calorie!!, createdAt)
                    }
                }
            }
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {
            // Do nothing
        }
    }

    private fun setSpinnerExercise() {
        binding.apply {
            spinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                exerciseList
            )
            spinnerExercise.setAdapter(spinnerAdapter)
        }


    }

    private fun setExerciseHoursText() {
        binding.apply {
            spinnerExercise.addTextChangedListener { exerciseName ->
                val filteredTrackingResponse = trackingResponse.filter { it.judul == exerciseName.toString().trim() }
                if (filteredTrackingResponse.isNotEmpty()) {
                    val intensityDescription = filteredTrackingResponse[0].intensity_description.toString()
                    if (intensityDescription.isNotEmpty() && intensityDescription != "null") {
                        tvExercise.text = exerciseName
                        tvExercise.visibility = View.VISIBLE
                        tvDescExercise.text = intensityDescription
                        tvDescExercise.visibility = View.VISIBLE
                    } else {
                        tvExercise.visibility = View.GONE
                        tvDescExercise.visibility = View.GONE
                    }
                } else {
                    tvExercise.visibility = View.GONE
                    tvDescExercise.visibility = View.GONE
                }
            }
        }
    }

    private fun setButtonEnabled() {
        binding.apply {
            btnOk.isEnabled = !edExerciseHours.text.isNullOrBlank() && !spinnerExercise.text.toString().equals("Select Exercise")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}