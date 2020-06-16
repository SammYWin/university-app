package ru.bstu.diploma.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.bstu.diploma.R
import ru.bstu.diploma.databinding.FragmentProfileInfoBinding
import ru.bstu.diploma.extensions.humanizeDiff
import ru.bstu.diploma.glide.GlideApp
import ru.bstu.diploma.models.data.Chat
import ru.bstu.diploma.models.data.User
import ru.bstu.diploma.utils.StorageUtil
import ru.bstu.diploma.utils.Utils
import ru.bstu.diploma.viewmodels.ProfileInfoViewModel
import ru.bstu.diploma.viewmodels.ProfileInfoViewModelFactory

class ProfileInfoFragment: Fragment() {

    private lateinit var binding: FragmentProfileInfoBinding
    private lateinit var viewModel: ProfileInfoViewModel
    private var chatId: String? = null
    private var userId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileInfoBinding.inflate(inflater, container, false)

        chatId = ProfileInfoFragmentArgs.fromBundle(requireArguments()).chatId
        userId = ProfileInfoFragmentArgs.fromBundle(requireArguments()).userId

        initViews()
        initViewModel()

        return binding.root
    }

    private fun initViewModel() {
        val viewModelFactory = ProfileInfoViewModelFactory(chatId, userId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileInfoViewModel::class.java)
        viewModel.getUserInfo().observe(viewLifecycleOwner, Observer { user -> updateUserInfo(user) })
        viewModel.getChat().observe(viewLifecycleOwner, Observer { chat -> navigateToChat(chat) })
    }

    private fun navigateToChat(chat: Chat) {
        findNavController().navigate(ProfileInfoFragmentDirections.actionProfileInfoFragmentToChatRoomFragment(chat.toChatItem()))
    }

    private fun updateUserInfo(user: User) {
        binding.tvNickName.text = user.nickName
        binding.tvGroup.text = user.group
        binding.tvLastVisit.text = when {
            user.lastVisit == null -> "Ещё ни разу не заходил"
            user.isOnline == true -> "online"
            else -> "Был в сети ${user.lastVisit.humanizeDiff()}"
        }

        if(user.isGroupLeader == true) {
            binding.ivAvatar.setBorderColor(resources.getColor(R.color.color_avatar_border_leader))
            binding.ivAvatar.setBorderWidth(2)
        }else if (user.isProfessor == true){
            binding.ivAvatar.setBorderColor(resources.getColor(R.color.color_avatar_border_professor))
            binding.ivAvatar.setBorderWidth(2)
        }

        if(user.avatar == "" || user.avatar == null)
            Utils.toInitials(user.firstName, user.lastName)?.let {
                binding.ivAvatar.setInitials(it)
                binding.ivAvatar.setImageDrawable(null)
            } ?: binding.ivAvatar.setImageResource(R.drawable.avatar_default)
        else  {
            GlideApp.with(this)
                .load(StorageUtil.pathToReference(user.avatar!!))
                .placeholder(resources.getDrawable(R.drawable.avatar_default, requireActivity().theme) )
                .into(binding.ivAvatar)
        }
    }

    private fun initViews() {
        binding.ivAvatar.setImageResource(R.drawable.avatar_default)
        binding.fab.setOnClickListener {
            if(chatId != null)
                findNavController().popBackStack()
            else
                viewModel.handleGetOrCreateChat()
        }
    }
}