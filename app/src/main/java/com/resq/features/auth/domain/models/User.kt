package com.resq.features.auth.domain.models

data class User (
  val username: String, 
  val phone: String, 
  val uid: String, 
  val navigateToHome: Boolean
) 