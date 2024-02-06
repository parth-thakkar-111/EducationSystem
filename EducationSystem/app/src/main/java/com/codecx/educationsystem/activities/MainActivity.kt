package com.codecx.educationsystem.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.codecx.educationsystem.R
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityEducatorBinding
import com.codecx.educationsystem.fragments.CoursesFragment
import com.codecx.educationsystem.fragments.LearnersFragment
import com.codecx.educationsystem.fragments.ProfileFragment

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityEducatorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEducatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.fragmentCourses
        selectFragment(CoursesFragment())
        }
        with(binding) {
            bottomNavigationView.itemIconTintList = null
            bottomNavigationView.setOnItemSelectedListener {
                val id = it.itemId
                when (id) {
                    R.id.fragmentCourses -> {
                        selectFragment(CoursesFragment())
                    }
                    R.id.fragmentProfile -> {
                        selectFragment(ProfileFragment())
                    }
                    R.id.fragmentUsers -> {
                        selectFragment(LearnersFragment())
                    }
                }
                true
            }
        }
    }

    private fun selectFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.mFragContainer, fragment).commit()
    }
}