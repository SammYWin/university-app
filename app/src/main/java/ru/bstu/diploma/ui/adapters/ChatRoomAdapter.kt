package ru.bstu.diploma.ui.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text_message.*
import ru.bstu.diploma.R
import ru.bstu.diploma.extensions.shortFormat
import ru.bstu.diploma.models.BaseMessage
import ru.bstu.diploma.models.TextMessage

class ChatRoomAdapter: RecyclerView.Adapter<ChatRoomAdapter.MessageItemViewHolder>() {

    var items: List<BaseMessage> = listOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_text_message, parent, false)
        return MessageItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageItemViewHolder, position: Int) {
        holder.bind(items[position])
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

        fun bind(item: BaseMessage){
            if(item is TextMessage){
                tv_message_text.text = item.text
                tv_message_time.text = item.date.shortFormat()

            }
        }
    }

    private fun setMessageRootGravity(holder: MessageItemViewHolder, message: BaseMessage) {
        if(message.senderId == FirebaseAuth.getInstance().currentUser!!.uid){
            holder.message_root.apply {
                background = resources.getDrawable(R.drawable.rect_message_mine, context.theme)
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.END
                )
            }
        }
        else{
            holder.message_root.apply {
                background = resources.getDrawable(R.drawable.rect_message_other, context.theme)
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.START
                )
            }
        }
    }

}