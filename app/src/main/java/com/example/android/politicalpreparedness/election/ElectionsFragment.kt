package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.domain.ApiStatus
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * A [Fragment] that displays upcoming and saved elections.
 */
class ElectionsFragment : Fragment() {
    private lateinit var savedElectionListAdapter: ElectionListAdapter
    private lateinit var upcomingElectionListAdapter: ElectionListAdapter

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this, ElectionsViewModelFactory(requireActivity().application)).get(ElectionsViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.navigateToVoterInformation.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                navigateToVoterInformation(it)
                viewModel.navigateToVoterInformationCompleted()
            }
        })

        savedElectionListAdapter = ElectionListAdapter(ElectionListener {
            viewModel.navigateToVoterInformation(it)
        })

        upcomingElectionListAdapter = ElectionListAdapter(ElectionListener {
            viewModel.navigateToVoterInformation(it)
        })

        binding.upcomingElectionsRecyclerView.adapter = upcomingElectionListAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionListAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer<List<Election>> { upcomingElections ->
            upcomingElections?.apply {
                upcomingElectionListAdapter.elections = upcomingElections
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer<List<Election>> { savedElections ->
            savedElections?.apply {
                savedElectionListAdapter.elections = savedElections
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer<ApiStatus> { apiStatus ->
            if (apiStatus == ApiStatus.ERROR) {
                Snackbar.make(requireView(), R.string.error_upcoming_elections, Snackbar.LENGTH_LONG).show()
            }
        })

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshElections()
    }

    private fun navigateToVoterInformation(election: Election) {
        this.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
    }
}