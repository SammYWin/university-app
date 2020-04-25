package ru.bgtu.diploma.ui.archive

import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_archive.*
import ru.bgtu.diploma.R
import ru.bgtu.diploma.extensions.config
import ru.bgtu.diploma.ui.adapters.ChatAdapter
import ru.bgtu.diploma.ui.adapters.ChatItemTouchHelperCallback
import ru.bgtu.diploma.viewmodels.ArchiveViewModel

class ArchiveFragment : Fragment() {
    private lateinit var chatAdapter : ChatAdapter
    private lateinit var viewModel : ArchiveViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_archive, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(ArchiveViewModel::class.java)
        viewModel.getChatData().observe(viewLifecycleOwner, Observer{chatAdapter.updateData(it)})
    }

    private fun initViews() {
        chatAdapter = ChatAdapter {
            Snackbar.make(rv_archive_list,"Click on ${it.title}and he is ${if (it.isOnline) "online" else "not online"}", Snackbar.LENGTH_LONG).show()
        }

        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter){
            val actionTextColor = TypedValue()
            val textColor = TypedValue()
            requireNotNull(activity).theme.resolveAttribute(R.attr.colorAccent, actionTextColor, true)
            requireNotNull(activity).theme.resolveAttribute(R.attr.colorSnackBarText, textColor, true)

            viewModel.restoreFromArchive(it.id)
            val snack = Snackbar.make(rv_archive_list, "Восстановить чат с ${it.title}из архива?", Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.snack_bar_undo)) {_-> viewModel.addToArchive(it.id)}
                    .setActionTextColor(actionTextColor.data)

            val textView = snack.view.findViewById(R.id.snackbar_text) as TextView
            textView.setTextColor(textColor.data)
            snack.config(snack.context)

            snack.show()
        }
        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            addItemDecoration(divider)
        }
    }
}
