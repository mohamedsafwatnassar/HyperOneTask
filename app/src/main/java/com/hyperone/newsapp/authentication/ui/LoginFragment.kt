package com.hyperone.newsapp.authentication.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hyperone.newsapp.R
import com.hyperone.newsapp.authentication.viewModel.AuthenticationViewModel
import com.hyperone.newsapp.base.BaseFragment
import com.hyperone.newsapp.databinding.FragmentLoginBinding
import com.hyperone.newsapp.utils.SharedPreferenceManager
import com.hyperone.newsapp.utils.extentions.customNavigate
import com.hyperone.newsapp.utils.extentions.onDebouncedListener
import com.paymob.moviesapp.network.DataHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AuthenticationViewModel by viewModels()

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBtnListeners()

        subscribeData()
    }

    private fun setBtnListeners() {
        binding.btnLogin.onDebouncedListener {
            hideKeyboard()
            if (validateAllInputs()) {
                viewModel.loginUser(email, password)
            }
        }

        binding.txtSignup.onDebouncedListener {
            findNavController().customNavigate(R.id.registerFragment)
        }

        binding.edtEmail.doAfterTextChanged {
            validateEmail()
        }

        binding.edtPassword.doAfterTextChanged {
            validatePassword()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().moveTaskToBack(true)
                }
            },
        )
    }

    private fun validateAllInputs(): Boolean {
        validateEmail()
        validatePassword()
        return validateEmail() && validatePassword()
    }

    private fun validatePassword(): Boolean {
        var isValid = true
        password = binding.edtPassword.text.toString().trim()

        if (password.isEmpty()) {
            binding.edtPassword.error = getString(R.string.required)
            isValid = false
        } else if (password.length < 8) {
            binding.edtPassword.error = "password should more than 8 chars"
            isValid = false
        }

        return isValid
    }


    private fun validateEmail(): Boolean {
        var isValid = true
        email = binding.edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.edtEmail.error = getString(R.string.required)
            isValid = false
        } else if (!validEmail()) {
            binding.edtEmail.error = getString(R.string.please_enter_a_valid_email)
            isValid = false
        }

        return isValid
    }

    private fun validEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun subscribeData() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
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
                    navigateToMainNewsScreen()
                    // Hide loading indicator
                    hideLoading()
                }

                is DataHandler.Error -> {
                    showToast("Error: ${result.message}")
                    // Hide loading indicator
                    hideLoading()
                }

                is DataHandler.ServerError -> {
                    showToast("Server Error: ${result.message}")
                    // Hide loading indicator
                    hideLoading()
                }
            }
        }
    }

    private fun navigateToMainNewsScreen() {
        // Navigate to the main news screen
        findNavController().customNavigate(R.id.newsFragment)
    }

    private fun handleNavigation() {
        if (sharedPreferenceManager.getLoggedInFLag()) {
            findNavController().customNavigate(R.id.newsFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        handleNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}