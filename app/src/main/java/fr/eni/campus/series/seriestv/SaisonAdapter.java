package fr.eni.campus.series.seriestv;

import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.eni.campus.series.seriestv.model.Saison;

public class SaisonAdapter extends RecyclerView.Adapter<SaisonAdapter.SaisonHolder> {
    private List<Saison> saisons;
    private String image;
    private SaisonAdapter.OnClick listener;

    private int widthParent;
    private int heightParent;

    public SaisonAdapter(List<Saison> saisons, String image, OnClick listener) {
        this.saisons = saisons;
        this.image = image;
        this.listener = listener;
    }

    @Override
    public SaisonAdapter.SaisonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saison, parent, false);
        SaisonHolder saisonHolder = new SaisonHolder(view);

        float density = parent.getResources().getDisplayMetrics().density;
        widthParent = parent.getWidth();
        if(view.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            heightParent = (int)(150 * density + 0.5f);
        else
            heightParent = (int)(100 * density + 0.5f);

        return saisonHolder;
    }

    @Override
    public void onBindViewHolder(SaisonAdapter.SaisonHolder holder, int position) {
        final Saison currentSaison = saisons.get(position);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { listener.onItemClickListener(currentSaison);
            }
        });
        holder.imageView.getLayoutParams().width = widthParent - 50;
        holder.imageView.getLayoutParams().height = heightParent;
        holder.imageView.requestLayout();
        if(image == null)
            image = currentSaison.getSerie().getImageUrl();
        Picasso.get().load(image).fit().centerInside().into(holder.imageView);
        holder.infoSaison.setText("Saison " + currentSaison.getNumber() + " - " +
            currentSaison.getEpisodes().size() + (currentSaison.getEpisodes().size() > 0 ? " épisodes" : " épisode")
        );
    }

    @Override
    public int getItemCount() {
        return saisons.size();
    }

    public class SaisonHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView infoSaison;
        protected View parent;

        public SaisonHolder(View itemView) {
            super(itemView);
            parent = itemView;
            imageView = itemView.findViewById(R.id.img_saison);
            infoSaison = itemView.findViewById(R.id.info_saison);
        }
    }

    public interface OnClick {
        void onItemClickListener(Saison saison);
    }
}
