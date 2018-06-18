package com.example.italo.gestante.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.transition.CircularPropagation;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.italo.gestante.R;
import com.example.italo.gestante.model.Conversa;
import com.example.italo.gestante.model.GestanteUser;
import com.example.italo.gestante.model.Medico;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos,parent,false);
        return  new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            Conversa conversa = conversas.get(position);
            holder.ultimaMSG.setText(conversa.getUltimaMSG());

            Medico medicoUser = conversa.getUsuarioExibicao();
            holder.nome.setText("Dr. "+medicoUser.getNome());

            if(medicoUser.getFoto() != null){
                Uri uri = Uri.parse(medicoUser.getFoto());
                Glide.with(context).load(uri).into(holder.foto);
            }else{
                holder.foto.setImageResource(R.drawable.doctorman);
            }

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome,ultimaMSG;

        public MyViewHolder(View itemView) {
            super(itemView);
            foto =(CircleImageView) itemView.findViewById(R.id.imgView);
            nome = (TextView) itemView.findViewById(R.id.textViewNome);
            ultimaMSG = (TextView) itemView.findViewById(R.id.textViewSubtitulo);
        }
    }

}
