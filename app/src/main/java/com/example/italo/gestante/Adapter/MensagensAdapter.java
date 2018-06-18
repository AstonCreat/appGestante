package com.example.italo.gestante.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.italo.gestante.R;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO= 1;


    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_remetente, parent, false);
        }else if (viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_msg_destinatario, parent, false);

        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Mensagem mensagem = mensagens.get(position);
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();
        if (imagem != null){
            Uri url = Uri.parse(imagem);
            Glide.with(context).load(url).into(holder.imagem);

            //esconder texto;
            holder.mensagem.setVisibility(View.GONE);
        }else{
            holder.mensagem.setText(msg);
            //esconder imagem;
            holder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get(position);

        String idUsuario = UsuarioFireBase.getIdentificadorUsuario();

        if (idUsuario.equals(mensagem.getIduser())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;

    }

    public  class  MyViewHolder extends RecyclerView.ViewHolder{
        TextView mensagem;
        ImageView imagem;

        public  MyViewHolder(View itemView){
            super(itemView);
            imagem = (ImageView) itemView.findViewById(R.id.imagemMensagemFoto);
            mensagem = (TextView) itemView.findViewById(R.id.textMensagemText);


        }
    }
}
