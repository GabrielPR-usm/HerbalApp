package com.example.practica.lista_despachos;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practica.R;

public class FirebaseDespachoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;
    despacho mDespacho;

    public FirebaseDespachoViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindUser(despacho desp, int pos) {
        this.mDespacho = desp;
        TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        TextView tvLabel = (TextView) mView.findViewById(R.id.tvLabel1);

        tvTitle.setText("Despacho " + pos);
        tvLabel.setText(desp.getAddress());
    }

    @Override
    public void onClick(View view) {
        /*
        if (!userModel.isGroup()) {
            Intent intent = new Intent(mContext, ChatConverstion.class);
            intent.putExtra("chat_id", "" + userModel.getChat_id());
            intent.putExtra("reciverUserName", "" + userModel.getDisplayName());
            intent.putExtra("reciverProfilePic", "" + userModel.getProfilePic());
            intent.putExtra("reciverUid", "" + userModel.getUser_id());
            mContext.startActivity(intent);
        }
         */
        Log.d("RECICLER", String.valueOf(mDespacho.getAddress()));
    }
}
