package com.example.tpacsi;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Inscription extends AppCompatActivity {

    TextInputLayout nom_prenom, email, mdp, confirmer_mdp, tel, adresse_postale, date_naissance;
    String nom_prenomTexte, emailTexte, mdpTexte, confirmer_mdpTexte, telTexte, adresse_postaleTexte, date_naissanceTexte;
    ImageView retourBtn;
    Button inscriptionBtn;

    FirebaseAuth fAuth;
    FirebaseUser client;
    String uidClient;
    FirebaseDatabase bdd;
    DatabaseReference clientRef;

    String[] date_naissanceTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        nom_prenom = findViewById(R.id.nom_prénom);
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        confirmer_mdp = findViewById(R.id.confirmer_mdp);
        tel = findViewById(R.id.telephone);
        adresse_postale = findViewById(R.id.adresse_postale);
        date_naissance = findViewById(R.id.date_de_naissance);

        retourBtn = findViewById(R.id.retour_btn);
        inscriptionBtn = findViewById(R.id.inscription_btn);

        retourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inscription.this, Connexion.class);
                startActivity(intent);
            }
        });

        inscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inscriptionUtilisateur();
            }
        });

    }

    public void inscriptionUtilisateur() {
        nom_prenomTexte = nom_prenom.getEditText().getText().toString();
        emailTexte = email.getEditText().getText().toString();
        mdpTexte = mdp.getEditText().getText().toString();
        confirmer_mdpTexte = confirmer_mdp.getEditText().getText().toString();
        telTexte = tel.getEditText().getText().toString();
        adresse_postaleTexte = adresse_postale.getEditText().getText().toString();
        date_naissanceTexte = date_naissance.getEditText().getText().toString();

        nom_prenom.setError(null);
        email.setError(null);
        mdp.setError(null);
        confirmer_mdp.setError(null);
        tel.setError(null);
        adresse_postale.setError(null);
        date_naissance.setError(null);

        if (nom_prenomTexte.isEmpty()) {
            nom_prenom.setError("Champ obligatoire");
            return;
        }

        if (emailTexte.isEmpty()) {
            email.setError("Champ obligatoire");
            return;
        }

        if (mdpTexte.isEmpty()) {
            mdp.setError("Champ obligatoire");
            return;
        }

        if (mdpTexte.length()<8) {
            mdp.setError("Le mot de passe doit contenir plus de 7 caractères");
            return;
        }

        if (confirmer_mdpTexte.isEmpty()) {
            confirmer_mdp.setError("Champ obligatoire");
            return;
        }

        if (confirmer_mdpTexte.length()<8){
            confirmer_mdp.setError("Le mot de passe doit contenir plus de 7 caractères");
            return;
        }

        if (!mdpTexte.equals(confirmer_mdpTexte)) {
            mdp.setError("Le mot de passe et la confirmation doivent être identiques");
            confirmer_mdp.setError("Le mot de passe et la confirmation doivent être identiques");
            return;
        }

        if (telTexte.isEmpty()) {
            tel.setError("Champ obligatoire");
            return;
        }

        if (telTexte.length()<10){
            tel.setError("Numéro de téléphone trop court");
        }

        if (adresse_postaleTexte.isEmpty()) {
            adresse_postale.setError("Champ obligatoire");
            return;
        }

        if (date_naissanceTexte.isEmpty()) {
            date_naissance.setError("Champ obligatoire");
            return;
        }

        date_naissanceTable = date_naissanceTexte.split("/");

        if (date_naissanceTable.length>3 || date_naissanceTable.length<3){
            date_naissance.setError("Format incorrect, vous devez entrer jj/mm/aaaa");
            return;
        }

        int jourNaissance = Integer.parseInt(date_naissanceTable[0]);
        int moisNaissance = Integer.parseInt(date_naissanceTable[1]);
        int anneeNaissance = Integer.parseInt(date_naissanceTable[2]);

        if (jourNaissance < 1 || jourNaissance > 31) {
            date_naissance.setError("Vérifiez le jour de naissance");
            return;
        }

        if (moisNaissance < 1 || moisNaissance > 12) {
            date_naissance.setError("Vérifiez le mois de naissance");
            return;
        }

        if (anneeNaissance > 2005 || anneeNaissance < 1923) {
            date_naissance.setError("Vérifiez l'année de naissance, notez que vous devez être agé de plus de 18 ans");
            return;
        }

        fAuth = FirebaseAuth.getInstance();
        fAuth.createUserWithEmailAndPassword(emailTexte, mdpTexte).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                client = FirebaseAuth.getInstance().getCurrentUser();
                uidClient = client.getUid();

                bdd = FirebaseDatabase.getInstance();
                clientRef = bdd.getReference().child("Utilisateurs").child("Clients").child(uidClient);

                HashMap<String, Object> inscriptionMap = new HashMap<>();
                inscriptionMap.put("id", uidClient);
                inscriptionMap.put("nom_prénom", nom_prenomTexte);
                inscriptionMap.put("email", emailTexte);
                inscriptionMap.put("mdp", mdpTexte);
                inscriptionMap.put("numTel", telTexte);
                inscriptionMap.put("adresse", adresse_postaleTexte);
                inscriptionMap.put("dateNaissance", date_naissanceTexte);

                clientRef.updateChildren(inscriptionMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(Inscription.this, Accueil.class);
                        startActivity(intent);
                        Toast.makeText(Inscription.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Inscription.this, "Inscription échouée", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Inscription.this, "Un promblème s'est arrivé", Toast.LENGTH_SHORT).show();
            }
        });
    }

}