package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.user

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.UserFragmentBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.State
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService.SERVER_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.regestrationmodule.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_ui_view_layout.view.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
@AndroidEntryPoint
class UserFragment : Fragment() {

    @Inject
    lateinit var viewModel: UserViewModel

    private lateinit var binding: UserFragmentBinding
    private val lifecycleOwner = this

    @SuppressLint("UseRequireInsteadOfGet", "FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        Timber.tag(INFO_TAG).i("ON CREATE VIEW user fragment")

        Timber.tag(INFO_TAG).i("User loaded to fragment")
        Timber.tag(SERVER_TAG).i("UPDATING USER")

        Timber.tag(SERVER_TAG).v("USER UPDATED SUCCESSFULLY")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.user_fragment, container, false)
        binding.userViewModel = viewModel

        binding.updateUserSwipe.setOnRefreshListener {
            viewModel.updateUserAfterSignIn()
            binding.updateUserSwipe.isRefreshing = false
        }

        viewModel.nullUserEvent.observe(this, { isNull ->
            if (isNull == true) {
                startActivity(Intent(this.requireContext(), LoginActivity::class.java))
                this.requireActivity().finish()
            }
        })

        binding.customViewButtonDelete.custom_button.setOnClickListener {
            createDeleteAlertDialog()
        }
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                usernameEditText.text?.apply {
                    clear()
                    append(user.username)
                }
                passwordEditText.text?.apply {
                    clear()
                    append(user.password)
                }
            }

        }

        Timber.tag(INFO_TAG).i("bound layout and fragment inflated")
        val loading = binding.loadingUser
        viewModel.loadingEvent.observe(lifecycleOwner, { loadingActive ->
            if (loadingActive) {
                loading.show()
            } else {
                loading.hide()
            }
        })



        return binding.root
    }

    private fun deleteEvent(state: State) {
        when (state) {
            State.SUCCEED -> {
                Timber.tag(SERVER_TAG).i("OBSERVE SUCCESSFUL")

                Toast.makeText(
                    activity,
                    getString(R.string.user) + getString(R.string.was_delete_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.apply {
                    clearCurrentUser()
                    clearCryptoStorage()
                }
                activity?.finish()
                startActivity(Intent(context, LoginActivity::class.java))

            }
            State.FAILED -> {
                Timber.tag(SERVER_TAG).i("OBSERVE FAILED")
                Toast.makeText(
                    activity,
                    getString(R.string.user) + getString(R.string.delete_operation_was_failed_try_again),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.delete_user_menu_item)
        inflater.inflate(R.menu.options_user_fragment_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         *  TODO if will use intent: "val username = this.activity?.intent?.getStringExtra(USERNAME).toString()"
         *  */
        viewModel.updateUserAfterSignIn()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_user_menu_item -> createDeleteAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    fun createDeleteAlertDialog() {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.user_delete_alert_dialog_title))
            .setMessage(getString(R.string.alert_dialog_title_delete_user))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                viewModel.deleteUser(::deleteEvent)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uiScope.launch {
            viewModel.delete(username = binding.usernameEditText.text.toString())
        }
    }
}