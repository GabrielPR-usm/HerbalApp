package com.example.practica.lista_despachos;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practica.R;

import java.util.ArrayList;

public class DespachosAdapter extends RecyclerView.Adapter<DespachosAdapter.DespachosViewHolder>{

    private ArrayList<despachoClass> data;
    //private OnDespachoListener mOnDespachoListener;

    public DespachosAdapter(ArrayList<despachoClass> data) {
            this.data = data;
            //this.mOnDespachoListener = onDespachoListener;
        }

        @Override
        public DespachosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            DespachosViewHolder despachosViewHolder = new DespachosViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_despachos_fragment, parent, false));
            return despachosViewHolder;
        }

    @Override
        public void onBindViewHolder(DespachosViewHolder holder, int position) {
            despachoClass despacho = data.get(position);
            holder.tvTitle.setText(despacho.getTitle());
            holder.tvLabel1.setText(despacho.getLabel1());
            holder.tvLabel2.setText(despacho.getLabel2());

       holder.llDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent startActivityIntent = new Intent(MainActivityCuidadores.this, CuidadorMonitorTemperature.class);

            }
        });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public despachoClass getDespacho(int position){

            return data.get(position);
        }

        class DespachosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tvTitle, tvLabel1, tvLabel2;
            LinearLayout llDespacho;

            public DespachosViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvLabel1 = (TextView) itemView.findViewById(R.id.tvLabel1);
                tvLabel2 = (TextView) itemView.findViewById(R.id.tvLabel2);
                llDespacho = (LinearLayout) itemView.findViewById(R.id.llDespachos);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                //onDespachoListener.onDespachoClick(getAdapterPosition());
            }
        }

        public interface OnDespachoListener{
            void onDespachoClick(int position);
        }
    }
