package com.krisgun.vibra.ui.history

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
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

private const val TAG = "HistoryView"

class HistoryFragment : Fragment(), RecyclerViewClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var navController: NavController
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sortListIconView: View
    private var measurementList: MutableList<Measurement> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        historyAdapter = HistoryAdapter(measurementList, this)
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

        recycler_view_measurements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        viewModel.measurementsData.observe(viewLifecycleOwner,
                Observer { measurements ->
                    measurementList.clear()
                    measurementList.addAll(measurements.sortedWith(viewModel.listComparator))
                    //Log.d(TAG, "Observer data: \n${measurementList.joinToString(limit = 2)}")
                    historyAdapter.notifyDataSetChanged()
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        //sortListIconView = menu.findItem(R.id.sort_list).actionView
       // sortListIconView = requireActivity().findViewById(R.id.sort_list)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_app_bar_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_list -> {
                viewModel.setComparatorToDateAscending()
                measurementList.sortWith(viewModel.listComparator)
                historyAdapter.notifyDataSetChanged()
                //showSortMenu(sortListIconView.rootView)
            }
            else -> {
                return item.onNavDestinationSelected(navController)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSortMenu(view: View) {
        PopupMenu(context, view).apply {
            setOnMenuItemClickListener(this@HistoryFragment)
            inflate(R.menu.bottom_nav_menu)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_name -> {
                true
            }
            R.id.sort_date -> {
                true
            }
            else -> false
        }
    }
}