package com.andbase.library.view.recycler;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public class AbDataBingRecyclerViewHolder extends RecyclerView.ViewHolder {
    private ViewDataBinding binding;

    public ViewDataBinding getBinding() {
        return binding;
    }

    public AbDataBingRecyclerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}

