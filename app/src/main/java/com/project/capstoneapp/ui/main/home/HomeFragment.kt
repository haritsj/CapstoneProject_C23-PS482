package com.project.capstoneapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.capstoneapp.R
import com.project.capstoneapp.adapter.HomeExerciseListAdapter
import com.project.capstoneapp.adapter.HomeFoodListAdapter
import com.project.capstoneapp.data.remote.response.HistoryResponse
import com.project.capstoneapp.databinding.FragmentHomeBinding
import com.project.capstoneapp.ui.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null
    private val homeViewModel get() = _homeViewModel!!

    private var _homeFoodListAdapter: HomeFoodListAdapter? = null
    private val homeFoodListAdapter get() = _homeFoodListAdapter!!

    private var _homeExerciseListAdapter: HomeExerciseListAdapter? = null
    private val homeExerciseListAdapter get() = _homeExerciseListAdapter!!

    private var historyData: List<HistoryResponse> = emptyList()

    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH) + 1
    private var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    private var eatenCalories: Double = 0.0
    private var burnedCalories: Double = 0.0

    private var calorieNeeds: Double = 0.0

    private var weightKg: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setContent()
        setAdapter()
        setViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setContent() {
        binding.tvTodayDate.text =
            getString(R.string.today).plus(" ${getDateWithUserLocaleFormat()}")
    }

    private fun setViewModel() {
        _homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]

        homeViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        homeViewModel.getLoginSession().observe(this) {
            if (it.isLogin) {
                binding.tvWelcomingText.text =
                    getString(R.string.welcoming_hello).plus(" ${it.user?.name}")
                weightKg = it.user!!.weightKg!!.toFloat()
                lifecycleScope.launch {
                    homeViewModel.getAllHistory(it.userId, it.token)
                    calorieNeeds = it.calorieNeeds!!.toDouble()
                    setDonutChart()
                }
            }
        }

        homeViewModel.toastText.observe(viewLifecycleOwner) {
            var toastText = ""
            when (it?.toString()) {
                "timeout" -> {
                    toastText = getString(R.string.scan_timeout)
                }
                "Unable to resolve host \"flask-c23-ps482-p7hhnsgwnq-uc.a.run.app\": No address associated with hostname" -> {
                    toastText = getString(R.string.scan_failed)
                }
            }
            if (toastText.isNotEmpty()) {
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.historyResponse.observe(viewLifecycleOwner) { data ->
            data?.let {
                historyData = it
                setupTableView()
            }
        }
    }

    private fun setupTableView() {
        setupFoodTable()
        setupExerciseTable()
        setDonutChart()
    }

    private fun setupFoodTable() {
        eatenCalories = 0.0
        val foodData = mutableListOf<HistoryResponse>()
        val selectedMonthText = if (month < 10) "0$month" else month
        val selectedDayText = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
        historyData.filter {
            val dateMonthFilter = it.createdAt?.split("/")
            it.jenis == "food" && dateMonthFilter?.get(0)?.trim()
                .orEmpty() == selectedMonthText.toString() && dateMonthFilter?.get(1)
                .orEmpty() == selectedDayText.toString()
        }.run {
            forEach {
                foodData.add(it)
            }
        }
        val model = ArrayList<HistoryResponse>()
        foodData.forEach {
            model.add(it)
            eatenCalories += it.calorie!!.toDouble()
        }

        homeFoodListAdapter.setFoodList(model)

        binding.tvEatenCalories.text = eatenCalories.toInt().toString()
    }

    private fun setupExerciseTable() {
        burnedCalories = 0.0
        val exerciseData = mutableListOf<HistoryResponse>()
        val selectedMonthText = if (month < 10) "0$month" else month
        val selectedDayText = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
        historyData.filter {
            val dateMonthFilter = it.createdAt?.split("/")
            it.jenis == "exercise" && dateMonthFilter?.get(0)?.trim()
                .orEmpty() == selectedMonthText.toString() && dateMonthFilter?.get(1)
                .orEmpty() == selectedDayText.toString()
        }.run {
            forEach {
                exerciseData.add(it)
            }
        }
        val model = ArrayList<HistoryResponse>()
        exerciseData.forEach {
            model.add(HistoryResponse(
                id = it.id.toString(),
                userId = it.userId.toString(),
                jenis = it.jenis.toString(),
                name = it.name.toString(),
                restaurant = it.restaurant.toString(),
                menu = it.menu.toString(),
                quantity = it.quantity,
                durasiMenit = it.durasiMenit,
                calorie = if (it.calorie!! < 1f) weightKg.times(it.calorie!!).times(it.durasiMenit!!.toFloat()) else it.calorie,
                createdAt = it.createdAt
            ))
            burnedCalories += if (it.calorie!! < 1f) weightKg.times(it.calorie!!).times(it.durasiMenit!!.toFloat()).toDouble() else it.calorie!!.toDouble()
        }
        homeExerciseListAdapter.setExerciseList(model)
        binding.tvBurnedCalories.text = burnedCalories.toInt().toString()
    }

    fun getDateWithUserLocaleFormat(): String {
        val currentDate = Date()
        // val userLocale = Locale.getDefault()
        val format = SimpleDateFormat("MMMM dd", Locale.US)
        return format.format(currentDate)
    }

    private fun setDonutChart() {
        binding.apply {
            val calorie = eatenCalories - burnedCalories
            val calorieNeeds = calorieNeeds
            tvCaloriesPercentage.text =
                StringBuilder("${calorie.roundToInt()}/${calorieNeeds.roundToInt()} Kcal")
            donutChart.setProgress(((calorie / calorieNeeds) * 100).toInt())
        }
    }


    private fun setAdapter() {
        setFoodListAdapter()
        setExerciseListAdapter()
    }

    private fun setFoodListAdapter() {
        binding.apply {
            _homeFoodListAdapter = HomeFoodListAdapter()

            val dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireContext()).orientation
            )

            rvFoodHome.apply {
                addItemDecoration(dividerItemDecoration)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeFoodListAdapter
            }
        }
    }

    private fun setExerciseListAdapter() {
        binding.apply {
            _homeExerciseListAdapter = HomeExerciseListAdapter()

            val dividerItemDecoration = DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireContext()).orientation
            )

            rvExerciseHome.apply {
                addItemDecoration(dividerItemDecoration)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeExerciseListAdapter
            }
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