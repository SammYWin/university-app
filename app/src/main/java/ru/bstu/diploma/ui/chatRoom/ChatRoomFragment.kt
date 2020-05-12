package ru.bstu.diploma.ui.chatRoom

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_chat_room.*
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentChatRoomBinding
import ru.bstu.diploma.models.data.ChatItem
import ru.bstu.diploma.ui.adapters.ChatItemTouchHelperCallback
import ru.bstu.diploma.ui.adapters.ChatRoomAdapter
import ru.bstu.diploma.utils.FirestoreUtil
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
        viewModel.getMessagesData().observe(viewLifecycleOwner, Observer { chatRoomAdapter.updateData(it) })
        viewModel.getIsMessageSend().observe(viewLifecycleOwner, Observer { updateRecyclerPosition(it) })
    }

    private fun initViews() {
        chatRoomAdapter = ChatRoomAdapter()

        with(binding.rvMessages) {
            adapter = chatRoomAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            scrollToPosition(binding.rvMessages.adapter!!.itemCount - 1)
        }

        binding.ivSend.setOnClickListener{
            if(!TextUtils.isEmpty(binding.etMessage.text)) {
                viewModel.handleSendMessage(binding.etMessage.text.toString())
                binding.etMessage.setText("")
            }
        }
    }

    fun updateRecyclerPosition(isSend: Boolean){
        if(isSend == true){
            binding.rvMessages.scrollToPosition(binding.rvMessages.adapter!!.itemCount - 1)
            viewModel.reloadIsMessageSend()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
    }
}