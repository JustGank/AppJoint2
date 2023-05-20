package com.appjoint2.moduleA.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appjoint2.moduleA.R

/**
 * @Author:JustGank
 * */
class ModuleAFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_module_a, container, false)
  }

  companion object {
    fun newInstance(): ModuleAFragment {
      return ModuleAFragment();
    }
  }
}