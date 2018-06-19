package com.example.italo.gestante;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.Permissao;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.GestanteUser;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import de.hdodenhof.circleimageview.CircleImageView;

public class activity_configuracoes extends AppCompatActivity {

    int progressState = 0;

    private Toolbar toolbar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    private ImageButton imgbtCamera, imgbtGaleria;
    private CircleImageView imgPerfil;
    private EditText edtNome, edtNum, edtNasc, edtIdade, edtCiclo;
    private Button btnSalvar;
    private StorageReference storageReference;
    private String identUser;
    private GestanteUser userLogado;

    ProgressDialog progressDialog;

    private static final int selecao_camera = 100;
    private static final int selecao_galeria = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        progressDialog = new ProgressDialog(getBaseContext());

        userLogado = UsuarioFireBase.getDadosUserLogado();

        imgbtCamera = (ImageButton) findViewById(R.id.imgButtonCamera);
        imgbtGaleria = (ImageButton) findViewById(R.id.imgButtonGaleria);
        imgPerfil = (CircleImageView) findViewById(R.id.circleImagePerfil);
        edtNome = (EditText) findViewById(R.id.confNome);
        edtNum = (EditText) findViewById(R.id.numCelular);
        edtNasc = (EditText) findViewById(R.id.data_nasci);
        edtIdade = (EditText) findViewById(R.id.idade);
        edtCiclo = (EditText) findViewById(R.id.data_ciclo);

        btnSalvar = (Button) findViewById(R.id.btn_alterGestante);

        //mascaras
        SimpleMaskFormatter fmDataNasc = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskData = new MaskTextWatcher(edtNasc, fmDataNasc);
        edtNasc.addTextChangedListener(maskData);

        SimpleMaskFormatter fmDataCiclo = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher maskDataCiclo = new MaskTextWatcher(edtCiclo, fmDataCiclo);
        edtCiclo.addTextChangedListener(maskDataCiclo);

        SimpleMaskFormatter fmNumCel = new SimpleMaskFormatter("(NN) N NNNN-NNNN");
        MaskTextWatcher maskCel = new MaskTextWatcher(edtNum, fmNumCel);
        edtNum.addTextChangedListener(maskCel);

        //referencia storege
        storageReference = ConfigFireBase.getFirebaseStorage();
        identUser = UsuarioFireBase.getIdentificadorUsuario();

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FIM TOOLBAR;

