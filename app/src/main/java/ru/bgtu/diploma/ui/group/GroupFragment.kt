package ru.bgtu.diploma.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group.*
import kotlinx.android.synthetic.main.fragment_group.chip_group
import kotlinx.android.synthetic.main.fragment_group.fab
import kotlinx.android.synthetic.main.fragment_group.toolbar
import ru.bgtu.diploma.R
import ru.bgtu.diploma.models.data.UserItem
import ru.bgtu.diploma.ui.adapters.UserAdapter
import ru.bgtu.diploma.viewmodels.GroupViewModel
import java.util.function.ToDoubleBiFunction

class GroupFragment : Fragment() {

    private lateinit var usersAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_group, container, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
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

        return super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun initViews() {
        usersAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        with(rv_user_list) {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        fab.setOnClickListener {
            viewModel.handleCreateGroup()
            TODO("navigate back to Chat Fragment")
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(viewLifecycleOwner, Observer { usersAdapter.updateData(it) })
        viewModel.getSelectedData().observe(viewLifecycleOwner, Observer {
            updateChips(it)
            toggleFab(it.size > 1)
        })
    }

    private fun toggleFab(isShow: Boolean) {
        if(isShow) fab.show()
        else fab.hide()
    }

    private fun addChipToGroup(user: UserItem) {
        val closeIconColor = TypedValue()
        val bgColor = TypedValue()
        requireNotNull(activity).theme.resolveAttribute(R.attr.colorChipClose, closeIconColor, true)
        requireNotNull(activity).theme.resolveAttribute(R.attr.colorChipBackground, bgColor, true)


        val chip = Chip(context).apply {
            text = user.fullName
            chipIcon = resources.getDrawable(R.drawable.avatar_default, requireNotNull(activity).theme)
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(closeIconColor.data)
            chipBackgroundColor = ColorStateList.valueOf(bgColor.data)

            setTextColor(Color.WHITE)
            setOnCloseIconClickListener{viewModel.handleRemoveChip(tag.toString())}
        }

        chip_group.addView(chip)
    }

    private fun updateChips(listUsers : List<UserItem>) {
        chip_group.visibility = if(listUsers.isEmpty()) View.GONE else View.VISIBLE

        val users = listUsers.associate { user -> user.id to user }.toMutableMap()
        val views = chip_group.children.associate { view -> view.tag to view }

        for ((k, v) in views) {
            if (!users.containsKey(k))
                chip_group.removeView(v)
            else users.remove(k)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }
}
