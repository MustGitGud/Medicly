package com.example.medicly;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AllergiesEditor extends AppCompatActivity {

    public static final String ALLERGY_ID_KEY = "allergy_id";

    private static final String LOADING_MESSAGE = "Loading...";
    private static final String SAVING_MESSAGE = "Saving...";
    private static final String ERROR_MESSAGE = "An error has occurred.";

    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies_editor);

        ImageButton bckBtn = findViewById(R.id.bckBtnAllergy);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        EditText title = findViewById(R.id.title);
        EditText description = findViewById(R.id.description);

        String id = getIntent().getStringExtra(ALLERGY_ID_KEY);
        if (id == null) {
            id = UUID.randomUUID().toString();
        } else {
            showLoadingDialog();
            FirebaseFirestore.getInstance().collection(Allergies.ALLERGIES_COLLECTION)
                    .document(id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            if (documentSnapshot.exists()) {
                                Map<String, String> data = Allergies.readData(documentSnapshot);
                                if (data != null) {
                                    title.setText(data.get(Allergies.ALLERGIES_TITLE_FIELD));
                                    description.setText(data.get(Allergies.ALLERGIES_DESCRIPTION_FIELD));
                                } else {
                                    showErrorDialog(null);
                                }
                            } else {
                                showErrorDialog(null);
                            }
                        }
                    });
        }
        final String finalId = id;

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavingDialog();
                Map<String, String> data = buildData(
                        title.getText().toString(),
                        description.getText().toString(),
                        "auth" // TODO get owner id from Firebase Auth
                );
                save(finalId, data);
            }
        });
    }

    private void save(String id, Map<String, String> data) {
        FirebaseFirestore.getInstance()
                .collection(Allergies.ALLERGIES_COLLECTION)
                .document(id)
                .set(data)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        finish();
                    }
                }).addOnFailureListener(AllergiesEditor.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorDialog(e.getMessage() == null ? ERROR_MESSAGE : e.getMessage());
                    }
                });
    }

    private Map<String, String> buildData(String title, String description, String owner) {
        HashMap<String, String> data = new HashMap<>();
        data.put(Allergies.ALLERGIES_TITLE_FIELD, title);
        data.put(Allergies.ALLERGIES_DESCRIPTION_FIELD, description);
        data.put(Allergies.ALLERGIES_OWNER_FIELD, owner);
        return data;
    }

    private void showSavingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setMessage(SAVING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setMessage(LOADING_MESSAGE)
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String message) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(AllergiesEditor.this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