        //recuperar dados usuario
        final FirebaseUser usuario = UsuarioFireBase.getUserAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null) {
            Glide.with(activity_configuracoes.this).load(url).into(imgPerfil);
        } else {
            imgPerfil.setImageResource(R.drawable.gestanteft);
        }


        pegandoDadosFireBase();

        imgbtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, selecao_camera);
                }

            }
        });
        imgbtGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, selecao_galeria);
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pattern = "dd/MM/yyyy";
                SimpleDateFormat df = new SimpleDateFormat(pattern);
                SimpleDateFormat cl = new SimpleDateFormat(pattern);
                df.setLenient(false);
                cl.setLenient(false);

                String trasnformeData = edtCiclo.getText().toString();

                String nome = edtNome.getText().toString();
                String celular = edtNum.getText().toString();
                String idade = edtIdade.getText().toString();
                String data = edtNasc.getText().toString();
                String ciclo = calcData(trasnformeData);



                if (!nome.isEmpty()) {
                    if (!data.isEmpty()) {
                        try {
                            Date date = df.parse(data);
                            if (!ciclo.isEmpty()) {
                                try {
                                    Date dateCiclo = cl.parse(ciclo);
                                    if (!idade.isEmpty()) {
                                        if (!idade.isEmpty()) {
                                            boolean retorno = UsuarioFireBase.atualizarNomeUser(nome);
                                            if (retorno) {
                                                userLogado.setNomeC(nome);
                                                userLogado.setCelular(celular);
                                                userLogado.setIdade(idade);
                                                userLogado.setDataNAsc(data);
                                                userLogado.setDataBB(ciclo);
                                                Log.i("ciclo", "ciclo: "+ciclo);
                                                userLogado.updadteDadosFireBase();

                                                Toast.makeText(activity_configuracoes.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(activity_configuracoes.this, "Dados não foram atualizados", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(activity_configuracoes.this, "Preencha campo celular", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(activity_configuracoes.this, "Preencha campo data", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    Toast.makeText(activity_configuracoes.this, "Data de ciclo incorreta!! ", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(activity_configuracoes.this, "Preencha campo ciclo", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            Toast.makeText(activity_configuracoes.this, "Data de nascimento incorreta!! ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity_configuracoes.this, "Preencha campo data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity_configuracoes.this, "Preencha campo nome", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //validar permisoes
        Permissao.validarPermission(permissoesNecessarias, this, 1);
    }

    public void pegandoDadosFireBase() {
        DatabaseReference userGestante = ConfigFireBase.getFirebase();
        DatabaseReference gestante = userGestante.child("Gestante").child("usuarios").child(UsuarioFireBase.getIdentificadorUsuario());

        gestante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GestanteUser gtUser = dataSnapshot.getValue(GestanteUser.class);
                Log.i("Firebase", dataSnapshot.getValue().toString());
                edtNome.setText(gtUser.getNomeC());
                edtNum.setText(gtUser.getCelular());
                edtIdade.setText(gtUser.getIdade());
                edtNasc.setText(gtUser.getDataNAsc());
                edtCiclo.setText(gtUser.getDataBB());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    case selecao_galeria:

                        Uri localImgSelect = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImgSelect);
                        break;
                }
                if (imagem != null) {

                    imgPerfil.setImageBitmap(imagem);

                    //recuperar img FireBase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    //salvar img firebase
                    StorageReference imgRef = storageReference
                            .child("imagens")
                            .child("Perfil_gestante")
                            //.child(identUser)
                            .child(identUser + ".jpeg");
                    showProgressDialog();

                    UploadTask uploadTask = imgRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity_configuracoes.this, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Toast.makeText(activity_configuracoes.this, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();
                            Uri url = taskSnapshot.getDownloadUrl();
                            atualizaFotouser(url);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

        }

    }

    public void atualizaFotouser(Uri url) {

        boolean retorno = UsuarioFireBase.atualizarFotoUser(url);
        if (retorno) {
            userLogado.setFoto(url.toString());
           // Toast.makeText(this, "sua foto foi alterada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "sua foto não foi alterada", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResult : grantResults) {
            if (permissaoResult == PackageManager.PERMISSION_DENIED) {
                alertvalidadePermission();
            }
        }
    }

    private void alertvalidadePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyCustomDialog);
        builder.setTitle("Permissões Negadas");
        builder.setCancelable(false);
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Atualizando foto");
        dialog.setMessage("Carregando....");
        dialog.setCancelable(false);

        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);

        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressState < 100) {
                    progressState += 20;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.setProgress(progressState);
                }
                if (progressState >= 100) {
                    dialog.dismiss();
                }
            }
        }).start();
    }


    public String calcData(String data) {
        String diafinal, mesFinal;
        int diasdoMes =0;

        int mesArray[];
        mesArray = new int[13];
        mesArray[0] = 0;
        mesArray[1] = 31;
        mesArray[2] = 28;
        mesArray[3] = 31;
        mesArray[4] = 30;
        mesArray[5] = 31;
        mesArray[6] = 30;
        mesArray[7] = 31;
        mesArray[8] = 31;
        mesArray[9] = 30;
        mesArray[10] = 31;
        mesArray[11] = 30;
        mesArray[12] = 31;

        Log.i("Entrada", "dataNova: " + data);
        int dia = Integer.parseInt(data.substring(0, 2));
        int mes = Integer.parseInt(data.substring(3, 5));
        int ano = Integer.parseInt(data.substring(6, 10));


        int diaAtual = dia + 7;
        int mesAtual = 0;
        int anoAtual = 0;

        if(mes <= 3 ){
            mesAtual = mes +9;
            if(mesAtual >=12){
                //pegando quantidade de dias da variavel => mes;
                for(int i = 0 ; i < mesArray.length ; i ++){
                    if(mesAtual == i){
                       // System.out.println(i+"ª:"+mesArray[i]);
                        diasdoMes = mesArray[i];
                    }
                    //System.out.println(i+"ª:"+mesArray[i]);
                }
                diaAtual = diaAtual - diasdoMes;
                mesAtual = (mesAtual + 1) - (12);
                anoAtual = ano + 1;
            }else if(mesAtual< 12){
                mesAtual = mesAtual+1;
                for (int i = 0; i < mesArray.length; i++) {
                    if(mesAtual == i){
                        //System.out.println(i+"ª:"+mesArray[i]);
                        diasdoMes = mesArray[i];
                    }
                }
                diaAtual = diasdoMes - dia;
            }
            diaAtual = diaAtual ;
            anoAtual = ano;

        }else{
            mesAtual = mes -3;
            anoAtual = ano +1;
            if(diaAtual > 30 || diaAtual > 31){
                mesAtual = mesAtual+1;
                for (int i = 0; i < mesArray.length; i++) {
                    if(mesAtual == i){
                        //System.out.println(i+"ª:"+mesArray[i]);
                        diasdoMes = mesArray[i];
                    }
                }
                diaAtual = diaAtual - diasdoMes;
            }else{
                mesAtual = mes -3;
                anoAtual = ano;

            }

        }
        diafinal = Integer.toString(diaAtual);
        mesFinal = Integer.toString(mesAtual);
        if (diafinal.length() < 2) {
            diafinal = "0"+diafinal;
        }
        if ( mesFinal.length() < 2) {
            mesFinal = "0"+mesFinal;
        }
            Log.i("Saida", "dataNova: " + diafinal+"/" +mesFinal+ "/"+anoAtual);

        return data = diafinal+"/" +mesFinal+ "/"+anoAtual;
    }
}
