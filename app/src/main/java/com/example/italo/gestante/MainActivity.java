package com.example.italo.gestante;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private EditText login, senha;
    private Button logarGestante;
    private FirebaseAuth autenticacao;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView reset = (TextView) findViewById(R.id.recSenha);

        autenticacao = ConfigFireBase.getFireBaseAutenticacao();

        login = (EditText) findViewById(R.id.loginGestante);
        senha = (EditText) findViewById(R.id.senhagestante);
        logarGestante = (Button) findViewById(R.id.btnLogar);

        logarGestante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarAutchUser(v);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Reset_Password_Gestante.class));
            }
        });



        //CHAMANDO TELA DE CADASTRO EM UM ALERTA;
        Button BtnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        BtnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Dados_Pessoais_Gestante.class));
            }
        });

    }

    public void logarUser(GestanteUser gestante){
        autenticacao.signInWithEmailAndPassword(
                gestante.getEmail(), gestante.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    abrirPrincipal();
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
                    Toast.makeText(MainActivity.this, excecao, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Preencha o campo Email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o campo Senha", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser userAtual = autenticacao.getCurrentUser();
        if(userAtual != null){
           abrirPrincipal();
        }
    }

    public void abrirPrincipal(){
        Intent intent = new Intent(this,Principal_Gestante.class);
        startActivity(intent);
    }
}

