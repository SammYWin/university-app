package ru.bstu.diploma.ui.chatList

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentChatListBinding
import ru.bstu.diploma.extensions.config
import ru.bstu.diploma.models.data.ChatType
import ru.bstu.diploma.ui.adapters.ChatListAdapter
import ru.bstu.diploma.ui.adapters.ChatItemTouchHelperCallback
import ru.bstu.diploma.viewmodels.ChatListViewModel


class ChatListFragment : Fragment() {

    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var viewModel: ChatListViewModel
    private lateinit var binding: FragmentChatListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatListBinding.inflate(inflater)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initViews() {
        chatListAdapter = ChatListAdapter {
            if(it.chatType == ChatType.ARCHIVE) {
                findNavController().navigate(ChatListFragmentDirections.actionChatFragmentToArchiveFragment())
            }
            else
                Snackbar.make(binding.rvChatList, "Click on ${it.title}and he is ${if (it.isOnline == true) "online" else "not online"}", Snackbar.LENGTH_LONG).show()
        }

        val touchCallback = ChatItemTouchHelperCallback(chatListAdapter){
//            val actionTextColor = TypedValue()
//            val textColor = TypedValue()
//            requireNotNull(activity).theme.resolveAttribute(R.attr.colorAccent, actionTextColor, true)
//            requireNotNull(activity).theme.resolveAttribute(R.attr.colorSnackBarText, textColor, true)
//
//            viewModel.addToArchive(it.id)
//            val snack = Snackbar.make(binding.rvChatList, "Вы точно хотите добавить ${it.title}в архив?", Snackbar.LENGTH_LONG)
//                .setAction(getString(R.string.snack_bar_undo)) {_-> viewModel.restoreFromArchive(it.id)}
//                .setActionTextColor(actionTextColor.data)
//
//            val textView = snack.view.findViewById(R.id.snackbar_text) as TextView
//            textView.setTextColor(textColor.data)
//            snack.config(snack.context)
//
//            snack.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(binding.rvChatList)

        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        with(binding.rvChatList){
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
            addItemDecoration(divider)
        }


        binding.fab.setOnClickListener{
            findNavController().navigate(ChatListFragmentDirections.actionChatFragmentToGroupFragment())
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(ChatListViewModel::class.java)
        viewModel.getChatData().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                chatListAdapter.updateData(it)
            }
        })
    }



}
