package com.guitar_tuner_tv.guitartunertv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sbarjola on 23/08/2018
 */
public class TuningTypeAdapter extends RecyclerView.Adapter<TuningTypeAdapter.ViewHolder> {

    private List<TuningType> tuningTypes;

    public TuningTypeAdapter(List<TuningType> tuningTypes) {
        this.tuningTypes = tuningTypes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.tuning_adapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(tuningTypes.get(position).getTuningName());
    }

    @Override
    public int getItemCount() {
        return tuningTypes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.adapterTuningName);
        }
    }
}