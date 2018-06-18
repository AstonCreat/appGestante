package com.example.italo.gestante.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.italo.gestante.R;
import com.example.italo.gestante.model.AlertGestacao;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder> {

    private List<AlertGestacao> alert;
    private Context context;


    public AlertAdapter(List<AlertGestacao> listaAlert, Context c) {
        this.alert = listaAlert;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert, parent,false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AlertGestacao alertGestacao = alert.get(position);

        holder.autor.setText("Dr. "+alertGestacao.getAutor());
        holder.asunto.setText(alertGestacao.getAssunto());
        holder.data.setText(alertGestacao.getDataAtual());
        holder.msg.setText(alertGestacao.getMensagem());


    }

    @Override
    public int getItemCount() {
        return alert.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView autor,asunto,data,msg;

        public MyViewHolder(View itemView) {
            super(itemView);

            autor = (TextView) itemView.findViewById(R.id.autorAlert);
            asunto = (TextView) itemView.findViewById(R.id.assuntoAlert);
            data = (TextView) itemView.findViewById(R.id.dataAlert);
            msg = (TextView) itemView.findViewById(R.id.mensagemAlert);
        }
    }
}
