package com.android.sharedshoppinglist.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.databinding.FragmentMenuBinding
import kotlin.system.exitProcess

class MenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        val binding: FragmentMenuBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_menu, container,false)

        binding.newListBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_menuFragment_to_newListFragment)
        }

        binding.listsBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_menuFragment_to_listsFragment)
        }

        binding.quitBtn.setOnClickListener {
            exitProcess(0)
        }

        return binding.root
    }

}