package com.example.scoutotask.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.scoutotask.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding
    private lateinit var sharedPrefs : SharedPreferences

    companion object {
        fun newInstance() = MainFragment()
    }

    private var username = ""
    private var password = ""

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sharedPrefs = requireActivity().getSharedPreferences(getString(com.example.scoutotask.R.string.sharedPrefsUser), Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        initListeners()
        initObservers()
        initArguments()

        return binding.root
    }

    private fun initArguments() {
        username = sharedPrefs.getString(getString(com.example.scoutotask.R.string.prefUsername), "").toString()
        password = sharedPrefs.getString(getString(com.example.scoutotask.R.string.prefPassword), "").toString()

        if (username != "" && password != "") {
            viewModel.login(username, password, requireContext())
        }
    }

    private fun initListeners() {
        binding.buttonLogin.setOnClickListener {
            if(checkIfUserOrPassIsEmpty()) {
                Toast.makeText(context, "Username or Password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            username = binding.edittextUsername.text.toString()
            password = binding.edittextPassword.text.toString()

            viewModel.login(username, password, requireContext())
        }

        binding.buttonRegister.setOnClickListener {
            if(checkIfUserOrPassIsEmpty()) {
                Toast.makeText(context, "Username or Password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            username = binding.edittextUsername.text.toString()
            password = binding.edittextPassword.text.toString()

            viewModel.register(username, password, requireContext())
        }
    }

    private fun saveUsernameAndPassword(username : String, password : String) {
        val editor = sharedPrefs.edit()
        editor.putString(getString(com.example.scoutotask.R.string.prefUsername), username)
        editor.putString(getString(com.example.scoutotask.R.string.prefPassword), password)
        editor.apply()
    }

    private fun initObservers() {
        viewModel.userLoggedIn.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "User logged in", Toast.LENGTH_SHORT).show()
                saveUsernameAndPassword(username, password)
                goToDashboard()
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.userRegistered.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "User registered", Toast.LENGTH_SHORT).show()
                saveUsernameAndPassword(username, password)
                goToDashboard()
            } else {
                Toast.makeText(context, "User not registered", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToDashboard() {
        val newFragment = DashboardFragment.newInstance(viewModel.userId)
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.example.scoutotask.R.id.container, newFragment)
        fragmentTransaction.commit()
    }

    private fun checkIfUserOrPassIsEmpty() : Boolean {
        if (binding.edittextUsername.text.toString().isEmpty() || binding.edittextPassword.text.toString().isEmpty()) {
            return true
        }
        return false
    }

}