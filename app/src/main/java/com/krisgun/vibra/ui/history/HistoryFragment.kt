package com.krisgun.vibra.ui.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment(), RecyclerViewClickListener {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        viewModel.measurementsData.observe(viewLifecycleOwner,
                Observer { measurements ->
                    recycler_view_measurements.also {
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = HistoryAdapter(measurements, this)
                        it.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
                    }
                }
        )
    }

    override fun onRecyclerViewItemClick(view: View, measurement: Measurement) {
        when (view.id) {
            R.id.list_item_more -> {
                val action = HistoryFragmentDirections
                        .actionNavigationHistoryToDetailsMenuDialog(measurement.id)
                navController.navigate(action)
            }

            R.id.constraint_layout -> {
                (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
                val action = HistoryFragmentDirections
                    .actionNavigationHistoryToNavigationDetails(measurement.id)
                navController.navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController)
                || return super.onOptionsItemSelected(item)
    }

}