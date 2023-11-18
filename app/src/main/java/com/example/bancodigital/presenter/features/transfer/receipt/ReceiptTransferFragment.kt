package com.example.bancodigital.presenter.features.transfer.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bancodigital.R
import com.example.bancodigital.data.model.Transfer
import com.example.bancodigital.data.model.User
import com.example.bancodigital.databinding.FragmentTransferReceiptBinding
import com.example.bancodigital.util.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptTransferFragment : Fragment() {

    private var _binding: FragmentTransferReceiptBinding? = null
    private val binding get() = _binding!!

    private val receiptTransferViewModel: ReceiptTransferViewModel by viewModels()

    private val args: ReceiptTransferFragmentArgs by navArgs()

    private val tagPicasso = "tagPicasso"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar, args.homeAsUpEnabled)

        getTransfer()

        initListener()
    }

    private fun getTransfer() {
        receiptTransferViewModel.getTransfer(args.idTransfer)
            .observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is StateView.Loading -> {

                    }
                    is StateView.Sucess -> {
                        stateView.data?.let { transfer ->
                            val userId = if (transfer.idUserSend == FirebaseHelper.getUserId()) {
                                transfer.idUserReceived
                            } else {
                                transfer.idUserSend
                            }
                            getProfile(userId)
                            configTransfer(transfer)
                        }
                    }
                    is StateView.Error -> {

                        showBottomSheet(message = stateView.message)
                    }
                }
            }
    }

    private fun getProfile(id: String) {
        receiptTransferViewModel.getProfile(id).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {

                }
                is StateView.Sucess -> {
                    stateView.data?.let {
                        configProfile(it)
                    }
                }
                is StateView.Error -> {

                    showBottomSheet(message = stateView.message)
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initListener() {
        binding.btnConfirm.setOnClickListener {
            if(args.homeAsUpEnabled){
                findNavController().popBackStack()
            }else{
                val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.transferUserListFragment, true).build()

                findNavController().navigate(R.id.action_global_homeFragment, null, navOptions)
            }
        }
    }

    private fun configTransfer(transfer: Transfer) {
        binding.tvSentOrReceived.text = if(transfer.idUserSend == FirebaseHelper.getUserId()){
            getString(R.string.text_message_sent_receipt_transfer_fragment)
        }else{
            getString(R.string.text_message_received_receipt_transfer_fragment)
        }

        binding.tvCodeTransactionValue.text = transfer.id
        binding.tvDepositDateValue.text = GetMask.getFormatedDate(transfer.date, 3)
        binding.tvValueDeposit.text =
            getString(R.string.text_formated_value, GetMask.getFormatedValue(transfer.amount))
    }

    private fun configProfile(user: User) {
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

                    override fun onError(e: java.lang.Exception?) {
                        TODO("Not yet implemented")
                    }

                })
        } else {
            binding.progressImage.isVisible = false
            binding.imgUser.isVisible = true
            binding.imgUser.setImageResource(R.drawable.ic_user_place_holder)
        }

        binding.tvUserName.text = user.name
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Picasso.get()?.cancelTag(tagPicasso)
        _binding = null
    }
}
