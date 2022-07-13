package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pet

import android.Manifest
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.coherentsolutions.by.max.sir.androidtrainingtasks.BuildConfig
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.PetFragmentBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PetFragment : Fragment() {

    @Inject
    lateinit var viewModel: PetViewModel
    private lateinit var binding: PetFragmentBinding

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                this.findNavController()
                    .navigate(PetFragmentDirections.actionPetFragmentToPetsFragment())
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(false)
        binding = DataBindingUtil.inflate(inflater, R.layout.pet_fragment, container, false)
        this.activity?.requestPermissions(arrayOf(Manifest.permission.CAMERA), 5000)
        val args = PetFragmentArgs.fromBundle(requireArguments())
        viewModel.passArg(args.pet)

        binding.imageView.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.choose_src_img_title_dialog))
                .setMessage(getString(R.string.choose_src_message_text))
                .setPositiveButton(getString(R.string.camera_text)) { dialog, _ ->
                    takeImage()
                    val path = latestTmpUri?.toString()
                    if (path != null) {
                        val bitmap = (previewImage.drawable as VectorDrawable).toBitmap()
                        viewModel.upload(bitmap)
                    }
                    dialog.cancel()
                    this.findNavController()
                        .navigate(PetFragmentDirections.actionPetFragmentToPetsFragment())

                }
                .setNegativeButton(getString(R.string.gallery_text)) { dialog, _ ->
                    selectImageFromGallery()
                    val path = latestTmpUri?.toString()
                    if (path != null) {
                        val bitmap = (previewImage.drawable as VectorDrawable).toBitmap()
                        viewModel.upload(bitmap)
                    }
                    dialog.cancel()
                    this.findNavController()
                        .navigate(PetFragmentDirections.actionPetFragmentToPetsFragment())
                }
                .create().show()
        }
        binding.notifyButton.setOnClickListener {
            NotificationHelper.scheduleNotification(
                requireContext(),
                10000,
                args.pet.id.toInt(),
                getString(R.string.remind_title_to_feed),
                getString(
                    R.string.reminder_text_body,
                    (args.pet.category?.name ?: "pet"),
                    args.pet.name
                )
            )
        }
        binding.petName.text = args.pet.name
        return binding.root
    }


    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    previewImage.setImageURI(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { previewImage.setImageURI(uri) }
        }

    private var latestTmpUri: Uri? = null

    private val previewImage by lazy { binding.imageView }


    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }


}