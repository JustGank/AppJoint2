package com.appjoint2.moduleA.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appjoint2.common.router.app.AppService
import com.appjoint2.common.router.moduleB.ModuleBService

import com.appjoint2.core.AppJoint2
import com.appjoint2.moduleA.R
import com.appjoint2.moduleA.databinding.FragmentModuleATabBinding

/**
 * @Author:JustGank
 * */
class ModuleATabFragment : Fragment() {

    lateinit var binding: FragmentModuleATabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModuleATabBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartActivityOfApp.setOnClickListener {
            context?.let { it1 -> AppJoint2.service(AppService::class.java)?.startActivityOfApp(it1) }
        }

        binding.btnGetFragmentOfApp.setOnClickListener {
            AppJoint2.service(AppService::class.java)?.obtainFragmentOfApp()?.let { fragment ->
                childFragmentManager.beginTransaction()
                    .replace(R.id.vAppFragmentPlaceholder, fragment)
                    .commitAllowingStateLoss()
            }
        }

        binding.btnCallMethodSyncOfApp.setOnClickListener {
            showToast(
                "From app module: ${
                    AppJoint2.service(AppService::class.java)?.callMethodSyncOfApp()
                }"
            )
        }

        binding.btnCallMethodAsyncOfApp.setOnClickListener { view ->
            AppJoint2.service(AppService::class.java)?.callMethodAsyncOfApp { appEntry ->
                view.post { showToast("From app module: ${appEntry?.data}") }
            }
        }

        // moduleB action setup

        binding.btnStartActivityOfModuleB.setOnClickListener {
            AppJoint2.service(ModuleBService::class.java)?.startActivityOfModuleB(context)
        }

        binding.btnGetFragmentOfModuleB.setOnClickListener {
            AppJoint2.service(ModuleBService::class.java)?.obtainFragmentOfModuleB()
                ?.let { fragment ->
                    childFragmentManager.beginTransaction()
                        .replace(R.id.vModuleBFragmentPlaceholder, fragment)
                        .commitAllowingStateLoss()
                }
        }

        binding.btnCallMethodSyncOfModuleB.setOnClickListener {
            showToast(
                "From moduleB: ${
                    AppJoint2.service(ModuleBService::class.java)?.callMethodSyncOfModuleB()
                }"
            )
        }

        binding.btnCallMethodAsyncOfModuleB.setOnClickListener { view ->
            AppJoint2.service(ModuleBService::class.java)?.callMethodAsyncOfModuleB {
                view.post { showToast("From moduleB: ${it?.data}") }
            }
        }

    }

    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): ModuleATabFragment {
            val fragment = ModuleATabFragment()
            return fragment
        }
    }
}