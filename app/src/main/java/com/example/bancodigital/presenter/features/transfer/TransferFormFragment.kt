package com.example.bancodigital.presenter.features.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.databinding.FragmentTransferFormBinding
import com.example.bancodigital.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferFormFragment : BaseFragment() {
    private var _binding: FragmentTransferFormBinding? = null
    private val binding get() = _binding!!

    private val args: TransferFormFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar, light = true)

        initListeners()
    }

    private fun initListeners() {
        with(binding.editAmount) {
            addTextChangedListener(MoneyTextWatcher(this))

            addTextChangedListener {
                if (MoneyTextWatcher.getValueUnMasked(this) > 99.999f) {
                    this.setText("R$ 0,00")
                }

                doAfterTextChanged {
                    this.text?.length?.let {
                        this.setSelection(it)
                    }
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            validateAmount()
        }
    }

    private fun validateAmount() {
        val amount = MoneyTextWatcher.getValueUnMasked(binding.editAmount)

        if (amount > 0f) {

            hideKeyboard()

            val action =
                TransferFormFragmentDirections.actionTransferFormFragmentToConfirmTransferFragment(
                    args.user,
                    amount
                )

            findNavController().navigate(action)

        } else {
            showBottomSheet(message = getString(R.string.text_message_empty_amount_fragment_transfer_form))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}