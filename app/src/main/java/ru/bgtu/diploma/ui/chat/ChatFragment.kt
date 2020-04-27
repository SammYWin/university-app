package ru.bgtu.diploma.ui.chat

import android.graphics.drawable.ColorDrawable
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
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_chat.*
import ru.bgtu.diploma.R
import ru.bgtu.diploma.databinding.FragmentChatBinding
import ru.bgtu.diploma.extensions.config
import ru.bgtu.diploma.models.data.ChatType
import ru.bgtu.diploma.ui.adapters.ChatAdapter
import ru.bgtu.diploma.ui.adapters.ChatItemTouchHelperCallback
import ru.bgtu.diploma.ui.archive.ArchiveFragment
import ru.bgtu.diploma.viewmodels.MainViewModel


class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(inflater)
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
        chatAdapter = ChatAdapter {
            if(it.chatType == ChatType.ARCHIVE) {
                findNavController().navigate(ChatFragmentDirections.actionChatFragmentToArchiveFragment())
            }
            else
                Snackbar.make(binding.rvChatList, "Click on ${it.title}and he is ${if (it.isOnline) "online" else "not online"}", Snackbar.LENGTH_LONG).show()
        }

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter){
            val actionTextColor = TypedValue()
            val textColor = TypedValue()
            requireNotNull(activity).theme.resolveAttribute(R.attr.colorAccent, actionTextColor, true)
            requireNotNull(activity).theme.resolveAttribute(R.attr.colorSnackBarText, textColor, true)

            viewModel.addToArchive(it.id)
            val snack = Snackbar.make(binding.rvChatList, "Вы точно хотите добавить ${it.title}в архив?", Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_bar_undo)) {_-> viewModel.restoreFromArchive(it.id)}
                .setActionTextColor(actionTextColor.data)

            val textView = snack.view.findViewById(R.id.snackbar_text) as TextView
            textView.setTextColor(textColor.data)
            snack.config(snack.context)

            snack.show()
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(binding.rvChatList)

        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        with(binding.rvChatList){
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            addItemDecoration(divider)
        }


        binding.fab.setOnClickListener{
            findNavController().navigate(ChatFragmentDirections.actionChatFragmentToGroupFragment())
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(viewLifecycleOwner, Observer { chatAdapter.updateData(it) })
    }



}
