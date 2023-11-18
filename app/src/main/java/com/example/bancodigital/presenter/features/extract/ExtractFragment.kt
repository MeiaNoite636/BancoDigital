package com.example.bancodigital.presenter.features.extract

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.MainGraphDirections
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.databinding.FragmentExtractBinding
import com.example.bancodigital.presenter.home.HomeFragmentDirections
import com.example.bancodigital.presenter.home.TransactionAdapter
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import com.example.bancodigital.util.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExtractFragment : Fragment() {
    private var _binding: FragmentExtractBinding? = null
    private val binding get() = _binding!!

    private val extractViewModel: ExtractViewModel by viewModels()

    private lateinit var adapterTransaction: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtractBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        configRecyclerView()

        getTransactions()
    }

    private fun configRecyclerView() {
        adapterTransaction = TransactionAdapter(requireContext()) { transaction ->
            when (transaction.operation) {
                TransactionOperation.DEPOSIT -> {
                    val action = MainGraphDirections.actionGlobalDepositReceiptFragment(
                        transaction.id,
                        true
                    )

                    findNavController().navigate(action)
                }

                TransactionOperation.RECHARGE -> {
                    val action = MainGraphDirections.actionGlobalRechargeReceiptFragment(
                        transaction.id,
                        true
                    )

                    findNavController().navigate(action)
                }
                TransactionOperation.TRANSFER -> {
                    val action = MainGraphDirections.actionGlobalReceiptTransferFragment(
                        transaction.id,
                        true
                    )

                    findNavController().navigate(action)
                }

                else -> {

                }
            }
        }


        with(binding.rvTransactions) {
            setHasFixedSize(true)
            adapter = adapterTransaction
        }

    }

    private fun getTransactions() {
        extractViewModel.getTransactions().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressbar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressbar.isVisible = false

                    binding.textMessage.isVisible = stateView.data?.isEmpty() == true
                    adapterTransaction.submitList(stateView.data?.reversed())

                }

                is StateView.Error -> {
                    binding.progressbar.isVisible = false
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