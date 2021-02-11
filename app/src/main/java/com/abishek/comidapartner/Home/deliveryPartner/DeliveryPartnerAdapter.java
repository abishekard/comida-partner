package com.abishek.comidapartner.Home.deliveryPartner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abishek.comidapartner.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DeliveryPartnerAdapter extends RecyclerView.Adapter<DeliveryPartnerAdapter.DeliveryPartnerHolder> {

    private List<DeliveryPartnerModel> partnerList;
    private Context context;

    public DeliveryPartnerAdapter(List<DeliveryPartnerModel> partnerList, Context context) {
        this.partnerList = partnerList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeliveryPartnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_partner_recycler_item, parent, false);
        return new DeliveryPartnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryPartnerHolder holder, int position) {

        holder.nameView.setText(partnerList.get(position).getName());
        holder.mobileView.setText(partnerList.get(position).getMobile());
        if(!partnerList.get(position).getProfileImage().equals("null"))
            Picasso.get().load(partnerList.get(position).getProfileImage()).into(holder.profileImageView);
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        private TextView btnRemove;
        private ImageView profileImageView;

        public DeliveryPartnerHolder(@NonNull View v) {
            super(v);

            nameView = v.findViewById(R.id.name);
            mobileView = v.findViewById(R.id.mobile);
            btnRemove = v.findViewById(R.id.btn_remove);
            profileImageView = v.findViewById(R.id.profile_image);
        }
    }
}
