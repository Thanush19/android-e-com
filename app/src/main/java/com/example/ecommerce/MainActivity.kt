package com.example.ecommerce

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.databinding.ActivityMainBinding
import com.example.ecommerce.views.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: AuthViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as? NavHostFragment
            ?: throw IllegalStateException("fragment not found")
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myProfileFragment -> {
                    navController.navigate(R.id.myProfileFragment)
                    true
                }
                R.id.myFeedFragment -> {
                    navController.navigate(R.id.myFeedFragment)
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.myProfileFragment -> {
                    navController.popBackStack(R.id.myProfileFragment, false)
                }
                R.id.myFeedFragment -> {
                    navController.popBackStack(R.id.myFeedFragment, false)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.myFeedFragment, R.id.myProfileFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }

        observeAuthState()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            vm.loggedIn.collectLatest { isLoggedIn ->
                val currentDestination = navController.currentDestination?.id
                if (isLoggedIn && currentDestination != R.id.myFeedFragment) {
                    navController.navigate(R.id.myFeedFragment)
                } else if (!isLoggedIn && currentDestination != R.id.loginFragment) {
                    navController.navigate(R.id.loginFragment)
                }
            }
        }

    }
}