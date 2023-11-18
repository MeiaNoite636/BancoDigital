package com.example.bancodigital.presenter.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.databinding.FragmentLoginBinding
import com.example.bancodigital.util.BaseFragment
import com.example.bancodigital.util.FirebaseHelper
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

    }


    private fun initListeners(){
        binding.btnLogin.setOnClickListener {
            validateData()
        }
        binding.btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnRecover.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverFragment)
        }
    }

    private fun validateData(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if(email.isNotEmpty()){
            if (password.isNotEmpty()){

                hideKeyboard()

                loginUser(email, password)

            }else{

                showBottomSheet(message = getString(R.string.text_password_empty))

            }
        }else{

            showBottomSheet(message = getString(R.string.text_email_empty))

        }
    }


    private fun loginUser(email: String, password: String) {

        loginViewModel.login(email, password).observe(viewLifecycleOwner) { stateView ->
            when(stateView){
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false

                    val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()

                    findNavController().navigate(R.id.action_global_homeFragment, null, navOptions)
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = getString(FirebaseHelper.validError(stateView.message ?: "")))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}