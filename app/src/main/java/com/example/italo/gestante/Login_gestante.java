package com.example.italo.gestante;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.model.GestanteUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login_gestante extends Fragment {

    private EditText login, senha;
    private Button logarGestante;
    private FirebaseAuth autenticacao;

    public Login_gestante() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //troca o return por (View v =) para poder usar o OnClickListener
        View v = inflater.inflate(R.layout.fragment_login_gestante, container, false);

        TextView  reset = (TextView) v.findViewById(R.id.recSenha);

        autenticacao = ConfigFireBase.getFireBaseAutenticacao();

         login = (EditText) v.findViewById(R.id.loginGestante);
         senha = (EditText) v.findViewById(R.id.senhagestante);
         logarGestante = (Button) v.findViewById(R.id.btnLogar);

         logarGestante.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ValidarAutchUser(v);
             }
         });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Reset_Password_Gestante.class));
            }
        });

        //return e o v
        return v;
    }
    public void logarUser(GestanteUser gestante){
        autenticacao.signInWithEmailAndPassword(
                gestante.getEmail(), gestante.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    abrirPrincipal(getView());
                }else{
                    String excecao="";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não está cadstrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao ="E-mai e senha não correnponde ao cadastrados";
                    }catch (Exception e){
                        excecao = "Erro ao autenticar usuario: " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), excecao, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void ValidarAutchUser(View view){
        //recuperar dados;
        String textoEmail = login.getText().toString();
        String textoSenha = senha.getText().toString();

        //verificando se está vazio;
        if(!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                //login
                GestanteUser authGestante = new GestanteUser();
                authGestante.setEmail(textoEmail);
                authGestante.setSenha(textoSenha);

                logarUser(authGestante);


            }else {
                Toast.makeText(getActivity(), "Preencha o campo Email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "Preencha o campo Senha", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser userAtual = autenticacao.getCurrentUser();
        if(userAtual != null){
            Intent intent = new Intent(getContext(),Principal_Gestante.class);
            startActivity(intent);
        }
    }

    public void abrirPrincipal(View view){
        Intent intent = new Intent(getContext(),Principal_Gestante.class);
        startActivity(intent);
    }


}
