package com.example.italo.gestante.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.italo.gestante.AlertGestacaoActivity;
import com.example.italo.gestante.ChatTab;
import com.example.italo.gestante.R;
import com.example.italo.gestante.config.ConfigFireBase;
import com.example.italo.gestante.helper.UsuarioFireBase;
import com.example.italo.gestante.model.AlertGestacao;
import com.example.italo.gestante.model.Consulta;
import com.example.italo.gestante.model.GestanteUser;
import com.example.italo.gestante.model.Vacina;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Menu extends Fragment {
        CardView imgconsult,imgsiringa,imgChat,alertGestante;
        private TextView dataNacimento;
        private Consulta consulta ;
        private Vacina vacina;
        private DatabaseReference referenciaC = FirebaseDatabase.getInstance().getReference();
        private String iduser;



    public Menu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_menu, container, false);


        dataNacimento = (TextView)v.findViewById(R.id.dataProvavel);

        imgconsult = (CardView)v.findViewById(R.id.imgClipBoard);
        imgsiringa = (CardView)v.findViewById(R.id.imgsiringa);
        imgChat = (CardView)v.findViewById(R.id.imgChat);
        alertGestante = (CardView)v.findViewById(R.id.alertGestante);


        dataNasc();

        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "tela chat", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),ChatTab.class));
            }
        });

       imgconsult.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //Toast
               Toast.makeText(getActivity(), "marcarConsulta", Toast.LENGTH_SHORT).show();

               //Dialog
               final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(),R.style.MyCustomDialog);
               final View mView = getLayoutInflater().inflate(R.layout.activity_dialog_cad_consulta,null);
               //mascaras


               Button btnFecharConsulta = (Button)mView.findViewById(R.id.btnCancelarConsulta);
               Button salvarCadastro = (Button)mView.findViewById(R.id.btnSalvarC);

               final EditText dataConsulta = (EditText)mView.findViewById(R.id.dataC);
               final EditText  horaConsulta = (EditText)mView.findViewById(R.id.horaC);
               final EditText  localConsulta = (EditText)mView.findViewById(R.id.localC);
               final EditText  espConsulta = (EditText)mView.findViewById(R.id.especialidadeC);
               final EditText  medicoConsulta = (EditText)mView.findViewById(R.id.medicoC);

               SimpleMaskFormatter fmDataNasc = new SimpleMaskFormatter("NN/NN/NNNN");
               MaskTextWatcher maskData = new MaskTextWatcher(dataConsulta, fmDataNasc);
               dataConsulta.addTextChangedListener(maskData);
               SimpleMaskFormatter fmHora = new SimpleMaskFormatter("NN:NN");
               MaskTextWatcher maskHora = new MaskTextWatcher(horaConsulta, fmHora);
               horaConsulta.addTextChangedListener(maskHora);

                iduser = UsuarioFireBase.getIdentificadorUsuario();


               mBuilder.setView(mView);
               final AlertDialog dialog = mBuilder.create();
               dialog.setCancelable(false);
                btnFecharConsulta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });

               salvarCadastro.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String patternDt = "dd/MM/yyyy";
                       String patternHr ="HH:mm";
                       SimpleDateFormat dt = new SimpleDateFormat(patternDt);
                       SimpleDateFormat hr = new SimpleDateFormat(patternHr);

                       dt.setLenient(false);
                       hr.setLenient(false);

                       if (!dataConsulta.getText().toString().isEmpty()){
                           try {
                               Date dts = dt.parse(dataConsulta.getText().toString());
                               if (!horaConsulta.getText().toString().isEmpty()){
                                   try {
                                       Date hrs = hr.parse(horaConsulta.getText().toString());
                                   }catch (ParseException e) {
                                       Toast.makeText(getActivity(), "Hora invalida", Toast.LENGTH_SHORT).show();
                                   }
                                   if (!localConsulta.getText().toString().isEmpty()){
                                       if (!espConsulta.getText().toString().isEmpty()){
                                           if (!medicoConsulta.getText().toString().isEmpty()){

                                               consulta = new Consulta();
                                               consulta.setId(iduser);
                                               consulta.setData(dataConsulta.getText().toString());
                                               consulta.setHora(horaConsulta.getText().toString());
                                               consulta.setLocal(localConsulta.getText().toString());
                                               consulta.setEspecialidade(espConsulta.getText().toString());
                                               consulta.setMedico(medicoConsulta.getText().toString());


                                               referenciaC.child("consultas").child(iduser).push().setValue(consulta);

                                               Toast.makeText(getActivity(), "Salvou", Toast.LENGTH_SHORT).show();
                                               //Log.i("consulta","retorno data: " + dataConsulta.getText().toString());
                                               //Log.i("hora","retorno hora: " + horaConsulta.getText().toString());
                                               //Log.i("local","retorno local: " + localConsulta.getText().toString());
                                               //Log.i("epecialidade","retorno especialidade: " + espConsulta.getText().toString());
                                               //Log.i("medico","retorno medico: " + medicoConsulta.getText().toString());

                                               Toast.makeText(getActivity(), "Consulta Agendada com sucesso", Toast.LENGTH_SHORT).show();

                                               dataConsulta.setText("");
                                               horaConsulta.setText("");
                                               localConsulta.setText("");
                                               espConsulta.setText("");
                                               medicoConsulta.setText("");


                                           }else {
                                               Toast.makeText(getActivity(), "Peencha campo medico", Toast.LENGTH_SHORT).show();
                                           }
                                       }else {
                                           Toast.makeText(getActivity(), "Peencha campo especialidade", Toast.LENGTH_SHORT).show();
                                       }
                                   }else {
                                       Toast.makeText(getActivity(), "Peencha campo Local", Toast.LENGTH_SHORT).show();
                                   }
                               }else {
                                   Toast.makeText(getActivity(), "Peencha campo Hora ", Toast.LENGTH_SHORT).show();
                               }
                           }catch (ParseException e) {
                               Toast.makeText(getActivity(), "Data Invalida", Toast.LENGTH_SHORT).show();
                           }
                       }else {
                           Toast.makeText(getActivity(), "Peencha campo data ", Toast.LENGTH_SHORT).show();
                       }


                   }
               });

               dialog.show();
           }
       });

       imgsiringa.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //Toast
               Context context = getActivity();
               CharSequence text = "Marca Vacina";
               int duraton = Toast.LENGTH_LONG;
               Toast toast = Toast.makeText(context,text,duraton);
               //toast.setGravity(Gravity.,0,0);
               toast.show();

               //Dialog
               AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(),R.style.MyCustomDialog);
               View mView = getLayoutInflater().inflate(R.layout.fragment_cad_vacina,null);

               Button cancelar = (Button)mView.findViewById(R.id.btnCancelarVacina);
               Button salvar = (Button)mView.findViewById(R.id.btnSalvarVacina);
               final EditText dtVacina = (EditText)mView.findViewById(R.id.dataVacina);
               final EditText hrVacina = (EditText)mView.findViewById(R.id.horaVacina);
               final EditText tpVacina = (EditText)mView.findViewById(R.id.vacinaTipe);

               SimpleMaskFormatter fmDataVacina = new SimpleMaskFormatter("NN/NN/NNNN");
               MaskTextWatcher maskData = new MaskTextWatcher(dtVacina, fmDataVacina);
               dtVacina.addTextChangedListener(maskData);
               SimpleMaskFormatter fmHora = new SimpleMaskFormatter("NN:NN");
               MaskTextWatcher maskHora = new MaskTextWatcher(hrVacina, fmHora);
               hrVacina.addTextChangedListener(maskHora);

               iduser = UsuarioFireBase.getIdentificadorUsuario();



               mBuilder.setView(mView);
               final AlertDialog dialog = mBuilder.create();
               dialog.setCancelable(false);

               cancelar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.hide();
                   }
               });

               salvar.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                        String patternDt = "dd/MM/yyyy";
                       String patternHr ="HH:mm";
                       SimpleDateFormat dt = new SimpleDateFormat(patternDt);
                       SimpleDateFormat hr = new SimpleDateFormat(patternHr);
                       dt.setLenient(false);
                       hr.setLenient(false);

                       if (!dtVacina.getText().toString().isEmpty()){
                           try {
                               Date dts = dt.parse(dtVacina.getText().toString());
                               if (!hrVacina.getText().toString().isEmpty()){
                                   try {
                                       Date hrs = hr.parse(hrVacina.getText().toString());
                                   }catch (ParseException e) {
                                       Toast.makeText(getActivity(), "Hora invalida", Toast.LENGTH_SHORT).show();
                                   }
                                   if (!tpVacina.getText().toString().isEmpty()){

                                       vacina = new Vacina();
                                       vacina.setIdVacina(iduser);
                                       vacina.setDatavacina(dtVacina.getText().toString());
                                       vacina.setHoraVacina(hrVacina.getText().toString());
                                       vacina.setTipoVacina(tpVacina.getText().toString());

                                       referenciaC.child("vacinas").child(iduser).push().setValue(vacina);
                                       /*
                                       Log.i("consulta","retorno data: " + dtVacina.getText().toString());
                                       Log.i("hora","retorno hora: " + hrVacina.getText().toString());
                                       Log.i("local","retorno local: " + tpVacina.getText().toString());
                                       */
                                       Toast.makeText(getActivity(), "Vacina Agendada com sucesso", Toast.LENGTH_SHORT).show();

                                       dtVacina.setText("");
                                       hrVacina.setText("");
                                       tpVacina.setText("");


                                   }else {
                                       Toast.makeText(getActivity(), "Peencha campo tipo vacina ", Toast.LENGTH_SHORT).show();
                                   }

                               }else {
                                   Toast.makeText(getActivity(), "Peencha campo Hora ", Toast.LENGTH_SHORT).show();
                               }
                           }catch (ParseException e) {
                               Toast.makeText(getActivity(), "Data Invalida", Toast.LENGTH_SHORT).show();
                           }
                       }else {
                           Toast.makeText(getActivity(), "Peencha campo data ", Toast.LENGTH_SHORT).show();
                       }

                   }
               });


               dialog.show();
           }
       });


        alertGestante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AlertGestacaoActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    public void dataNasc(){

//pega usuario atual

        DatabaseReference userGestante = ConfigFireBase.getFirebase();
        DatabaseReference principalGestante = userGestante.child("Gestante").child("usuarios").child(UsuarioFireBase.getIdentificadorUsuario());
        principalGestante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GestanteUser gtUser = dataSnapshot.getValue(GestanteUser.class);
                dataNacimento.setText(gtUser.getDataNAsc());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

}
