package com.project.capstoneapp.ui.main.exercise.fragment.recommendationfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.mystoryapp.utils.getDateWithUserLocaleFormat
import com.project.capstoneapp.R
import com.project.capstoneapp.adapter.RecommendationListAdapter
import com.project.capstoneapp.data.remote.response.RecommendationResponse
import com.project.capstoneapp.databinding.FragmentListRecommendationBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.main.exercise.fragment.ListRecommendationViewModel
import kotlinx.coroutines.launch

class ListRecommendationFragment : Fragment() {
    private var _binding: FragmentListRecommendationBinding? = null
    private val binding get() = _binding!!

    private lateinit var recommendationListAdapter: RecommendationListAdapter

    private var _formRecommendationViewModel: FormRecommendationViewModel? = null
    private val formRecommendationViewModel get() = _formRecommendationViewModel!!

    private var _listRecommendationViewModel: ListRecommendationViewModel? = null
    private val listRecommendationViewModel get() = _listRecommendationViewModel!!

    private var recommendationResponse = ArrayList<RecommendationResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setAdapter()
        setOnBackPressed()
    }

    private fun setViewModel() {
        _formRecommendationViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[FormRecommendationViewModel::class.java]

        formRecommendationViewModel.recommendationResponse.observe(this) {
            it?.let {
                recommendationResponse = ArrayList(it)
            }
        }

        _listRecommendationViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[ListRecommendationViewModel::class.java]

        listRecommendationViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun setAdapter() {
        formRecommendationViewModel.recommendationResponse.observe(viewLifecycleOwner) { recommendationResponse ->
            val recommendationList = ArrayList<RecommendationResponse>(recommendationResponse!!)

            recommendationListAdapter = RecommendationListAdapter(recommendationList)
            binding.rvRecommendation.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = recommendationListAdapter
            }
            recommendationListAdapter.setOnItemClickCallback(object :
                RecommendationListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: RecommendationResponse) {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle(data.exercise.toString())
                        .setMessage(getString(R.string.message_excercise))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            val jenis = "exercise"
                            val name = data.exercise.toString()
                            val duration = arguments?.getInt(EXERCISE_DURATION)!!.toInt()
                            val calorie = data.calorie
                            val createdAt = getDateWithUserLocaleFormat()
                            viewLifecycleOwner.lifecycleScope.launch {
                                listRecommendationViewModel.addExerciseHistory(
                                    jenis,
                                    name,
                                    duration,
                                    calorie!!,
                                    createdAt
                                )
                                findNavController().navigate(R.id.navigation_history)
                            }
                        }
                        .setNegativeButton(getString(R.string.negative_excercise)) { _, _ ->
                        }
                        .show()
                }
            })
        }
    }

    private fun setOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.container_recommendation,
                                FormRecommendationFragment(),
                                FormRecommendationFragment::class.java.simpleName
                            )
                            commit()
                        }
                    }
                }
            })
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXERCISE_DURATION = "exercise_duration"
    }
}