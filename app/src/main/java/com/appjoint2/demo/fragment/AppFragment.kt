package com.appjoint2.demo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appjoint2.demo.R

/**
 * @Author:JustGank
 * */
class AppFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_app, container, false)
  }

  companion object {
    fun newInstance(): AppFragment {
      return AppFragment();
    }
  }
}