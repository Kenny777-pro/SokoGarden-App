package com.example.sokogardenapp_kenny

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    // VIEWS
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var txtTrending: TextView
    private lateinit var promoCarousel: ViewPager2
    private lateinit var carouselIndicator: TabLayout

    private lateinit var toggleMenu: ImageButton
    private lateinit var topMenu: View
    private lateinit var overlay: View

    private lateinit var btnTop: FloatingActionButton

    // Category Buttons
    private lateinit var btnShopNow: Button
    private lateinit var btnPhones: Button
    private lateinit var btnLaptops: Button
    private lateinit var btnGaming: Button

    // DATA
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val filteredList = mutableListOf<Product>()

    // CAROUSEL LOGIC
    private val carouselHandler = Handler(Looper.getMainLooper())
    private val carouselRunnable = Runnable {
        val itemCount = promoCarousel.adapter?.itemCount ?: 1
        val nextItem = (promoCarousel.currentItem + 1) % itemCount
        promoCarousel.setCurrentItem(nextItem, true)
    }

    // SERVER
    private val url = "https://kennyfungo.alwaysdata.net/api/get_device_details"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupCarousel()
        setupRecycler()
        setupSearch()
        setupMenu()
        setupScrollButton()
        setupCategoryListeners()

        fetchProducts()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerview)
        progressBar = findViewById(R.id.progress)
        searchView = findViewById(R.id.searchView)
        txtTrending = findViewById(R.id.txtTrending)
        promoCarousel = findViewById(R.id.promoCarousel)
        carouselIndicator = findViewById(R.id.carouselIndicator)

        toggleMenu = findViewById(R.id.toggleMenu)
        topMenu = findViewById(R.id.topMenu)
        overlay = findViewById(R.id.overlay)

        btnTop = findViewById(R.id.btnTop)

        findViewById<Button>(R.id.signup).setOnClickListener { 
            startActivity(Intent(this, SignUpActivity::class.java))
            hideMenu()
        }
        findViewById<Button>(R.id.signin).setOnClickListener { 
            startActivity(Intent(this, SigninActivity::class.java))
            hideMenu()
        }
        findViewById<Button>(R.id.about).setOnClickListener { 
            startActivity(Intent(this, AboutActivity::class.java))
            hideMenu()
        }
        findViewById<Button>(R.id.logout).setOnClickListener { 
            toast("Logging out...")
            hideMenu()
        }

        btnShopNow = findViewById(R.id.btnShopNow)
        btnPhones = findViewById(R.id.btnPhones)
        btnLaptops = findViewById(R.id.btnLaptops)
        btnGaming = findViewById(R.id.btnGaming)
    }

    private fun setupCarousel() {
        val promos = listOf(
            Promo("iPhone 15 Pro", "The ultimate iPhone experience.", "https://kennyfungo.alwaysdata.net/static/images/iphone15pro.jpg"),
            Promo("MacBook Air M3", "Supercharged by M3 chip.", "https://kennyfungo.alwaysdata.net/static/images/macbook_m3.jpg"),
            Promo("PlayStation 5", "Play Has No Limits.", "https://kennyfungo.alwaysdata.net/static/images/ps5.jpg")
        )

        val carouselAdapter = CarouselAdapter(promos) { promo ->
            filterProducts(promo.title.lowercase())
            toast("Viewing ${promo.title}")
        }
        
        promoCarousel.adapter = carouselAdapter
        
        // Link Indicator with ViewPager2
        TabLayoutMediator(carouselIndicator, promoCarousel) { _, _ -> }.attach()
        
        // Auto-slide logic
        promoCarousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                carouselHandler.removeCallbacks(carouselRunnable)
                carouselHandler.postDelayed(carouselRunnable, 4000)
            }
        })
    }

    private fun setupRecycler() {
        adapter = ProductAdapter(filteredList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    private fun fetchProducts() {
        progressBar.isVisible = true
        Volley.newRequestQueue(this).add(StringRequest(url,
            { response ->
                try {
                    val arr = JSONArray(response)
                    productList.clear()
                    productList.addAll(ProductAdapter.fromJsonArray(arr))
                    filterProducts("")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Data Error", e)
                }
                progressBar.isVisible = false
            },
            { progressBar.isVisible = false; toast("Connection failed") }
        ))
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText?.trim()?.lowercase() ?: "")
                return true
            }
        })
    }

    private fun filterProducts(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(productList)
            txtTrending.text = getString(R.string.trending_now)
        } else {
            val results = productList.filter {
                it.product_name.lowercase().contains(query) ||
                it.product_description?.lowercase()?.contains(query) == true
            }
            filteredList.addAll(results)
            txtTrending.text = getString(R.string.search_results, query)
        }
        adapter.notifyDataSetChanged()
    }

    private fun setupCategoryListeners() {
        btnShopNow.setOnClickListener { filterProducts("") }
        btnPhones.setOnClickListener { filterByCategory("phone") }
        btnLaptops.setOnClickListener { filterByCategory("laptop") }
        btnGaming.setOnClickListener { filterByCategory("gaming") }
    }

    private fun filterByCategory(category: String) {
        filterProducts(category)
        recyclerView.smoothScrollToPosition(0)
    }

    private fun setupMenu() {
        toggleMenu.setOnClickListener { if (topMenu.isVisible) hideMenu() else showMenu() }
        overlay.setOnClickListener { hideMenu() }
    }

    private fun showMenu() {
        topMenu.isVisible = true
        overlay.isVisible = true
    }

    private fun hideMenu() {
        topMenu.isVisible = false
        overlay.isVisible = false
    }

    private fun setupScrollButton() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (rv.computeVerticalScrollOffset() > 300) btnTop.show() else btnTop.hide()
            }
        })
        btnTop.setOnClickListener { recyclerView.smoothScrollToPosition(0) }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        carouselHandler.removeCallbacks(carouselRunnable)
        super.onDestroy()
    }
}
