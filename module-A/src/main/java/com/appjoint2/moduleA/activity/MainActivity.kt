package com.appjoint2.moduleA.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.appjoint2.common.router.moduleB.ModuleBService
import com.appjoint2.moduleA.fragment.ModuleATabFragment
import com.appjoint2.core.AppJoint2

import com.appjoint2.moduleA.databinding.ActivityMainBinding

/**
 * @Author:JustGank
 * */
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titles = arrayOf(getString(com.appjoint2.common.R.string.moduleA), getString(com.appjoint2.common.R.string.moduleB))

        binding.viewpager.adapter =
            object : FragmentStateAdapter(supportFragmentManager, lifecycle) {

                override fun getItemCount(): Int = titles.size

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> ModuleATabFragment.newInstance()
                        1 -> AppJoint2.service(ModuleBService::class.java)?.moduleBTabFragment()!!
                        else -> throw IllegalArgumentException("Illegal adapter index $position")
                    }
                }
            }

        val mediator = TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab, position ->
            tab.text = titles[position]
        }

        mediator.attach()
    }
}
