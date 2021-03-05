package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.network.domain.ApiStatus

/**
 * Used to change the visibility of a [ProgressBar] depending on the [ApiStatus].
 */
@BindingAdapter("apiStatus")
fun bindApiStatus(progressbar: ProgressBar, apiStatus: ApiStatus?) {
    when (apiStatus) {
        ApiStatus.LOADING -> {
            progressbar.visibility = View.VISIBLE
        }
        ApiStatus.DONE -> {
            progressbar.visibility = View.GONE
        }
        else -> progressbar.visibility = View.GONE
    }
}