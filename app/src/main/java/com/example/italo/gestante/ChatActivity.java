package com.example.italo.gestante;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.italo.gestante.Adapter.MensagensAdapter;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.Base64Custom;
import com.example.italo.gestante.helper.Permissao;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.Conversa;
import com.example.italo.gestante.model.Medico;
import com.example.italo.gestante.model.Mensagem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.italo.gestante.config.ConfigFireBase.getFirebase;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView circleFoto;
    private TextView textViewNome;
    private EditText edtMensagemEnvio;
    private ImageView imgcamera;
    private static final int selecao_camera = 100;

    private Medico medicoDestinatario;
    private RecyclerView recyclerMEnsagem;

    private MensagensAdapter adapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private DatabaseReference database;
    private StorageReference storange;
    private DatabaseReference msgREf;
    private ChildEventListener childEventListenerMensagens;

    //identificador usuario remetente e destinatario
    private String idUserREmetente; //usuario logado
    private String iduserDestinatario;//usuario recebe msg

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //iniciando configurações
        textViewNome = (TextView) findViewById(R.id.textViewChat);
        circleFoto = (CircleImageView) findViewById(R.id.CircleImageFoto);
        imgcamera = (ImageView) findViewById(R.id.imageViewCameraChat);
        edtMensagemEnvio = (EditText) findViewById(R.id.edtMEnsagem);
        recyclerMEnsagem = (RecyclerView) findViewById(R.id.recycleMensagem);

        //solicitação permissao
        Permissao.validarPermission(permissoesNecessarias,this,selecao_camera);


        //Recuperar dados do usuario remetente
        idUserREmetente = UsuarioFireBase.getIdentificadorUsuario();

        //Recuperando dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            medicoDestinatario = (Medico) bundle.getSerializable("chatContato");
            textViewNome.setText("Dr. " + medicoDestinatario.getNome());

            String foto = medicoDestinatario.getFoto();

            if (foto != null) {
                Uri url = Uri.parse(medicoDestinatario.getFoto());
                Glide.with(ChatActivity.this).load(url).into(circleFoto);

            } else {
                circleFoto.setImageResource(R.drawable.doctorman);
            }

            //Recuperar dados usuario destinatario
            iduserDestinatario = Base64Custom.codificarBase64(medicoDestinatario.getEmail());
        }

        //config Adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());
        //Config recycle
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMEnsagem.setLayoutManager(layoutManager);
        recyclerMEnsagem.setHasFixedSize(true);
        recyclerMEnsagem.setAdapter(adapter);

        database = ConfigFireBase.getFirebase();
        storange = ConfigFireBase.getFirebaseStorage();
        msgREf = database.child("mensagens")
                .child(idUserREmetente)
                .child(iduserDestinatario);


        //Evento de Clique na camera
        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, selecao_camera);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            try {
                switch (requestCode) {
                    case selecao_camera:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                }

                if (imagem != null) {

                    //recuperar img FireBase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Criar nome imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    //Configurando referencias do firebase
                    StorageReference imgRef = storange.child("imagens")
                            .child("fotoschat")
                            .child(idUserREmetente)
                            .child(nomeImagem);
                    UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERRO", "Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String donwloadUrl = taskSnapshot.getDownloadUrl().toString();
                            Mensagem mensagem = new Mensagem();
                            mensagem.setIduser(idUserREmetente);
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem(donwloadUrl);

                            //salvando imagem para remetente
                            salvarMensagem(idUserREmetente,iduserDestinatario, mensagem);
                            //Salvar mensagem destinatario
                            salvarMensagem(iduserDestinatario,idUserREmetente,mensagem);
                            recyclerMEnsagem.smoothScrollToPosition(mensagens.size()+1);
                            Toast.makeText(ChatActivity.this, "Sucesso ao enviar", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for ( int permissaoResult :grantResults){
            if(permissaoResult == PackageManager.PERMISSION_DENIED){
                alertvalidadePermission();
            }
        }
    }

    private void alertvalidadePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyCustomDialog);
        builder.setTitle("Permissão negada");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o chat é necessario da permissao");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //botao de enviar via onClick
    public void fabEnviar(View view) {
        String txtmensagem = edtMensagemEnvio.getText().toString();

        if (!txtmensagem.isEmpty()) {

            Mensagem mensagem = new Mensagem();
            mensagem.setIduser(idUserREmetente);
            mensagem.setMensagem(txtmensagem);

            //Salvar mensagem remetente
            salvarMensagem(idUserREmetente, iduserDestinatario, mensagem);

            //Salvar mensagem destinatario
            salvarMensagem(iduserDestinatario,idUserREmetente,mensagem);


            //salvar conversa
            salvarConversa(mensagem);


        } else {
            Toast.makeText(this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
        }


    }

    private void salvarConversa(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdREmetente(idUserREmetente);
        conversaRemetente.setIdDestinatario(iduserDestinatario);
        conversaRemetente.setUltimaMSG(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(medicoDestinatario);
        conversaRemetente.salvar();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) {

        DatabaseReference database = ConfigFireBase.getFirebase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //limpar text
        edtMensagemEnvio.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagem();


    }

    @Override
    protected void onStop() {
        super.onStop();
        msgREf.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagem() {
        mensagens.clear();
        childEventListenerMensagens = msgREf.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                //barra de rolagem msg
                recyclerMEnsagem.smoothScrollToPosition(mensagens.size() + 1);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
