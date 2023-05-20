package com.appjoint2.moduleA.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appjoint2.moduleA.R

/**
 * @Author:JustGank
 * */
class ModuleAActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_module_a)

      val map=HashMap<String,String>()
      map.put("a","b");
      map.put("c","d");
  }

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, ModuleAActivity::class.java)
      context.startActivity(intent)
    }
  }
}