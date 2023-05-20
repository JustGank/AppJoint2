package com.appjoint2.moduleB.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.util.Consumer
import com.appjjoint.moduleB.R
import com.appjjoint.moduleB.databinding.FragmentModuleBTabBinding
import com.appjoint2.common.router.app.AppService
import com.appjoint2.common.router.moduleA.ModuleAService
import com.appjoint2.core.AppJoint2

/**
 * @Author:JustGank
 * */
class ModuleBTabFragment : Fragment() {

    lateinit var binding: FragmentModuleBTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModuleBTabBinding.inflate(inflater)
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
            AppJoint2.service(AppService::class.java)?.callMethodAsyncOfApp {
                view.post { showToast("From app module: ${it?.data}") }
            }
        }


        // moduleA action setup

        binding.btnStartActivityOfModuleA.setOnClickListener {
            context?.let { context ->
                AppJoint2.service(ModuleAService::class.java)?.startActivityOfModuleA(
                    context
                )
            }
        }

        binding.btnGetFragmentOfModuleA.setOnClickListener {
            AppJoint2.service(ModuleAService::class.java)?.obtainFragmentOfModuleA()
                ?.let { fragment ->
                    childFragmentManager.beginTransaction()
                        .replace(
                            R.id.vModule1FragmentPlaceholder,
                            fragment
                        )
                        .commitAllowingStateLoss()
                }
        }

        binding.btnCallMethodSyncOfModuleA.setOnClickListener {
            showToast(
                "From moduleA: ${
                    AppJoint2.service(ModuleAService::class.java)?.callMethodSyncOfModuleA()
                }"
            )
        }

        binding.btnCallMethodAsyncOfModuleA.setOnClickListener { view ->
            AppJoint2.service(ModuleAService::class.java)?.callMethodAsyncOfModuleA(Consumer {
                view.post { showToast("From moduleA: ${it.data}") }
            })
        }

    }

    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(): ModuleBTabFragment {
            val fragment = ModuleBTabFragment()
            return fragment
        }
    }
}