package fr.eni.campus.series.seriestv;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.eni.campus.series.seriestv.model.Episode;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder> {
    private static final DateFormat format = new SimpleDateFormat("d MMMM YYYY", Locale.FRANCE);
    private List<Episode> episodes;
    private String image;
    private EpisodeAdapter.OnClick listener;

    private int widthParent;
    private int heightParent;

    public EpisodeAdapter(List<Episode> episodes, String image, OnClick listener) {
        this.episodes = episodes;
        this.image = image;
        this.listener = listener;
    }

    @Override
    public EpisodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episode, parent, false);
        EpisodeHolder episodeHolder = new EpisodeHolder(view);

        float density = parent.getResources().getDisplayMetrics().density;
        widthParent = parent.getWidth();
        if(view.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            heightParent = (int)(150 * density + 0.5f);
        else
            heightParent = (int)(100 * density + 0.5f);

        return episodeHolder;
    }

    @Override
    public void onBindViewHolder(EpisodeHolder holder, int position) {
        final Episode currentEpisode = episodes.get(position);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { listener.onItemClickListener(currentEpisode);
            }
        });
        holder.imageView.getLayoutParams().width = widthParent - 50;
        holder.imageView.getLayoutParams().height = heightParent;
        holder.imageView.requestLayout();
        if(image == null)
            image = currentEpisode.getSaison().getSerie().getImageUrl();
        Picasso.get().load(image).fit().centerInside().into(holder.imageView);
        holder.titleEpisode.setText("Episode " + currentEpisode.getNumber() + " - " + currentEpisode.getTitle());
        holder.sortieEpisode.setText("Sorti le " + format.format(currentEpisode.getSortieDate()));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class EpisodeHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView titleEpisode;
        protected TextView sortieEpisode;
        protected View parent;

        public EpisodeHolder(View itemView) {
            super(itemView);
            parent = itemView;
            imageView = itemView.findViewById(R.id.img_episode);
            titleEpisode = itemView.findViewById(R.id.title_episode);
            sortieEpisode = itemView.findViewById(R.id.sortie_episode);
        }
    }

    public interface OnClick {
        void onItemClickListener(Episode episode);
    }
}
