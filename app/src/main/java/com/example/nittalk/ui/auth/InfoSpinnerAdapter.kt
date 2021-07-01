package com.example.nittalk.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.nittalk.R
import kotlinx.android.synthetic.main.item_info_spinner.view.*

class InfoSpinnerAdapter(context: Context, list: ArrayList<String>) :
    ArrayAdapter<String>(context, 0, list) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val currentItem = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(R.layout.item_info_spinner, parent, false)

        view.title_TV.text = currentItem

        return view
    }
}