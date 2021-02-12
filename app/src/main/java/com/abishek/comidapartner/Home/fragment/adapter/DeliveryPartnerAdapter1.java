package com.abishek.comidapartner.Home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.Home.deliveryPartner.DeliveryPartnerModel;
import com.abishek.comidapartner.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_IMAGE;

public class DeliveryPartnerAdapter1 extends RecyclerView.Adapter<DeliveryPartnerAdapter1.DeliveryPartnerHolder> {

    private List<DeliveryPartnerModel> partnerList;
    private Context context;
    private OrderAdapter2.PartnerSelectionListener partnerSelectionListener;

    public DeliveryPartnerAdapter1(List<DeliveryPartnerModel> partnerList, Context context,
                                   OrderAdapter2.PartnerSelectionListener partnerSelectionListener) {
        this.partnerList = partnerList;
        this.context = context;
        this.partnerSelectionListener=partnerSelectionListener;
    }

    @NonNull
    @Override
    public DeliveryPartnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_partner_recycler_item1, parent, false);
        return new DeliveryPartnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryPartnerHolder holder, int position) {

        holder.nameView.setText(partnerList.get(position).getName());
        holder.mobileView.setText(partnerList.get(position).getMobile());
        if(!partnerList.get(position).getProfileImage().equals("null"))
            Picasso.get().load(BASE_IMAGE+partnerList.get(position).getProfileImage()).into(holder.profileImageView);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               partnerSelectionListener.selectedPosition(position,partnerList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return partnerList.size();
    }

    public class DeliveryPartnerHolder extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView mobileView;
        private CardView card;
        private ImageView profileImageView;

        public DeliveryPartnerHolder(@NonNull View v) {
            super(v);

            nameView = v.findViewById(R.id.name);
            mobileView = v.findViewById(R.id.mobile);
            card = v.findViewById(R.id.card);
            profileImageView = v.findViewById(R.id.profile_image);
        }
    }
}
