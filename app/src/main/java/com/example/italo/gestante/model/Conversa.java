package com.example.italo.gestante.model;

import com.example.italo.gestante.config.ConfigFireBase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idREmetente, idDestinatario,ultimaMSG;
    private Medico usuarioExibicao;

    public Conversa() {
    }
    public void salvar(){
        DatabaseReference databaseReference = ConfigFireBase.getFirebase();
        DatabaseReference converRef = databaseReference.child("conversas");
        converRef.child(this.getIdREmetente())
                .child(this.getIdDestinatario())
                .setValue(this);
    }

    public String getIdREmetente() {
        return idREmetente;
    }

    public void setIdREmetente(String idREmetente) {
        this.idREmetente = idREmetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMSG() {
        return ultimaMSG;
    }

    public void setUltimaMSG(String ultimaMSG) {
        this.ultimaMSG = ultimaMSG;
    }

    public Medico getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Medico usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
