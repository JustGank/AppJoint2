package com.appjoint2.moduleB.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appjoint2.moduleB.R


/**
 * @Author:JustGank
 * */
class ModuleBActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_module_b)
  }

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, ModuleBActivity::class.java)
      context.startActivity(intent)
    }
  }
}