package com.example.italo.gestante.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.italo.gestante.R;
import com.example.italo.gestante.model.Vacina;

import java.util.List;

public class VacinaAdapter extends RecyclerView.Adapter<VacinaAdapter.MyViewHolder> {

    private List<Vacina> listadeVacina;
    private Context context;

    public VacinaAdapter(List<Vacina> listaVacina, Context c) {
        this.listadeVacina = listaVacina;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vacina_list,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Vacina vacina = listadeVacina.get(position);

        holder.data.setText(vacina.getDatavacina());
        holder.hora.setText(vacina.getHoraVacina());
        holder.tipo.setText(vacina.getTipoVacina());
    }

    @Override
    public int getItemCount() {
        return listadeVacina.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView data,hora,tipo;

        public MyViewHolder(View itemView) {
        super(itemView);

        data = (TextView)itemView.findViewById(R.id.dataVacina);
        hora = (TextView)itemView.findViewById(R.id.horaVacina);
        tipo = (TextView)itemView.findViewById(R.id.tipoVacina);

        }
    }

}
