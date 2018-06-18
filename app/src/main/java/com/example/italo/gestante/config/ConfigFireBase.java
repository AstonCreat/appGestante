package com.example.italo.gestante.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by italo on 03/01/2018.
 */

public final class ConfigFireBase {

    private static DatabaseReference database ;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //retorna intancia do firebaseDatabase
    public static DatabaseReference getFirebase(){
        if(database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //retorna intancia do firebaseAuth
    public  static FirebaseAuth getFireBaseAutenticacao(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getFirebaseStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
