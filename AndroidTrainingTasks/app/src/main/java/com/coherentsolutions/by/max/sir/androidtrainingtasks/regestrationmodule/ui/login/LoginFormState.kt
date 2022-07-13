package com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false,
    val emailError: Int? = null,
    val phoneError: Int? = null,
    val lastnameError: Int? = null,
    val firstnameError: Int? = null
)