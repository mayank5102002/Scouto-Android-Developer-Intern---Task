package com.example.scoutotask.ui.main

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scoutotask.R
import com.example.scoutotask.databinding.FragmentDashboardBinding
import com.example.scoutotask.utils.AppPermissions
import com.example.scoutotask.utils.CarsAdapter
import com.example.scoutotask.utils.ItemSpacingDecoration
import com.example.scoutotask.utils.RetrofitClient

class DashboardFragment : Fragment(), CarsAdapter.OnItemClickListener {

    private lateinit var binding : FragmentDashboardBinding
    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var viewModel: DashboardVM

    private lateinit var carMakeSpinner : Spinner
    private lateinit var carModelSpinner : Spinner

    private var carMakeSelected = ""
    private var carModelSelected = ""

    private var userId = -1

    private lateinit var adapter : CarsAdapter
    private lateinit var layoutManager : LinearLayoutManager
    private lateinit var recyclerView : RecyclerView

    private val permissionEnabled = MutableLiveData(false)

    private var idToChange = -1

    companion object {
        fun newInstance(userId : Int) : DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DashboardVM::class.java]
        sharedPrefs = requireActivity().getSharedPreferences(getString(R.string.sharedPrefsUser), Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        init()
        initListeners()
        initObservers()

        return binding.root
    }

    private fun init() {
        userId = arguments?.getInt("userId")!!
        viewModel.getCars(userId, requireContext())

        initAdapter()

        carMakeSpinner = binding.spnMake
        carModelSpinner = binding.spnModel
        viewModel.getMakes()
        initCarMakeSpinner(listOf("Select a car make"))
        initCarModelSpinner(listOf("Select a car model"))
    }

    private fun checkPermissions() {
        if (!AppPermissions.checkPermissions(requireContext())) {
            AppPermissions.requestPermissions(requireActivity())
        } else {
            permissionEnabled.value = true
        }
    }

    private fun initAdapter() {
        recyclerView = binding.rvCars
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        adapter = CarsAdapter(viewModel.carsList, this)
        recyclerView.adapter = adapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        recyclerView.addItemDecoration(ItemSpacingDecoration(spacingInPixels))
    }

    private fun initListeners() {
        binding.btnLogout.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            viewModel.closeDb()
            goToLogin()
        }
        binding.addCarBtn.setOnClickListener {
            if (carMakeSelected != "" && carModelSelected != "") {
                viewModel.addCar(userId, carMakeSelected, carModelSelected, requireContext())
            } else {
                Toast.makeText(requireContext(), "Please select a car make and model", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initObservers() {
        RetrofitClient.makesFetched.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.makesList = RetrofitClient.makesList!!
                viewModel.getMakesList()
            }
        }
        RetrofitClient.modelsFetched.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.modelsList = RetrofitClient.modelList!!
                viewModel.getModelsList()
            }
        }
        viewModel.listProcessed.observe(viewLifecycleOwner) {
            if (it) {
                initCarMakeSpinner(viewModel.makes)
            }
        }
        viewModel.modelListProcessed.observe(viewLifecycleOwner) {
            if (it) {
                initCarModelSpinner(viewModel.models)
            }
        }
        viewModel.carsListProcessed.observe(viewLifecycleOwner) {
            if (it) {
                updateCarsList()
            }
        }
        permissionEnabled.observe(viewLifecycleOwner) {
            if (it) {
                selectImage()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateCarsList() {
        adapter.dataList = viewModel.carsList
        adapter.notifyDataSetChanged()
    }

    private fun initCarMakeSpinner(dataList : List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dataList)

        carMakeSpinner = binding.spnMake
        carMakeSpinner.adapter = adapter

        carMakeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String

                if (selectedItem != "Select a car make") {
                    carMakeSelected = selectedItem
                    viewModel.getModel(viewModel.makeToIdMap[selectedItem]!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    private fun initCarModelSpinner(dataList : List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dataList)

        carModelSpinner = binding.spnModel
        carModelSpinner.adapter = adapter

        carModelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String

                if(selectedItem != "Select a car model") {
                    carModelSelected = selectedItem
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    private fun goToLogin() {
        val newFragment = MainFragment.newInstance()
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, newFragment)
        fragmentTransaction.commit()
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, AppPermissions.REQUEST_CODE)
    }

    override fun onImgBtnClick(position: Int) {
        idToChange = position
        checkPermissions()
    }

    override fun onDelBtnClick(position: Int) {
        viewModel.deleteCar(viewModel.carsList[position].id, userId, requireContext())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Get the selected image URI and do something with it
        if (requestCode == AppPermissions.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            // Do something with the selected image URI
            val car = viewModel.carsList[idToChange]
            viewModel.updateImg(car.id, car.carMake, car.carModel, selectedImage.toString(), requireContext(), userId)
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppPermissions.REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}