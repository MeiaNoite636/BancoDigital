package com.example.bancodigital.presenter.features.recharge

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
import com.example.bancodigital.MainGraphDirections
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.data.model.Recharge
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.databinding.FragmentRechargeFormBinding
import com.example.bancodigital.presenter.features.deposit.DepositFormFragmentDirections
import com.example.bancodigital.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RechargeFormFragment : BaseFragment() {
    private var _binding: FragmentRechargeFormBinding? = null
    private val binding get() = _binding!!

    private val rechargeViewModel: RechargeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargeFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar, light = true)

        initListener()
    }


    private fun initListener() {
        with(binding.editAmount) {
            addTextChangedListener(MoneyTextWatcher(this))

            addTextChangedListener {
                if(MoneyTextWatcher.getValueUnMasked(this) > 100f){
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
            validateData()
        }
    }

    private fun validateData() {
        val amount = MoneyTextWatcher.getValueUnMasked(binding.editAmount)
        val phone = binding.etRecharge.text.toString().trim()

        if (amount > 0f) {
            if (phone.isNotEmpty()) {

                hideKeyboard()

                val recharge = Recharge(
                    amount = amount,
                    number = phone
                )

                saveRecharge(recharge)

            } else {
                showBottomSheet(message = getString(R.string.text_value_recharge_form_amount_fragment))
            }
        } else {
            showBottomSheet(message = "Digite um valor")
        }
    }

    private fun saveRecharge(recharge: Recharge) {
        rechargeViewModel.saveRecharge(recharge).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    stateView.data?.let {
                        saveTransaction(it)
                    }

                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    private fun saveTransaction(recharge: Recharge) {
        val transaction = Transaction(
            id = recharge.id,
            operation = TransactionOperation.RECHARGE,
            date = recharge.date,
            amount = recharge.amount,
            type = TransactionType.CASH_OUT
        )

        rechargeViewModel.saveTransaction(transaction).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {

                }

                is StateView.Sucess -> {
                    val action = MainGraphDirections.actionGlobalRechargeReceiptFragment(
                        recharge.id,
                        false
                    )

                    findNavController().navigate(action)
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}