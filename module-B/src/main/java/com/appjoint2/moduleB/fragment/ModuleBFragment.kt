package com.appjoint2.moduleB.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appjoint2.moduleB.R


/**
 * @Author:JustGank
 * */
class ModuleBFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_module_b, container, false)
  }

  companion object {
    fun newInstance(): ModuleBFragment {
      return ModuleBFragment();
    }
  }
}