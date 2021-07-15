package com.example.searchmovieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.searchmovieapp.R
import com.example.searchmovieapp.databinding.FragmentNavigationContainerBinding

class NavigationContainerFragment : Fragment() {
    private var _binding: FragmentNavigationContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::navigate)

        navigateToFragment(HomeFragment())
    }

    private fun navigate(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home -> navigateToFragment(HomeFragment())
            R.id.item_favorites -> navigateToFragment(HomeFragment())
            R.id.item_ratings -> navigateToFragment(HomeFragment())
        }

        return true
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container_with_navigation, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}