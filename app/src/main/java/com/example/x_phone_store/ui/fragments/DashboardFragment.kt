package com.example.x_phone_store.ui.fragments

import FirestoreClass
import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.x_phone_store.R
import com.example.x_phone_store.models.Product
import com.example.x_phone_store.ui.activities.CartListActivity
import com.example.x_phone_store.ui.activities.SearchActivity
import com.example.x_phone_store.ui.activities.SettingsActivity
import com.example.x_phone_store.ui.adapters.DashboardItemsListAdapter
import com.example.x_phone_store.ui.adapters.DashboardItemsPagerAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.Timer
import java.util.TimerTask


class DashboardFragment : BaseFragment() {

    private var timer: Timer? = null
    private var mDashboardItemsList = mutableListOf<String?>()
    private var currentPosition = 0

    private var timerTask: TimerTask? = null
    private val DELAY_MS: Long = 3000

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
/*        val colorAnimator = AnimatorInflater.loadAnimator(context, R.animator.fragment_color_animator)
        colorAnimator.setTarget(root)
        colorAnimator.start()*/

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_search ->{
                startActivity(Intent(activity, SearchActivity::class.java))
                return true
            }

            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }

            R.id.action_cart -> {

                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        if(timer!=null){
            //stopAutoSlider()
        }
    }

    override fun onResume() {
        super.onResume()

        startAutoSlide()

        getDashboardItemsList()
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)

        /*val customFont = Typeface.createFromAsset(context?.assets, "Bangers-Regular.ttf")
        tv_discount.typeface = customFont*/
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        hideProgressDialog()


        if (dashboardItemsList.size > 0) {
            if(dashboardItemsList.size > 2){
                showProgressDialog(resources.getString(R.string.please_wait))
                val db = FirebaseFirestore.getInstance()
                val imagesRef: CollectionReference = db.collection("slideshow_images")

                val imageUrls: MutableList<String?> = ArrayList()

                imagesRef.get().addOnSuccessListener { queryDocumentSnapshots ->

                    hideProgressDialog()

                    for (documentSnapshot in queryDocumentSnapshots) {
                        val imageUrlOne = documentSnapshot.getString("imageurl_one")
                        val imageUrlTwo = documentSnapshot.getString("imageurl_two")
                        val imageUrlThree = documentSnapshot.getString("imageurl_three")
                        imageUrls.add(imageUrlOne)
                        imageUrls.add(imageUrlThree)
                        imageUrls.add(imageUrlTwo)
                    }

                    view_pager.visibility = View.VISIBLE
                    dots_indicator.visibility = View.VISIBLE

                    mDashboardItemsList = imageUrls
                    val viewPager: ViewPager2 = view_pager
                    val adapter = DashboardItemsPagerAdapter(imageUrls)
                    viewPager.adapter = adapter

                    val dotsIndicator: DotsIndicator = dots_indicator
                    dotsIndicator.setViewPager2(viewPager)

                }.addOnFailureListener {e ->
                        hideProgressDialog()
                         Log.e("Slideshow", "Error getting documents: ", e) }

            }
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE
            /*discount_container.visibility = View.VISIBLE
            tv_discount.visibility = View.VISIBLE*/

            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val dashboardAdapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = dashboardAdapter

        } else {
            view_pager.visibility = View.GONE
            dots_indicator.visibility = View.GONE
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun startAutoSlide() {
        timerTask = object : TimerTask() {
            override fun run() {
                currentPosition = (currentPosition + 1) % 3 // Move to the next slide
                activity?.runOnUiThread {
                    view_pager.currentItem = currentPosition
                }
            }
        }
        timer = Timer()
        timer?.schedule(timerTask, DELAY_MS, DELAY_MS)
    }
    private fun stopAutoSlide() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null
    }

    override fun onPause() {
        super.onPause()
        stopAutoSlide()
    }

}