package ru.bstu.diploma.ui.adapters

import android.graphics.drawable.Drawable
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text_message.*
import ru.bstu.diploma.R
import ru.bstu.diploma.extensions.shortFormat
import ru.bstu.diploma.glide.GlideApp
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.TextMessage
import ru.bstu.diploma.models.data.ChatType
import ru.bstu.diploma.utils.FirestoreUtil
import ru.bstu.diploma.utils.StorageUtil
import ru.bstu.diploma.utils.Utils

class ChatRoomAdapter(val chatType: ChatType, val avatarClicked: (userId: String) -> Unit): RecyclerView.Adapter<ChatRoomAdapter.MessageItemViewHolder>() {

    var items: List<BaseMessage> = listOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_text_message, parent, false)
        return MessageItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) {
        holder.bind(items[position], chatType)
        setMessageRootGravity(holder, items[position])
    }

    fun updateData(data : List<BaseMessage>){
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

    inner class MessageItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), LayoutContainer  {
        override val containerView: View?
            get() = itemView

        fun bind(item: BaseMessage, chatType: ChatType){
            if(item is TextMessage){
                tv_message_text.text = item.text
                tv_message_time.text = item.date.shortFormat()
            }

            iv_sender_avatar.setOnClickListener {
                avatarClicked(item.senderId)
            }

            if(chatType == ChatType.GROUP && item.senderId != FirebaseAuth.getInstance().currentUser!!.uid){
                tv_sender_name.visibility = View.VISIBLE
                tv_sender_name.text = item.senderName

                if(item is TextMessage){
                    val params = tv_message_time.layoutParams as RelativeLayout.LayoutParams
                    if(item.text!!.length <= item.senderName.length){
                        params.addRule(RelativeLayout.ALIGN_END, R.id.tv_sender_name)
                    } else params.addRule(RelativeLayout.ALIGN_END, R.id.tv_message_text)
                    tv_message_time.layoutParams = params
                }

                iv_sender_avatar.visibility = View.VISIBLE
                FirestoreUtil.getUserById(item.senderId){
                    if(it.avatar == null || it.avatar == "" ){
                        GlideApp.with(itemView).clear(iv_sender_avatar)
                        iv_sender_avatar.setInitials(Utils.toInitials(it.firstName, it.lastName)!!)
                    }else {
                        GlideApp.with(itemView)
                            .load(StorageUtil.pathToReference(it.avatar!!))
                            .placeholder(getDrawable(itemView.context, R.drawable.avatar_default))
                            .into(iv_sender_avatar)
                    }
                }
            }else{
                tv_sender_name.visibility = View.GONE
                iv_sender_avatar.visibility = View.GONE

                val params = tv_message_time.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_END, R.id.tv_message_text)
                tv_message_time.layoutParams = params
            }
        }
    }

    private fun setMessageRootGravity(holder: MessageItemViewHolder, message: BaseMessage) {
        if(message.senderId == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.root.apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.END
                )
            }

            holder.message_root.apply {
                background = resources.getDrawable(R.drawable.rect_message_mine, context.theme)
            }

        }
        else{
            holder.root.apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.START
                )
            }

            holder.message_root.apply {
                background = resources.getDrawable(R.drawable.rect_message_other, context.theme)
            }
        }
    }

}