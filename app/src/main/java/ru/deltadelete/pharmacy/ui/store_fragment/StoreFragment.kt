package ru.deltadelete.pharmacy.ui.store_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
                alignItems = AlignItems.FLEX_START
            }

        initRecyclerView()
        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getDrugs().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun initRecyclerView() {
        adapter = DrugAdapter()
        binding.recyclerView.adapter = adapter
    }
}

class StoreViewModel : ViewModel() {

    private val drugRemoteDataSource = DrugRemoteDataSource(ApiClient.drugService)

    fun getDrugs(): Flow<PagingData<Drug>> {
        return drugRemoteDataSource.getDrugs()
    }
}
