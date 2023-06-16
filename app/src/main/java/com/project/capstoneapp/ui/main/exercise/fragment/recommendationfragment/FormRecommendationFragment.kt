package com.project.capstoneapp.ui.main.exercise.fragment.recommendationfragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.capstoneapp.R
import com.project.capstoneapp.data.remote.request.RecommendationRequest
import com.project.capstoneapp.databinding.FragmentFormRecommendationBinding
import com.project.capstoneapp.ui.ViewModelFactory

class FormRecommendationFragment : Fragment() {
    private var _binding: FragmentFormRecommendationBinding? = null
    private val binding get() = _binding!!

    private var _formRecommendationViewModel: FormRecommendationViewModel? = null
    private val formRecommendationViewModel get() = _formRecommendationViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setListeners()
    }

    private fun setViewModel() {
        _formRecommendationViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[FormRecommendationViewModel::class.java]

        formRecommendationViewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        formRecommendationViewModel.recommendationResponse.observe(this) {
            if (it != null) {
                val listRecommendationFragment = ListRecommendationFragment()
                val bundle = Bundle()
                val duration = binding.edTime.text.toString().toInt()
                bundle.putInt(ListRecommendationFragment.EXERCISE_DURATION, duration)
                listRecommendationFragment.arguments = bundle

                parentFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.container_recommendation,
                        listRecommendationFragment,
                        ListRecommendationFragment::class.java.simpleName
                    )
                    commit()
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnOk.isEnabled = false

        binding.edCalories.addTextChangedListener(watcher)
        binding.edTime.addTextChangedListener(watcher)

        binding.btnOk.setOnClickListener {

            val calorie = binding.edCalories.text.toString().toIntOrNull() ?: 0
            val time = binding.edTime.text.toString().toIntOrNull() ?: 0

            var userWeight = 0
            formRecommendationViewModel.getLoginSession().observe(this) {
                userWeight = it.user?.weightKg!!.toInt()
            }

            val recommendationRequest = RecommendationRequest(calorie, time, userWeight)
            formRecommendationViewModel.getRecommendationList(recommendationRequest)
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.btnOk.isEnabled = !binding.edCalories.text.isNullOrBlank() && !binding.edTime.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}