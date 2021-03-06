package com.example.practica.lista_despachos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.practica.CrearDespachoActivity;
import com.example.practica.FirebaseMethods;
import com.example.practica.ListaDespachosActivity;
import com.example.practica.MapsActivity;
import com.example.practica.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FirebaseDespachoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;
    Activity mActivity;
    classDespacho mDespacho;

    public FirebaseDespachoViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        mActivity = (Activity) mContext;
        itemView.setOnClickListener(this);
    }

    public void bindUser(classDespacho desp, int pos) {
        this.mDespacho = desp;
        TextView tvTitle = (TextView) mView.findViewById(R.id.tvTitle);
        TextView tvLabel = (TextView) mView.findViewById(R.id.tvLabel1);

        tvTitle.setText("Despacho " + desp.getEmpresa());

        Log.d("adapter - empresa ", desp.getEmpresa());
        Log.d("adapter - destinos ", String.valueOf(desp.getDestinos()));

        int i = 1;
        String[] destinosArray = desp.getDestinos().split("<");
        String s = "";

        for(String dest : destinosArray){
            String[] latLng = dest.split(";");
            Double lat = Double.valueOf(latLng[0]);
            Double lng = Double.valueOf(latLng[1]);

            String addr = MapsActivity.getAddress(mContext, lat, lng, 1);

            s += "<b>Destino " + i + ": </b>" + addr + "<br>";

            i++;
        }

        tvLabel.setText(Html.fromHtml(s));
    }

    @Override
    public void onClick(View view) {

        final AlertDialog alertD = new AlertDialog.Builder(mContext).create();

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_see_despacho, null);

        Button btnAgregar = (Button) popupView.findViewById(R.id.btnIniciarViaje);
        btnAgregar.setText("Agregar a mis despachos");
        TextView tvX = popupView.findViewById(R.id.tvX);

        TextView label1 = popupView.findViewById(R.id.tvLabel1);
        TextView label2 = popupView.findViewById(R.id.tvLabel2);
        TextView label3 = popupView.findViewById(R.id.tvLabel3);
        TextView label4 = popupView.findViewById(R.id.tvLabel4);

        String empresa = "<b>Empresa: </b>" + mDespacho.getEmpresa();
        String fecha = "<b>Fecha: </b>" + mDespacho.getFecha();
        String nombre = "<b>A nombre de: </b>" + mDespacho.getNombre1();
        String valor = "<b>Valor: </b>" + mDespacho.getValor();

        label1.setText(Html.fromHtml(empresa));
        label2.setText(Html.fromHtml(nombre));
        label3.setText(Html.fromHtml(fecha));
        label4.setText(Html.fromHtml(valor));

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMethods.updateDespacho(mDespacho.getId(), 1);
                alertD.cancel();
            }
        });

        tvX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertD.cancel();
            }
        });

        alertD.setView(popupView);
        alertD.show();
    }
}
