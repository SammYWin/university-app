package ru.bstu.diploma.ui.users

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_users.chip_group
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentUsersBinding
import ru.bstu.diploma.models.data.UserItem
import ru.bstu.diploma.ui.adapters.UserAdapter
import ru.bstu.diploma.viewmodels.UsersViewModel

class UsersFragment : Fragment() {

    private lateinit var usersAdapter: UserAdapter
    private lateinit var viewModel: UsersViewModel
    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsersBinding.inflate(inflater)
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
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


    private fun initViews() {
        usersAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        with(binding.rvUserList) {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        binding.fab.setOnClickListener {
            viewModel.handleCreateChat()
            findNavController().popBackStack()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        viewModel.getUsersData().observe(viewLifecycleOwner, Observer { usersAdapter.updateData(it) })
        viewModel.getSelectedData().observe(viewLifecycleOwner, Observer {
            updateChips(it)
            toggleFab(it.size >= 1)
        })
    }

    private fun toggleFab(isShow: Boolean) {
        if(isShow) binding.fab.show()
        else binding.fab.hide()
    }

    private fun addChipToGroup(user: UserItem) {
        val closeIconColor = TypedValue()
        val bgColor = TypedValue()
        requireNotNull(activity).theme.resolveAttribute(R.attr.colorChipClose, closeIconColor, true)
        requireNotNull(activity).theme.resolveAttribute(R.attr.colorChipBackground, bgColor, true)

        val chip = Chip(context).apply {
            text = user.fullName
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(closeIconColor.data)
            chipBackgroundColor = ColorStateList.valueOf(bgColor.data)

            setTextColor(Color.WHITE)
            setOnCloseIconClickListener { viewModel.handleRemoveChip(tag.toString()) }
        }

        binding.chipGroup.addView(chip)
    }

    private fun updateChips(listUsers : List<UserItem>) {
        binding.chipGroup.visibility = if(listUsers.isEmpty()) View.GONE else View.VISIBLE

        val users = listUsers.associate { user -> user.id to user }.toMutableMap()
        val views = chip_group.children.associate { view -> view.tag to view }

        for ((k, v) in views) {
            if (!users.containsKey(k))
                binding.chipGroup.removeView(v)
            else users.remove(k)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }
}
