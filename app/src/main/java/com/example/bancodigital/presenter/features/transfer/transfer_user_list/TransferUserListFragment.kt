package com.example.bancodigital.presenter.features.transfer.transfer_user_list

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bancodigital.R
import com.example.bancodigital.data.model.User
import com.example.bancodigital.databinding.FragmentTransferUserListBinding
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import com.example.bancodigital.util.showBottomSheet
import com.ferfalk.simplesearchview.SimpleSearchView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransferUserListFragment : Fragment() {
    private var _binding: FragmentTransferUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var transferUserAdapter: TransferUserAdapter

    private val transferUserListViewModel: TransferUserListViewModel by viewModels()

    private var profileList: List<User> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar, light = true)

        initRecyclerView()

        getProfileList()

        configSearchView()

    }

    private fun configSearchView() {
        binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return if (newText.isNotEmpty()) {
                    val newList = profileList.filter { it.name.contains(newText, true) }

                    emptyUserList(newList)

                    transferUserAdapter.submitList(newList)
                    true
                } else {
                    emptyUserList(profileList)
                    transferUserAdapter.submitList(profileList)
                    false
                }
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        binding.searchView.setOnSearchViewListener(object : SimpleSearchView.SearchViewListener {
            override fun onSearchViewShown() {
            }

            override fun onSearchViewClosed() {
                emptyUserList(profileList)
                transferUserAdapter.submitList(profileList)
            }

            override fun onSearchViewShownAnimation() {
            }

            override fun onSearchViewClosedAnimation() {
            }
        })
    }

    private fun getProfileList() {
        transferUserListViewModel.getProfileListUser().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false

                    profileList = stateView.data ?: emptyList()
                    transferUserAdapter.submitList(profileList)
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(message = stateView.message)
                }
            }
        }
    }

    private fun initRecyclerView() {
        transferUserAdapter = TransferUserAdapter { userSelected ->
            val action =
                TransferUserListFragmentDirections.actionTransferUserListFragmentToTransferFormFragment(
                    userSelected
                )

            findNavController().navigate(action)
        }

        with(binding.rvUsers) {
            adapter = transferUserAdapter
            setHasFixedSize(true)
        }

    }

    private fun emptyUserList(userList: List<User>) {
        binding.textMessage.isVisible = userList.isEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val item = menu.findItem(R.id.action_search)
        binding.searchView.setMenuItem(item)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}