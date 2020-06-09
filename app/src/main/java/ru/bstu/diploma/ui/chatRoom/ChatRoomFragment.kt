package ru.bstu.diploma.ui.chatRoom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentChatRoomBinding
import ru.bstu.diploma.extensions.isKeyboardOpen
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.ui.adapters.ChatRoomAdapter
import ru.bstu.diploma.viewmodels.ChatRoomViewModel
import ru.bstu.diploma.viewmodels.ChatRoomViewModelFactory

class ChatRoomFragment: Fragment() {
    private lateinit var binding: FragmentChatRoomBinding
    private lateinit var viewModel: ChatRoomViewModel
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var chatItem: ChatItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatRoomBinding.inflate(inflater)

        chatItem = ChatRoomFragmentArgs.fromBundle(requireArguments()).chatItem

        (activity as AppCompatActivity).supportActionBar!!.title = chatItem.title
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        val viewModelFactory = ChatRoomViewModelFactory(chatItem)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatRoomViewModel::class.java)
        viewModel.getMessagesData().observe(viewLifecycleOwner, Observer {
            chatRoomAdapter.updateData(it)
            binding.rvMessages.scrollToPosition((binding.rvMessages.adapter!!.itemCount-1) - chatItem.unreadCount)
        })
        viewModel.getIsMessageSent().observe(viewLifecycleOwner, Observer { updateRecyclerPosition(it) })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        chatRoomAdapter = ChatRoomAdapter(chatItem.chatType)

        binding.etMessage.setOnTouchListener { v, event ->
            val currentVisiblePosition = (binding.rvMessages.getLayoutManager() as LinearLayoutManager).findLastVisibleItemPosition()
            if (currentVisiblePosition == binding.rvMessages.adapter!!.itemCount - 1)
                binding.rvMessages.postDelayed({
                    binding.rvMessages.scrollToPosition(currentVisiblePosition)
                }, 100)
            false
        }

        with(binding.rvMessages) {
            adapter = chatRoomAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.ivSend.setOnClickListener{
            if(!TextUtils.isEmpty(binding.etMessage.text)) {
                viewModel.handleSendMessage(binding.etMessage.text.toString())
                binding.etMessage.setText("")
            }
        }
    }

    fun updateRecyclerPosition(isSent: Boolean){
        if(isSent == true){
            binding.rvMessages.scrollToPosition(binding.rvMessages.adapter!!.itemCount - 1)
            viewModel.reloadIsMessageSent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }
}