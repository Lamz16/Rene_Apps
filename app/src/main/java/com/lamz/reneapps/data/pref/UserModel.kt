package com.lamz.reneapps.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val email: String,
    val name : String,
    var token: String,
    val isLogin: Boolean = false
) : Parcelable