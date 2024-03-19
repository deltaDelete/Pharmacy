package ru.deltadelete.pharmacy.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.deltadelete.pharmacy.R
import ru.deltadelete.pharmacy.api.dto.Drug
import ru.deltadelete.pharmacy.databinding.DrugItemBinding

class DrugAdapter
    : PagingDataAdapter<Drug, DrugAdapter.ViewHolder>(
    DrugDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DrugItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrugAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(
        private val binding: DrugItemBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {
        private val radius = binding.root.context.resources.getDimension(R.dimen.eight).toInt()
        fun bind(item: Drug?) {
            item?.let {
                Glide.with(binding.root)
                    .load(item.image)
                    .transform(RoundedCorners(radius))
                    .into(binding.drugImage)

                binding.textviewDrugName.text = item.name
                binding.textviewDrugPrice.text = String.format("%.2f₽", item)
            }
        }
    }
}

class DrugDiffCallback : DiffUtil.ItemCallback<Drug>() {
    override fun areItemsTheSame(oldItem: Drug, newItem: Drug): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Drug, newItem: Drug): Boolean {
        return oldItem == newItem
    }

}