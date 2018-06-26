package fr.eni.campus.series.seriestv;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.eni.campus.series.seriestv.model.Serie;

class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieHolder> {
    private List<Serie> series;

    public SerieAdapter(List<Serie> series) {
        this.series = series;
    }

    @Override
    public SerieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie, parent, false);
        SerieHolder serieHolder = new SerieHolder(view);
        return serieHolder;
    }

    @Override
    public void onBindViewHolder(SerieHolder holder, int position) {
        Serie currentSerie = series.get(position);
        holder.text.setText(currentSerie.toString());
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public List<Serie> getItems() {
        return series;
    }

    public class SerieHolder extends RecyclerView.ViewHolder {
        protected TextView text;

        public SerieHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
        }
    }
}
