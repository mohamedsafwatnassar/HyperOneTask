package com.hyperone.newsapp.authentication.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyperone.newsapp.R
import com.hyperone.newsapp.authentication.viewModel.AuthenticationViewModel
import com.hyperone.newsapp.base.BaseFragment
import com.hyperone.newsapp.databinding.FragmentRegisterBinding
import com.hyperone.newsapp.utils.extentions.customNavigate
import com.hyperone.newsapp.utils.extentions.onDebouncedListener
import com.paymob.moviesapp.network.DataHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AuthenticationViewModel by viewModels()

    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBtnListeners()

        subscribeData()
    }

    private fun setBtnListeners() {
        binding.edtUserName.doAfterTextChanged {
            validateUserName()
        }

        binding.edtEmail.doAfterTextChanged {
            validateEmail()
        }

        binding.edtPassword.doAfterTextChanged {
            validatePassword()
        }

        binding.btnSignup.onDebouncedListener {
            hideKeyboard()
            if (validateAllInputs()) {
                viewModel.registerUser(username, email, password)
            }
        }

        binding.txtLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun validateAllInputs(): Boolean {
        validateUserName()
        validateEmail()
        validatePassword()
        return (validateUserName() && validateEmail() && validatePassword())
    }

    private fun validateUserName(): Boolean {
        username = binding.edtUserName.text.toString().trim()
        var isValid = true
        if (username.isEmpty()) {
            binding.edtUserName.error = getString(R.string.required)
            isValid = false
        } else {
            binding.edtEmail.error = null
            isValid = false
        }
        return isValid
    }

    private fun validateEmail(): Boolean {
        email = binding.edtEmail.text.toString().trim()
        var isValid = true
        if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.required)
            isValid = false
        } else if (!validEmail()) {
            binding.edtEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.edtEmail.error = null
            isValid = false
        }
        return isValid
    }

    private fun validatePassword(): Boolean {
        password = binding.edtPassword.text.toString().trim()
        var isValid = true
        if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.required)
            isValid = false
        } else if (password.length < 8) {
            binding.edtPassword.error = "password should be more than 8 characters"
            isValid = false
        } else {
            binding.edtEmail.error = null
            isValid = false
        }
        return isValid
    }

    private fun validEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun subscribeData() {
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DataHandler.ShowLoading -> {
                    // Show loading indicator
                    showLoading()
                }

                is DataHandler.HideLoading -> {
                    // Hide loading indicator
                    hideLoading()
                }

                is DataHandler.Success -> {
                    showToast(result.data)
                    // navigate to main news screen.
                    navigateToLoginScreen()
                }

                is DataHandler.Error -> {
                    showToast("Error: ${result.message}")
                }

                is DataHandler.ServerError -> {
                    showToast("Server Error: ${result.message}")
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        // Navigate to the main news screen
        findNavController().customNavigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}