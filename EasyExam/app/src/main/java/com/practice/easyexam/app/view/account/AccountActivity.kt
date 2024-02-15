package com.practice.easyexam.app.view.account

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.practice.easyexam.R
import com.practice.easyexam.app.data.local.SharedPref
import com.practice.easyexam.app.view.account.editpass.EditPasswordActivity
import com.practice.easyexam.app.view.login.LoginActivity

class AccountActivity : AppCompatActivity() {

    private lateinit var myImageView: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvIDStudent: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvIDClass: TextView
    private lateinit var tvChangePassword: TextView
    private lateinit var tvLogout: TextView
    private var sharedPref: SharedPref? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        initView()

        tvChangePassword.setOnClickListener {
            val intent = Intent(this, EditPasswordActivity::class.java)
            startActivity(intent)
        }

        tvLogout.setOnClickListener {
            sharedPref?.clearSharedPref(this);
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initView() {
        myImageView = findViewById(R.id.myImageView)
        tvName = findViewById(R.id.tvName)
        tvIDStudent = findViewById(R.id.tvIDStudent)
        tvEmail = findViewById(R.id.tvEmail)
        tvIDClass = findViewById(R.id.tvIDClass)
        tvChangePassword = findViewById(R.id.tvChangePassword)
        tvLogout = findViewById(R.id.tvLogout)
        val toolbar: MaterialToolbar = findViewById(R.id.tool_bar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sharedPref = SharedPref.getInstance()
        var user = sharedPref?.getUser(this)
        tvName.text = user?.name
        tvEmail.text = user?.email
        tvIDStudent.text = user?.idStudent
        tvIDClass.text = user?.idClass
    }
}