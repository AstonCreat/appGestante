package com.example.italo.gestante.model;

import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by italo on 04/01/2018.
 */

public class GestanteUser implements Serializable{
    private String id;
    private String nomeC;
    private String dataNAsc;
    private String sexo;
    private String idade;
    private String sangue;
    private String celular;
    private String email;
    private String senha;
    private String foto;
    private String dataBB;
    private String perfilG;



    public GestanteUser(){

    }

    public void salvar(){
        DatabaseReference referenciaFireBase = ConfigFireBase.getFirebase();
        DatabaseReference user = referenciaFireBase.child("Gestante").child("usuarios").child(getId());
        user.setValue(this);
    }


    public void updadteDadosFireBase() {

        String identificadorUser = UsuarioFireBase.getIdentificadorUsuario();
        DatabaseReference database = ConfigFireBase.getFirebase();
        DatabaseReference userRef =database.child("Gestante").child("usuarios")
                .child(identificadorUser);

        Map<String, Object> valoruser = convertParaMap();

        userRef.updateChildren(valoruser);

    }

    @Exclude
    public Map<String,Object> convertParaMap(){
        HashMap<String, Object> userMAp = new HashMap<>();
        //configurando usuario map
        userMAp.put("celular",getCelular());
        userMAp.put("idade",getIdade());
        userMAp.put("dataNAsc",getDataNAsc());
        userMAp.put("nomeC",getNomeC());
        userMAp.put("foto", getFoto());
        userMAp.put("dataBB",getDataBB());

        return userMAp;
    }


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeC() {
        return nomeC;
    }

    public void setNomeC(String nomeC) {
        this.nomeC = nomeC;
    }

    public String getDataNAsc() {
        return dataNAsc;
    }

    public void setDataNAsc(String dataNAsc) {
        this.dataNAsc = dataNAsc;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSangue() {
        return sangue;
    }

    public void setSangue(String sangue) {
        this.sangue = sangue;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDataBB() {
        return dataBB;
    }

    public void setDataBB(String dataBB) {
        this.dataBB = dataBB;
    }

    public void setPerfilG(String perfilG) {
        this.perfilG = perfilG;
    }

    public String getPerfilG() {
        return perfilG;
    }


}
