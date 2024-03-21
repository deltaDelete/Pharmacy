package ru.deltadelete.pharmacy.ui.drug_info_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.deltadelete.pharmacy.R
import ru.deltadelete.pharmacy.api.ApiClient
import ru.deltadelete.pharmacy.api.dto.Drug
import ru.deltadelete.pharmacy.api.dto.WarehouseDto
import ru.deltadelete.pharmacy.databinding.FragmentDrugInfoBinding
import ru.deltadelete.pharmacy.databinding.ItemWarehouseStockBinding

class DrugInfoFragment : Fragment() {

    private lateinit var adapter: WarehousesAdapter
    private lateinit var binding: FragmentDrugInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDrugInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemJson = arguments?.getString("item")
        if (itemJson.isNullOrEmpty()) {
            findNavController().popBackStack()
            return
        }
        val item = jacksonObjectMapper().readValue<Drug>(itemJson)
        binding.textviewDrugName.text = item.name
        binding.textviewDrugPrice.text = String.format("%.2f руб.", item.price)
        binding.textviewDrugDescription.text = item.description

        adapter = WarehousesAdapter(mutableListOf())

        val context = requireContext()
        (binding.recyclerViewWarehouses.layoutManager as LinearLayoutManager).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.recyclerViewWarehouses.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                ResourcesCompat.getDrawable(context.resources, R.drawable.divider, null)?.let {
                    setDrawable(it)
                }
            }
        )
        binding.recyclerViewWarehouses.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val response = ApiClient.drugService.getDrugIdAmount(item.id)
            response.body()?.let { drugAvailabilities ->
                drugAvailabilities.firstOrNull()?.let {
                    launch(Dispatchers.Main) {
                        adapter.addAll(it.warehouses)
                    }
                }
            }
        }
    }
}

class WarehousesAdapter(
    val list: MutableList<WarehouseDto>
) : RecyclerView.Adapter<WarehousesAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: ItemWarehouseStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: WarehouseDto, position: Int) {
            binding.textviewWarehouseName.text = item.name
            binding.textviewQuantity.text = item.quantity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemWarehouseStockBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position], position)
    }

    @Synchronized
    fun addAll(items: List<WarehouseDto>) {
        val before = list.size
        list.addAll(items)
        notifyItemRangeInserted(before, items.size)
    }
}