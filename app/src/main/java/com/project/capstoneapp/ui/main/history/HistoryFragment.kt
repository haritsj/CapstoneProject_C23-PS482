package com.project.capstoneapp.ui.main.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.project.capstoneapp.databinding.FragmentHistoryBinding
import com.project.capstoneapp.ui.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var _historyViewModel: HistoryViewModel
    private val historyViewModel get() = _historyViewModel

    private var _homeFoodListAdapter: HomeFoodListAdapter? = null
    private val homeFoodListAdapter get() = _homeFoodListAdapter!!

    private var _homeExerciseListAdapter: HomeExerciseListAdapter? = null
    private val homeExerciseListAdapter get() = _homeExerciseListAdapter!!

    private var weightKg: Float = 0f

    private var historyData: List<HistoryResponse> = emptyList()

    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH) + 1
    private var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setViewModel()
        setAdapter()
        setupCalendar()
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendar() {
        val monthText = if (month < 10) "0$month" else month
        val dayText = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
        binding.spinnerMonth.setText("$monthText/$dayText/$year")
        binding.spinnerMonth.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    this.year = year
                    this.month = month + 1
                    this.dayOfMonth = dayOfMonth
                    val selectedMonthText = if (month < 9) "0${month + 1}" else (month + 1)
                    val selectedDayText = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
                    val selectedDate = "$selectedMonthText/$selectedDayText/$year"
                    binding.spinnerMonth.setText(selectedDate)
                    setupTableView()
                }, year, month - 1, dayOfMonth
            )
            datePickerDialog.show()
        }
    }

    private fun setViewModel() {
        _historyViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HistoryViewModel::class.java]

        historyViewModel.getLoginSession().observe(viewLifecycleOwner) {
            if (it != null) {
                lifecycleScope.launch {
                    historyViewModel.getAllHistory(it.userId, it.token)
                    weightKg = it.user?.weightKg!!.toFloat()
                    setupTableView()
                }
            } else {
                Toast.makeText(requireContext(), "Try to relogin!", Toast.LENGTH_SHORT).show()
            }
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        historyViewModel.toastText.removeObservers(this)
        try {
            historyViewModel.toastText.observe(viewLifecycleOwner) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        historyViewModel.historyActivityResponse.observe(viewLifecycleOwner) { data ->
            data?.let {
                historyData = it
                setupTableView()
            }
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

    private fun setupTableView() {
        setupFoodTable()
        setupExerciseTable()
    }

    private fun setupFoodTable() {
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
        }

        homeFoodListAdapter.setFoodList(model)
    }

    private fun setupExerciseTable() {
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
        }
        homeExerciseListAdapter.setExerciseList(model)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        historyViewModel.toastText.removeObservers(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}