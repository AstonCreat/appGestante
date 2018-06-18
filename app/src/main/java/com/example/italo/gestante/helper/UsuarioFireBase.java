package com.example.italo.gestante.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.model.GestanteUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFireBase {

    public static String getIdentificadorUsuario(){

        FirebaseAuth usuario = ConfigFireBase.getFireBaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificaUser = Base64Custom.codificarBase64(email);

    return identificaUser;
    }

    public static FirebaseUser getUserAtual(){
        FirebaseAuth usuario = ConfigFireBase.getFireBaseAutenticacao();
        return usuario.getCurrentUser();

    }

    //recuperando so o nome

    public  static  boolean atualizarNomeUser(String nomeC){

        try {
            FirebaseUser user = getUserAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nomeC)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar dados");
                    }
                }
            });

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public  static  boolean atualizarFotoUser (Uri url){

       try {
           FirebaseUser user = getUserAtual();
           UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();
           user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (!task.isSuccessful()){
                       Log.d("Perfil","Erro ao atualizar foto de perfil");
                   }
               }
           });

           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }


    public  static GestanteUser getDadosUserLogado(){
        FirebaseUser fireBaseUser = getUserAtual();
        GestanteUser dadosGestante = new GestanteUser();
        dadosGestante.setEmail(fireBaseUser.getEmail());
        dadosGestante.setNomeC(fireBaseUser.getDisplayName());

        if(fireBaseUser.getPhotoUrl() == null){
            dadosGestante.setFoto("");
        }else {
            dadosGestante.setFoto(fireBaseUser.getPhotoUrl().toString());
        }
        return dadosGestante;
    }

}
