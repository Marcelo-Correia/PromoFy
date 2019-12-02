package up.edu.br.promofy.views;

import up.edu.br.promofy.PromotionDetail;
import up.edu.br.promofy.helpers.ApplicationHelper;
import android.app.Application;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import up.edu.br.promofy.R;
import up.edu.br.promofy.helpers.ApplicationHelper;
import up.edu.br.promofy.models.Promotion;

public class PromotionRecyclerAdapter extends RecyclerView.Adapter<PromotionRecyclerAdapter.PromitionView> {

    private ArrayList<Promotion> promotions;

    public PromotionRecyclerAdapter(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public PromitionView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);

        return new PromitionView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromitionView holder, int position) {
        holder.txtDescription.setText(promotions.get(position).getShortDescription());
        holder.txtOriginalPrice.setText("Preço original: " + promotions.get(position).getOriginalPrice());
        holder.txtPromotionalPrice.setText("Preço promocional: " + promotions.get(position).getPromotionalPrice());
        holder.txtLocation.setText(promotions.get(position).getLocationName());
        holder.promotion = promotions.get(position);
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class PromitionView extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Promotion promotion;
        public TextView txtDescription;
        public TextView txtOriginalPrice;
        public TextView txtPromotionalPrice;
        public TextView txtLocation;

        public PromitionView(@NonNull View itemView) {
            super(itemView);

            // Set data in the view
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtOriginalPrice = itemView.findViewById(R.id.txtOriginalPrice);
            txtPromotionalPrice = itemView.findViewById(R.id.txtPromotionalPrice);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }

        @Override
        public void onClick(View v) {
            Intent pview = new Intent(ApplicationHelper.getContext(), PromotionDetail.class);
            pview.putExtra("promotion", promotion.getUid());
        }
    }

}
