package com.project.capstoneapp.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.capstoneapp.ui.main.exercise.fragment.RecommendationFragment
import com.project.capstoneapp.ui.main.exercise.fragment.TrackingFragment

class ExercisePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = TrackingFragment()
            1 -> fragment = RecommendationFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}