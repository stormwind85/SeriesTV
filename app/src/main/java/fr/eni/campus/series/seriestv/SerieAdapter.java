package fr.eni.campus.series.seriestv;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eni.campus.series.seriestv.model.Saison;
import fr.eni.campus.series.seriestv.model.Serie;

class SerieAdapter extends RecyclerView.Adapter<SerieAdapter.SerieHolder> {
    private List<Serie> series;
    private ProgressBar beginProgressBar;

    private int widthParent;
    private int heightParent;
    private OnClick listener;


    public SerieAdapter(List<Serie> series, View beginProgressBar, OnClick listener) {
        this.series = series;
        this.beginProgressBar = (ProgressBar) beginProgressBar;
        this.listener = listener;
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
        final Serie currentSerie = series.get(position);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { listener.onItemClickListener(currentSerie);
            }
        });
        holder.image.getLayoutParams().width = widthParent - 50;
        holder.image.getLayoutParams().height = heightParent;
        holder.image.requestLayout();
        Picasso.get().load(currentSerie.getImageUrl()).fit().centerInside().into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
                beginProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError(Exception e) {
            }
        });
        holder.title.setText(currentSerie.getTitle());
        holder.rating.setRating(currentSerie.getNote().floatValue());
        int episodes = 0;
        for(Saison s : currentSerie.getSaisons()) {
            episodes += s.getEpisodes().size();
        }
        holder.infos.setText(currentSerie.getStatus().toString()  + " - " +
                currentSerie.getSaisons().size() + (currentSerie.getSaisons().size() > 1 ? " saisons" : " saison") + " - " +
                episodes + (episodes > 1 ? " épisodes" : " épisode")
        );
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public class SerieHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView title;
        protected RatingBar rating;
        protected TextView infos;
        protected View parent;

        public SerieHolder(View itemView) {
            super(itemView);
            parent = itemView;
            image = itemView.findViewById(R.id.img_serie);
            title = itemView.findViewById(R.id.title_serie);
            rating = itemView.findViewById(R.id.rating_serie);
            infos = itemView.findViewById(R.id.info_serie);
        }
    }

    public interface OnClick {
        void onItemClickListener(Serie serie);
    }
}
