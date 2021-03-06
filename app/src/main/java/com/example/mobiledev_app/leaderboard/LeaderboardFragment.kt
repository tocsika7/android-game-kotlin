package com.example.mobiledev_app.leaderboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.mobiledev_app.R
import com.example.mobiledev_app.database.ResultDatabase
import com.example.mobiledev_app.databinding.FragmentLeaderboardBinding
import com.example.mobiledev_app.title.TitleFragmentDirections


class LeaderboardFragment : Fragment() {


    private lateinit var viewModel: LeaderboardViewModel
    private lateinit var viewModelFactory: LeaderboardViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLeaderboardBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_leaderboard,
            container,
            false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = ResultDatabase.getInstance(application).resultDatabaseDao

        viewModelFactory = LeaderboardViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeaderboardViewModel::class.java)
        binding.leaderBoardViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = ResultAdapter(ResultListener { resultId ->
            viewModel.onResultClicked(resultId)
        })

        viewModel.navigateToResultDetail.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                this.findNavController().navigate(LeaderboardFragmentDirections.actionLeaderboardFragmentToResultDetailFragment(result))
                Log.i("LeaderBoard", "Navigating to a RecyclerView list item's detail screen")
                viewModel.onResultNavigated()
            }
        })

        viewModel.navigateToTitle.observe(viewLifecycleOwner, Observer { nav ->
            if (nav) {
                Log.i("LeaderBoard", "Navigating to the Title Screen")
                navigateToTitle()
            }
        })

        binding.resultList.adapter = adapter

        viewModel.results.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })
        
        return binding.root
    }

    private fun navigateToTitle(){
        NavHostFragment.findNavController(this).navigate(
            LeaderboardFragmentDirections.actionLeaderboardFragmentToTitleFragment()
        )
        viewModel.onTitleNavigated()
    }

}