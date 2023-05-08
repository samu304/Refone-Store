package com.example.x_phone_store.ui.activities

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.x_phone_store.R
import com.example.x_phone_store.models.Product
import com.example.x_phone_store.ui.adapters.DashboardItemsListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchView: SearchView = findViewById(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    val productsRef = FirebaseFirestore.getInstance().collection("products")


                    productsRef.whereGreaterThanOrEqualTo("title", query)
                        .whereLessThanOrEqualTo("title", query + '\uf8ff')
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            hideProgressDialog()

                            val products = mutableListOf<Product>()

                            for (document in querySnapshot.documents) {
                                val product = document.toObject(Product::class.java)
                                product?.let {
                                    it.product_id = document.id
                                        products.add(it)

                                }
                                Log.d("firebase", "Query submitted: $product")
                            }

                            if (products.isEmpty()) {
                                Toast.makeText(
                                    this@SearchActivity,
                                    "$query No such thing found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                successSearchedItemsList(products as ArrayList<Product>)
                            }
                        }
                        .addOnFailureListener { exception ->
                            hideProgressDialog()
                            Toast.makeText(this@SearchActivity, "$exception", Toast.LENGTH_SHORT)
                                .show()

                            Log.d("ProductFirebaseE", "Error searching for products: $exception")
                        }

                    //Log.d("Searched Text", "Query submitted: $query")
                } else {
                    Toast.makeText(
                        this@SearchActivity,
                        "Please enter something...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("QueryChanged", "$newText")
                return false
            }
        })

    }

    private fun successSearchedItemsList(searchedItemsList: ArrayList<Product>) {

        hideProgressDialog()

        if (searchedItemsList.size > 0) {

            recycler_view_search.visibility = View.VISIBLE
            ll_search_view_no_items.visibility = View.GONE

            recycler_view_search.layoutManager = GridLayoutManager(this@SearchActivity, 2)
            recycler_view_search.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(this@SearchActivity, searchedItemsList)
            recycler_view_search.adapter = adapter


        } else {
            recycler_view_search.visibility = View.GONE
            ll_search_view_no_items.visibility = View.VISIBLE
        }
    }

}