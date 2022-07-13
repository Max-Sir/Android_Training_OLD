package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.uiScope
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.databinding.PetsFragmentBinding
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pet.PetFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PetsFragment : Fragment() {


    @Inject lateinit var viewModel: PetsViewModel
    private lateinit var binding: PetsFragmentBinding
    private lateinit var adapter: PetsAdapter

    companion object {
        fun newInstance() = PetFragment()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getAllPets()
        binding = DataBindingUtil.inflate(inflater, R.layout.pets_fragment, container, false)
        binding.petsViewModel = viewModel

        viewModel.eventLoading.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.loadingPets.show()
            } else {
                binding.loadingPets.hide()
            }
        }

        adapter = PetsAdapter(PetsAdapter.OnClickListener { petResponse ->
            viewModel.displayPetDetails(petResponse)
        })
        /** TODO if you want swipe Gestures:
         *     val swipeGesture= object : NotifyGesture(){
         *
         *                 override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
         *                     when(direction){
         *                         ItemTouchHelper.LEFT ->{
         *                             Toast.makeText(requireContext(),"Notification Created",Toast.LENGTH_SHORT).show()
         *
         *                         }
         *                         ItemTouchHelper.RIGHT ->{
         *                             Toast.makeText(requireContext(),"Notification Prolonged",Toast.LENGTH_SHORT).show()
         *
         *                         }
         *                     }
         *
         *                     super.onSwiped(viewHolder, direction)
         *                 }
         *
         *             }
         *     val itemTouchHelper = ItemTouchHelper(swipeGesture)
         *     itemTouchHelper.attachToRecyclerView(binding.recyclerView)
         */

        binding.swipe.setOnRefreshListener (object:SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                viewModel.getAllPets()
                binding.swipe.isRefreshing=false
            }
        })

        viewModel.navigateToPetDetailsHelper.observe(viewLifecycleOwner, { pet ->
            this.findNavController()
                .navigate(PetsFragmentDirections.actionPetsFragmentToPetFragment(pet))
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        viewModel.pets.observe(viewLifecycleOwner) { pets ->

            uiScope.launch {
                adapter.submitList(pets)
            }
        }

        return binding.root
    }

}