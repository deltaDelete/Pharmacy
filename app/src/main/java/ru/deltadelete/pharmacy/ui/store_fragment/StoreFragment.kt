package ru.deltadelete.pharmacy.ui.store_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.deltadelete.pharmacy.R
import ru.deltadelete.pharmacy.api.ApiClient
import ru.deltadelete.pharmacy.api.dto.Drug
import ru.deltadelete.pharmacy.databinding.FragmentStoreBinding
import ru.deltadelete.pharmacy.paging.DrugAdapter
import ru.deltadelete.pharmacy.paging.DrugRemoteDataSource

class StoreFragment : Fragment() {

    private lateinit var adapter: DrugAdapter
    private lateinit var binding: FragmentStoreBinding
    private val viewModel: StoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        // https://github.com/google/flexbox-layout
        binding.recyclerView.layoutManager =
            FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).apply {
                // По дополнительной оси, т.е. вертикально
                alignItems = AlignItems.STRETCH
                // По главное оси, в данном случае row, т.е. горизонтально
                justifyContent = JustifyContent.CENTER
            }

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.HORIZONTAL
            ).apply {
                ResourcesCompat.getDrawable(context.resources, R.drawable.divider, null)?.let {
                    this.setDrawable(it)
                }
            }
        )
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            ).apply {
                ResourcesCompat.getDrawable(context.resources, R.drawable.divider, null)?.let {
                    this.setDrawable(it)
                }
            }
        )

        initRecyclerView()
        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getDrugs().collectLatest {
                binding.root.isRefreshing = false
                adapter.submitData(it)
            }
        }
    }

    private fun initRecyclerView() {
        binding.root.isRefreshing = true
        binding.root.setOnRefreshListener {
            adapter.refresh()
        }
        binding.root.setColorSchemeColors(0x0099cc)
        adapter = DrugAdapter().apply {
            onItemClick = { item, _ ->
                val itemAsJson = jacksonObjectMapper().writeValueAsString(item)
                findNavController().navigate(
                    R.id.action_storeFragment_to_drugInfoFragment,
                    bundleOf("item" to itemAsJson)
                )
            }
        }
        adapter.addOnPagesUpdatedListener {
            binding.root.isRefreshing = false
        }
        binding.recyclerView.adapter = adapter
    }
}

class StoreViewModel : ViewModel() {

    private val drugRemoteDataSource = DrugRemoteDataSource(ApiClient.drugService)

    fun getDrugs(): Flow<PagingData<Drug>> {
        return drugRemoteDataSource.getDrugs()
    }
}
