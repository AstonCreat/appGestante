package com.example.italo.gestante.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.italo.gestante.Adapter.VacinaAdapter;
import com.example.italo.gestante.R;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.Vacina;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListVacina extends Fragment {
    private RecyclerView recyclerView;
    private VacinaAdapter adapter;
    private ArrayList<Vacina> listaVacina = new ArrayList<>();
    private  Vacina vacina;
    private ValueEventListener valueEventListenerVacina;

    private DatabaseReference vacinaREf;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vacina_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewVacinaList);


        //configurando adapter
         adapter= new VacinaAdapter(listaVacina,getActivity());


        //Configurar Recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        swipe();

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarVacina();
    }

    @Override
    public void onStop() {
        super.onStop();
        vacinaREf.removeEventListener(valueEventListenerVacina);
    }

    public  void recuperarVacina(){
        vacinaREf = ConfigFireBase.getFirebase();
        DatabaseReference vacinaRetorno = vacinaREf.child("vacinas").child(UsuarioFireBase.getIdentificadorUsuario());


        valueEventListenerVacina = vacinaRetorno.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaVacina.clear();
                for(DataSnapshot dados: dataSnapshot.getChildren()) {
                   Vacina vacina = dados.getValue(Vacina.class);
                    vacina.setKey( dados.getKey());
                    listaVacina.add(vacina);

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
        alertDialog.setTitle(Html.fromHtml("<font color='#000'>Excluir Vacina</font>"));
        alertDialog.setMessage(Html.fromHtml("<font color='#000'>Deseja excluir essa consulta?</font>"));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                vacina = listaVacina.get(position);

                vacinaREf = ConfigFireBase.getFirebase();
                 vacinaREf.child("vacinas").child(UsuarioFireBase.getIdentificadorUsuario()).child(vacina.getKey()).removeValue();

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
