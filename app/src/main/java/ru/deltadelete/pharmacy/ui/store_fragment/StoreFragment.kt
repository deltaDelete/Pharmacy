package ru.deltadelete.pharmacy.ui.store_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.DefaultSpanSizeLookup
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.deltadelete.pharmacy.api.Drug
import ru.deltadelete.pharmacy.databinding.FragmentStoreBinding

class StoreFragment : Fragment() {

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
        val adapter = DrugAdapter(viewModel.items.toMutableList())
        binding.recyclerView.adapter = adapter
        // https://github.com/google/flexbox-layout
        binding.recyclerView.layoutManager =
            FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP).apply {
                alignItems = AlignItems.FLEX_START
            }

        lifecycleScope.launch {
            viewModel.itemEvents.collectLatest {
                when (it) {
                    is ItemEvent.NewItems -> {
                        adapter.addAll(it.items)
                    }

                    is ItemEvent.RemoveItem -> {
                        adapter.remove(it.item)
                    }

                    else -> {}
                }
            }
        }
    }
}

class StoreViewModel : ViewModel() {
    val items = listOf(
        Drug(1, "Парацетамол", 99.9f),
        Drug(2, "Кашлеуберин", 49.9f),
        Drug(3, "Насморковыводин", 199.9f),
    )

    val itemEvents = MutableStateFlow<ItemEvent<Drug>>(ItemEvent.Initial())
}

sealed class ItemEvent<T> {
    class Initial<T> : ItemEvent<T>()

    data class NewItems<T>(val items: List<T>) : ItemEvent<T>()
    data class RemoveItem<T>(val item: T, val index: Int) : ItemEvent<T>()
}

fun GridLayoutManager.setSpanSizeLookup(span: Int) {
    this.spanSizeLookup = object :
        SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = span
    }
}