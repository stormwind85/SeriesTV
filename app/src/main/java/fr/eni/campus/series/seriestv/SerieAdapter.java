package fr.eni.campus.series.seriestv;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eni.campus.series.seriestv.model.Serie;

class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieHolder> {
    private List<Serie> series;
    private ProgressBar beginProgressBar;

    private int widthParent;
    private int heightParent;


    public SerieAdapter(List<Serie> series, View beginProgressBar) {
        this.series = series;
        this.beginProgressBar = (ProgressBar) beginProgressBar;
    }

    @Override
    public SerieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie, parent, false);
        SerieHolder serieHolder = new SerieHolder(view);

        float density = parent.getResources().getDisplayMetrics().density;
        widthParent = parent.getWidth();
        if(view.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            heightParent = (int)(150 * density + 0.5f);
        else
            heightParent = (int)(100 * density + 0.5f);

        return serieHolder;
    }

    @Override
    public void onBindViewHolder(SerieHolder holder, final int position) {
        Serie currentSerie = series.get(position);
        holder.imageView.getLayoutParams().width = widthParent - 50;
        holder.imageView.getLayoutParams().height = heightParent;
        holder.imageView.requestLayout();
        Picasso.get().load(currentSerie.getImageUrl()).fit().centerInside().into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                if(position == 0) {
                    beginProgressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(Exception e) {
            }
        });
        holder.textView.setText(currentSerie.getTitle() + " - " +
                currentSerie.getStatus().toString()  + " - " +
                currentSerie.getSaisons().size() + (currentSerie.getSaisons().size() > 1 ? " saisons" : " saison"));
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
        protected ImageView imageView;
        protected TextView textView;

        public SerieHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_serie);
            textView = itemView.findViewById(R.id.info_serie);
        }
    }
}
