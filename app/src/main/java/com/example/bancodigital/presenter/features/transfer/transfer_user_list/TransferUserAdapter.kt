package com.example.bancodigital.presenter.features.transfer.transfer_user_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bancodigital.R
import com.example.bancodigital.data.model.User
import com.example.bancodigital.databinding.TransferUserItemBinding
import com.squareup.picasso.Picasso

class TransferUserAdapter(
    private val userSelected: (User) -> Unit
) : ListAdapter<User, TransferUserAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TransferUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)

        loadImageUser(holder, user)

        holder.binding.textUserName.text = user.name


        holder.itemView.setOnClickListener {
            userSelected(user)
        }
    }

    private fun loadImageUser(holder: ViewHolder, user: User) {
        if (user.image.isNotEmpty()) {
            Picasso.get()
                .load(user.image)
                .fit().centerCrop()
                .into(holder.binding.imgUser)
        }else{
            holder.binding.imgUser.setImageResource(R.drawable.ic_user_place_holder)
        }
    }

    inner class ViewHolder(val binding: TransferUserItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}