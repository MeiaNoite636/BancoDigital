package com.example.bancodigital.presenter.features.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bancodigital.MainGraphDirections
import com.example.bancodigital.R
import com.example.bancodigital.data.enum.TransactionOperation
import com.example.bancodigital.data.model.Deposit
import com.example.bancodigital.databinding.FragmentDepositReceiptBinding
import com.example.bancodigital.util.GetMask
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DepositReceiptFragment : Fragment() {

    private var _binding: FragmentDepositReceiptBinding? = null
    private val binding get() = _binding!!

    private val depositReceiptViewModel: DepositReceiptViewModel by viewModels()
    private val args: DepositReceiptFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepositReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar, args.homeAsUpEnabled)

        getDeposit()

        initListener()
    }

    private fun getDeposit() {
        depositReceiptViewModel.getDeposit(args.idDeposit).observe(viewLifecycleOwner){stateview->
            when(stateview){
                is StateView.Loading -> {

                }
                is StateView.Sucess  -> {
                    stateview.data?.let {
                        configData(it)
                    }
                }
                is StateView.Error -> {
                    Toast.makeText(requireContext(), "Ocorreu um erro", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun configData(deposit: Deposit) {
        binding.tvCodeTransaction.text = deposit.id
        binding.tvDateValue.text = GetMask.getFormatedDate(deposit.date, GetMask.DAY_MONTH_YEAR_HOUR_MINUTE)
        binding.tvValueTransaction.text =
            getString(R.string.text_formated_value, GetMask.getFormatedValue(deposit.amount))
    }

    private fun initListener() {
        binding.btnContinue.setOnClickListener {
            if(args.homeAsUpEnabled){
                findNavController().popBackStack()
            }else{
                val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.depositFormFragment, true).build()

                findNavController().navigate(R.id.action_global_homeFragment, null, navOptions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
