package ru.bstu.diploma.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_list.*
import ru.bstu.diploma.R
import ru.bstu.diploma.glide.GlideApp
import ru.bstu.diploma.models.data.UserItem
import ru.bstu.diploma.utils.StorageUtil

class UserAdapter(val listener: (UserItem)->Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var items: List<UserItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) = holder.bind(items[position], listener)

    fun updateData(data : List<UserItem>){
        val diffCallBack = object : DiffUtil.Callback(){

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].hashCode() == data[newPos].id.hashCode()

            override fun getOldListSize(): Int  = items.size

            override fun getNewListSize(): Int = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallBack)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), LayoutContainer{
        override val containerView: View?
        get() = itemView

        fun bind(user: UserItem, listener: (UserItem) -> Unit){
            if(user.avatar == ""){
                Glide.with(itemView).clear(iv_avatar_user)
                iv_avatar_user.setInitials(user.initials ?: "??")
            } else{
                GlideApp.with(itemView)
                    .load(StorageUtil.pathToReference(user.avatar!!))
                    .into(iv_avatar_user)
            }

            sv_indicator.visibility = if(user.isOnline == true) View.VISIBLE else View.GONE
            tv_user_name.text = user.fullName
            tv_last_activity.text = user.lastActivity
            iv_selected.visibility = if(user.isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener{listener.invoke(user)}
        }
    }
}