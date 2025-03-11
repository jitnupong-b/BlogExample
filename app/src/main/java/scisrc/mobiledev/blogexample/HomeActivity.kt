package scisrc.mobiledev.blogexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import scisrc.mobiledev.blogexample.databinding.ActivityHomeBinding
import scisrc.mobiledev.blogexample.ui.dashboard.DashboardFragment
import scisrc.mobiledev.blogexample.ui.home.HomeFragment
import scisrc.mobiledev.blogexample.ui.notifications.NotificationsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, DashboardFragment())
                        .commit()
                    true
                }
                R.id.navigation_notifications -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NotificationsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.navigation_notifications)
        badge.number = 99
        badge.isVisible = true
    }
}