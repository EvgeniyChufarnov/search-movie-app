package com.example.searchmovieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.searchmovieapp.R
import com.example.searchmovieapp.databinding.FragmentNavigationContainerBinding
import com.example.searchmovieapp.ui.favorites.FavoritesFragment
import com.example.searchmovieapp.ui.home.HomeFragment
import com.example.searchmovieapp.ui.ratings.RatingsFragment

private const val CURRENT_FRAGMENT_TAG = "current frugment"

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

        binding.bottomNavigation.setOnItemSelectedListener(::navigate)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        binding.bottomNavigation.run {
            navigate(menu.findItem(selectedItemId))
        }
    }

    private fun navigate(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_home -> navigateToFragment(HomeFragment())
            R.id.item_favorites -> navigateToFragment(FavoritesFragment())
            R.id.item_ratings -> navigateToFragment(RatingsFragment())
        }

        return true
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container_with_navigation, fragment, CURRENT_FRAGMENT_TAG)
            .commit()
    }

    override fun onDestroyView() {
        _binding = null

        val currentFragment = requireActivity().supportFragmentManager
            .findFragmentByTag(CURRENT_FRAGMENT_TAG)

        currentFragment?.let {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }

        super.onDestroyView()
    }
}