package com.example.italo.gestante.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.italo.gestante.Adapter.ContatosAdapter;
import com.example.italo.gestante.ChatActivity;
import com.example.italo.gestante.R;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.RecyclerItemClickListener;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.GestanteUser;
import com.example.italo.gestante.model.Medico;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatoFragment extends Fragment {
private RecyclerView recyclerViewListContato;
private ContatosAdapter adapter;
private ArrayList<Medico> listaContatos = new ArrayList<>();
private DatabaseReference usuarioRef;
private  ValueEventListener valueEventListenerContatos;
private FirebaseUser usuarioAtual;

    public ContatoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contato, container, false);

        //Configurações iniciais
        recyclerViewListContato = (RecyclerView) view.findViewById(R.id.recycleViewContato);
        usuarioRef = ConfigFireBase.getFirebase().child("Medico").child("usuarios");
        usuarioAtual = UsuarioFireBase.getUserAtual();

        //Configuração adapter
        adapter = new ContatosAdapter(listaContatos, getActivity());

        //Configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListContato.setLayoutManager(layoutManager);
        recyclerViewListContato.setHasFixedSize(true);
        recyclerViewListContato.setAdapter(adapter);

        //Configura evento de Click no reclycleView
        recyclerViewListContato.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), recyclerViewListContato, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Medico usuarioSelecionado = listaContatos.get(position);

                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("chatContato",usuarioSelecionado);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){
        listaContatos.clear();
       valueEventListenerContatos = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaContatos.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Medico usuarioMedico = dados.getValue(Medico.class);
                   usuarioMedico.setKey(dados.getKey());
                   listaContatos.add(usuarioMedico);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
