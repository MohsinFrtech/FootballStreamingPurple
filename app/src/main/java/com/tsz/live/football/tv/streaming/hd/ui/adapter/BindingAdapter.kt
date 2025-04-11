package com.tsz.live.football.tv.streaming.hd.ui.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tsz.live.football.tv.streaming.hd.R

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, url: String?){
    if (!url.isNullOrEmpty()){
      Glide.with(imageView.context).load(url)
          .apply(RequestOptions()
              .placeholder(R.drawable.splash_icon)
              .error(R.drawable.splash_icon)).into(imageView)
    }
}