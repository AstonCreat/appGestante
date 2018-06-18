package com.example.italo.gestante.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.italo.gestante.Adapter.ConsultaAdapter;
import com.example.italo.gestante.R;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.Base64Custom;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.Consulta;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListConsulta extends Fragment {
    private RecyclerView recyclerView;
    private ConsultaAdapter adapter;
    private ArrayList<Consulta> listaConsulta = new ArrayList<>();
    private Consulta consulta;
    private ValueEventListener valueEventListenerConsulta;

    private DatabaseReference consultaREf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_consulta, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleViewConsulta);


        //Configursar adapter
        adapter = new ConsultaAdapter(listaConsulta, getActivity());


        //Configurar RecylerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        swipe();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConsulta();
    }

    @Override
    public void onStop() {
        super.onStop();
        consultaREf.removeEventListener(valueEventListenerConsulta);
    }

    public  void recuperarConsulta(){
        consultaREf = ConfigFireBase.getFirebase();
        DatabaseReference consultaRetorno = consultaREf.child("consultas").child(UsuarioFireBase.getIdentificadorUsuario());


        valueEventListenerConsulta = consultaRetorno.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaConsulta.clear();
                for(DataSnapshot dados: dataSnapshot.getChildren()) {
                    Consulta consulta = dados.getValue(Consulta.class);
                    consulta.setKey( dados.getKey());
                    listaConsulta.add(consulta);

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public  void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START;
                //pode arrastar pros dois lados;
                //int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.START;
                return makeMovementFlags(dragFlag,swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    Log.i("swipe","item arrastado");
                excluirMovimentacao(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(Html.fromHtml("<font color='#000'>Excluir consulta</font>"));
        alertDialog.setMessage(Html.fromHtml("<font color='#000'>Deseja excluir essa consulta?</font>"));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                consulta = listaConsulta.get(position);

                consultaREf = ConfigFireBase.getFirebase();
                consultaREf.child("consultas").child(UsuarioFireBase.getIdentificadorUsuario()).child(consulta.getKey()).removeValue();
                //Log.i("valor","Key:"+ consulta.getKey());
                //consultaREf.child(consulta.getKey()).removeValue();
                adapter.notifyItemRemoved(position);



            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
        Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
        btnNegative.setTextColor(Color.argb(225,24,107,0));
        btnPositive.setTextColor(Color.argb(225,24,107,0));

    }
}
