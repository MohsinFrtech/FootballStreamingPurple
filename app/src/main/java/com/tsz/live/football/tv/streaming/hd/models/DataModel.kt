package com.tsz.live.football.tv.streaming.hd.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class DataModel(
    @Json(name = "app_ads") var app_ads: List<AppAd>?,
    @Json(name = "application_configurations") var application_configurations: List<ApplicationConfiguration>?,
    var build_no: String?,
    @Json(name = "categories") var categories: List<Category>?,
    @Json(name = "events") var events: List<Event>?,
    var live: Boolean?,
    var name: String?,
    var extra_1: String? = null,
    var app_version: String?,
    var app_update_text: String?,
    var url: String?,
    var extra_2: String? = null,
    var extra_3: String? = null,
    var is_permanent_dialog: Boolean = false

) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class AppAd(
    @Json(name = "ad_locations") var ad_locations: List<AdLocation>?,
    var ad_provider: String?,
    var enable: Boolean?,
    var otherad: String?,
    var time: String?,
    var ad_key:String?
) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class AdLocation(
    var title: String?
) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class ApplicationConfiguration(
    var key: String?,
    var value: String?
) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Category(
    var category_image_url: String?,
    @Json(name = "channels") var channels: List<Channel>?,
    var image_url: String?,
    var live: Boolean?,
    var name: String?,
    var priority: Int?,
    var thumbnail_image: String?,
    var web_image_url: String?
) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Channel(
    var channel_image_url: String?,
    var channel_type: String?,
    var image_url: String?,
    var live: Boolean?,
    var name: String?,
    var position: Int?,
    var date: String?,
    var priority: Int?,
    var url: String?,
    var web_image_url: String?,
    var initial_time:String?=null,
    var clear_key:String?,
    var user_agent:String?,
    var forwarded_for:String?,
    var country_codes:List<String>?=null,
    var isSelected: Boolean? = false
) : Parcelable

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Event(
    @Json(name = "channels") var channels: List<Channel>?,
    var event_image_url: String?,
    var image_url: String?,
    var live: Boolean?,
    var name: String?,
    var priority: Int?,
    var status: String?,
    var country_codes:List<String>?=null,
    var web_image_url: String?
) : Parcelable

@JsonClass(generateAdapter = true)
data class AllTeamsModel(
    var id: String?,
    var league_id: String?,
    var team_id: String?,
    var name: String?,
    var code: String?,
    var logo: String?,
    var country: String?,
    var founded: String?,
    var venue_name: String?,
    var venue_surface: String?,
    var venue_address: String?,
    var venue_city: String?,
    var venue_capacity: String?,
    var create_date: String?,
    var isFav: Boolean = false
)