package com.codecx.educationsystem.dataclasses

data class UserDataClass(
    var uId: String? = null,
    var name: String? = null,
    var phoneNo: String? = null,
    var userType: String? = null,
    var email:String?=null,
    var password:String?=null
){
    override fun toString(): String {
        return "Name = $name\nPhoneNo = $phoneNo\nAccount Type = $userType\nEmail = $email"
    }
}