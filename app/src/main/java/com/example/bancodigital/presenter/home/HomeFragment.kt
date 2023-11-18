package com.example.bancodigital.presenter.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.MainGraphDirections
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.enum.TransactionType
import com.example.bancodigital.data.model.Transaction
import com.example.bancodigital.data.model.User
import com.example.bancodigital.databinding.FragmentHomeBinding
import com.example.bancodigital.util.FirebaseHelper
import com.example.bancodigital.util.GetMask
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.showBottomSheet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapterTransaction: TransactionAdapter

    private val tagPicasso = "tagPicasso"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProfile()

        configRecyclerView()

        getTransactions()

        initListener()
    }

    private fun initListener() {
        binding.btnLogout.setOnClickListener {
            FirebaseHelper.getAuth().signOut()

            val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build()

            findNavController().navigate(R.id.action_global_authentication, null, navOptions)
        }

        binding.cardExtract.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_extractFragment)
        }

        binding.btnShowAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_extractFragment)
        }

        binding.cardDeposit.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_depositFormFragment)
        }

        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.cardRecharge.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_rechargeFormFragment)
        }

        binding.cardTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transferUserListFragment)
        }

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
                        transaction.id, true
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

    private fun getProfile() {
        homeViewModel.getProfile().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressbar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressbar.isVisible = false
                    stateView.data?.let {
                        configData(it)
                    }
                }

                is StateView.Error -> {
                    binding.progressbar.isVisible = false
                    showBottomSheet(
                        message = getString(
                            FirebaseHelper.validError(
                                stateView.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    private fun configData(user: User) {
        if (user.image.isNotEmpty()) {
            Picasso.get()
                .load(user.image)
                .tag(tagPicasso)
                .fit().centerCrop()
                .into(binding.imgUser, object : Callback {
                    override fun onSuccess() {
                        binding.progressImage.isVisible = false
                        binding.imgUser.isVisible = true
                    }

                    override fun onError(e: Exception) {
                        TODO("Not yet implemented")
                    }

                })
        } else {
            binding.progressImage.isVisible = false
            binding.imgUser.isVisible = true
            binding.imgUser.setImageResource(R.drawable.ic_user_place_holder)
        }
    }

    private fun getTransactions() {
        homeViewModel.getTransactions().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressbar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressbar.isVisible = false

                    adapterTransaction.submitList(stateView.data?.reversed()?.take(6))

                    binding.textMessage.isVisible = stateView.data?.isEmpty() == true

                    showBalance(stateView.data ?: emptyList())
                }

                is StateView.Error -> {
                    binding.progressbar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    private fun showBalance(transactions: List<Transaction>) {
        var cashIn = 0f
        var cashOut = 0f

        transactions.forEach { transaction ->
            if (transaction.type == TransactionType.CASH_IN) {
                cashIn += transaction.amount
            } else {
                cashOut += transaction.amount
            }
        }

        binding.textBalance.text =
            getString(R.string.text_formated_value, GetMask.getFormatedValue(cashIn - cashOut))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Picasso.get().cancelTag(tagPicasso)
        _binding = null
    }
}