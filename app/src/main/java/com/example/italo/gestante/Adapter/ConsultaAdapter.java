package com.example.italo.gestante.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.italo.gestante.R;
import com.example.italo.gestante.model.Consulta;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.MyViewHolder> {

    private ArrayList<Consulta> consultaList;
    private Context context;

    public ConsultaAdapter(ArrayList<Consulta> listaConsulta, Context c) {
        this.consultaList = listaConsulta;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemConsulta = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_consulta, parent, false);

        return new MyViewHolder(itemConsulta);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Consulta consulta = consultaList.get(position);

        holder.consulta.setText(consulta.getEspecialidade());
        holder.local.setText(consulta.getLocal());
        holder.data.setText(consulta.getData());
        holder.hora.setText(consulta.getHora());


    }

    @Override
    public int getItemCount() {
        return consultaList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView consulta,data,local,hora;


        public MyViewHolder(View itemView) {
            super(itemView);

            consulta = (TextView) itemView.findViewById(R.id.NomeConsulta);
            data = (TextView) itemView.findViewById(R.id.dataConsulta);
            local = (TextView) itemView.findViewById(R.id.localConsulta);
            hora = (TextView) itemView.findViewById(R.id.horaConsulta);

        }
    }
}
