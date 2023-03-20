package com.example.tpacsi;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Connexion extends AppCompatActivity {

    TextInputLayout email, mdp;
    String emailTexte, mdpTexte;
    TextView insciez_vous;
    Button connexionBtn;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        //liaison des variables déclarées avec leurs apparition dans l'interface
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        insciez_vous = findViewById(R.id.inscription);
        connexionBtn = findViewById(R.id.connexion_btn);

        connexionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connexionUtilisateur();
            }
        });

        insciez_vous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Connexion.this, Inscription.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void connexionUtilisateur() {
        emailTexte = email.getEditText().getText().toString();
        mdpTexte = mdp.getEditText().getText().toString();

        //supprimer les erreurs précédentes pour effectuer un nouveau test
        email.setError(null);
        mdp.setError(null);

        if (emailTexte.isEmpty()) {
            email.setError("Champ obligatoire");
        }

        if (mdpTexte.isEmpty()) {
            mdp.setError("Champ obligatoire");
            return; //pour quitter la fonction onclick
        }

        fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(emailTexte, mdpTexte).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Connexion.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                //vérifier si c'est un client ou un livreur
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();
                DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child("Clients");
                clientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild(currentUid)) {  // cet utilisateur est un client

                            Intent intent = new Intent(Connexion.this, Accueil.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Connexion.this, "Vérifiez vos coordonnées", Toast.LENGTH_SHORT).show();
            }
        });

    }

}