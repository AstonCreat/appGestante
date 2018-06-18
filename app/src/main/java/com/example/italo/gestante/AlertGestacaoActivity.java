package com.example.italo.gestante;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;

import com.example.italo.gestante.Adapter.AlertAdapter;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.model.AlertGestacao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlertGestacaoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AlertAdapter adapter;
    private ArrayList<AlertGestacao> listAlert =new  ArrayList<>();
    private DatabaseReference alertRef;
    private ValueEventListener valueEventListenerAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_gestacao);

        alertRef = ConfigFireBase.getFirebase().child("alerta_gestacao");


        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarAlert);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Alerta de gestação");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FIM TOOLBAR;

        recyclerView = (RecyclerView)findViewById(R.id.recycleAlert);


        //configurar adapter
        adapter = new AlertAdapter(listAlert,getApplicationContext());

        //configura recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarAlert();
    }

    @Override
    protected void onStop() {
        super.onStop();
        alertRef.removeEventListener(valueEventListenerAlert);
    }

    public void recuperarAlert(){
        listAlert.clear();
        valueEventListenerAlert = alertRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    AlertGestacao alertGestacao = dados.getValue(AlertGestacao.class);
                    listAlert.add(alertGestacao);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
