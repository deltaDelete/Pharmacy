package ru.deltadelete.pharmacy.ui.store_fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.deltadelete.pharmacy.R
import ru.deltadelete.pharmacy.api.Drug
import ru.deltadelete.pharmacy.databinding.DrugItemBinding

class DrugAdapter(
    val list: MutableList<Drug>
) : RecyclerView.Adapter<DrugAdapter.ViewHolder>() {
    inner class ViewHolder(
        private val binding: DrugItemBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        private val radius = binding.root.context.resources.getDimension(R.dimen.eight).toInt()
        fun bind(item: Drug) {
            Glide.with(binding.root)
                .load(item.image)
                .transform(RoundedCorners(radius))
                .into(binding.drugImage)

            binding.textviewDrugName.text = item.name
            binding.textviewDrugPrice.text = String.format("%.2f₽", item.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DrugItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @Synchronized
    fun add(item: Drug) {
        list.add(item)
        notifyItemInserted(list.lastIndex)
    }


    @Synchronized
    fun remove(item: Drug) {
        val position = list.indexOf(item)
        if (list.remove(item)) {
            notifyItemRemoved(position)
        }
    }

    @Synchronized
    fun addAll(vararg items: Drug) {
        val before = list.lastIndex
        list.addAll(items)
        notifyItemRangeInserted(before, items.size)
    }

    @Synchronized
    fun addAll(items: List<Drug>) {
        val before = list.size
        list.addAll(items)
        notifyItemRangeInserted(before, items.size)
    }

    @Synchronized
    fun removeAll(vararg items: Drug) {
        list.removeAll(items.toSet())
    }

    @SuppressLint("notifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    @Synchronized
    @SuppressLint("notifyDataSetChanged")
    fun replaceAll(items: List<Drug>) {
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    @Synchronized
    fun appendStart(item: Drug) {
        list.add(0, item)
        notifyItemInserted(0)
    }

    @Synchronized
    fun appendStartAll(items: List<Drug>) {
        list.addAll(0, items)
        notifyItemRangeInserted(0, items.size)
    }

    val size: Int
        get() = list.size
}