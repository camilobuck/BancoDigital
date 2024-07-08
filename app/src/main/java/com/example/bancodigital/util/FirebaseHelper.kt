package com.example.bancodigital.util

import com.example.bancodigital.R
import com.google.firebase.auth.FirebaseAuth

class FirebaseHelper {

    companion object {
        fun isAuthenticated() = FirebaseAuth.getInstance().currentUser != null
        fun validError(error: String): Int {
            return when {
                error.contains
                    ("The supplied auth credential is incorrect, malformed or has expired") -> {
                    R.string.account_not_registered_register_fragment
                }

                error.contains("The email address is badly formatted") -> {
                    R.string.invalid_email_register_fragment
                }

                error.contains("The password is invalid") -> {
                    R.string.invalid_email_register_fragment
                }

                error.contains("The email address is already in use by another account") -> {
                    R.string.email_in_use_register_fragment
                }

                error.contains("Password should be at least 6 characters") -> {
                    R.string.strong_password_register_fragment
                }

                else -> {
                    R.string.error_generic
                }
            }
        }
    }
}