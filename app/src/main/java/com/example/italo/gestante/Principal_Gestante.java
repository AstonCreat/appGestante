package com.example.italo.gestante;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.fragment.ListConsulta;
import com.example.italo.gestante.fragment.ListVacina;
import com.example.italo.gestante.fragment.Menu;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.GestanteUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import de.hdodenhof.circleimageview.CircleImageView;

public class Principal_Gestante extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseAuth autenticacao;
    private TextView nome,sangue,idade;
    private CircleImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        nome = (TextView)findViewById(R.id.txtNome_fire);
        sangue = (TextView)findViewById(R.id.txtSangue_fire);
        idade = (TextView)findViewById(R.id.txtidade_fire);
        img = (CircleImageView)findViewById(R.id.imgPerfil);

        autenticacao = ConfigFireBase.getFireBaseAutenticacao();

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Principal Gestante");
        setSupportActionBar(toolbar);

        //FIM TOOLBAR;

        FirebaseUser prinGestante = UsuarioFireBase.getUserAtual();
        Uri url = prinGestante.getPhotoUrl();

        if (url !=null){
            Glide.with(Principal_Gestante.this).load(url).into(img);
        }else{
            img.setImageResource(R.drawable.pregnancy);
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("MENU",Menu.class)
                        .add("Agenda de consulta",ListConsulta.class)
                        .add("Agenda de vacinas",ListVacina.class)
                        .create()
        );
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_conteudo);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagertab = (SmartTabLayout) findViewById(R.id.stl_label);
        viewPagertab.setViewPager(viewPager);

/*

*/
        //recuperando dados
        DatabaseReference userGestante = ConfigFireBase.getFirebase();
        DatabaseReference principalGestante = userGestante.child("Gestante").child("usuarios").child(UsuarioFireBase.getIdentificadorUsuario());
        principalGestante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GestanteUser gtUser = dataSnapshot.getValue(GestanteUser.class);
                nome.setText(gtUser.getNomeC().toUpperCase());
                idade.setText(gtUser.getIdade());
                sangue.setText(gtUser.getSangue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //sobre escrever menu

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                    deslogarUser();
                    finish();
                break;
            case R.id.menuConfiguracoes:
                    abrirConfig();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    public void deslogarUser(){
        try {

            autenticacao.signOut();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void abrirConfig(){
        Intent intent = new Intent(Principal_Gestante.this, activity_configuracoes.class);
        startActivity(intent);
    }


}
