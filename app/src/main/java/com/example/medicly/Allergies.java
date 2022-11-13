package com.example.medicly;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Allergies extends AppCompatActivity {

    public static final String ALLERGIES_COLLECTION = "allergies";
    public static final String ALLERGIES_TITLE_FIELD = "title";
    public static final String ALLERGIES_DESCRIPTION_FIELD = "description";
    public static final String ALLERGIES_OWNER_FIELD = "owner";

    public static class AllergiesListAdapter extends RecyclerView.Adapter<AllergiesListAdapter.AllergiesListViewHolder> {

        private List<Map<String, String>> data;

        @SuppressLint("NotifyDataSetChanged")
        public void setData(List<Map<String, String>> data) {
            this.data = data;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AllergiesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.allergies_list_item, parent, false);
            return new AllergiesListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AllergiesListViewHolder holder, int position) {
            Map<String, String> entry = data.get(position);
            holder.title.setText(entry.get(ALLERGIES_TITLE_FIELD));
            holder.description.setText(entry.get(ALLERGIES_DESCRIPTION_FIELD));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), AllergiesEditor.class);
                    intent.putExtra(AllergiesEditor.ALLERGY_ID_KEY, entry.get("id"));
                    holder.itemView.getContext().startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this entry?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String id = entry.get("id");
                                    if (id != null) {
                                        FirebaseFirestore.getInstance()
                                                .collection(ALLERGIES_COLLECTION)
                                                .document(id)
                                                .delete();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            if (data != null) {
                return data.size();
            } else {
                return 0;
            }
        }

        public static class AllergiesListViewHolder extends RecyclerView.ViewHolder {

            TextView title, description;

            public AllergiesListViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
            }
        }
    }

    private AllergiesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allergies);

        ImageButton bckBtn = findViewById(R.id.bckBtnAllergy);
        bckBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), HealthRecords.class);
                startActivity(i1);
            }
        });

        ImageButton addBtn = findViewById(R.id.addBtnAllergy);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Allergies.this, AllergiesEditor.class));
            }
        });

        adapter = new AllergiesListAdapter();
        RecyclerView allergiesList = findViewById(R.id.allergiesList);
        allergiesList.addItemDecoration(new DividerItemDecoration(Allergies.this, DividerItemDecoration.VERTICAL));
        allergiesList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance()
                .collection(ALLERGIES_COLLECTION)
                .whereEqualTo(ALLERGIES_OWNER_FIELD, "auth") // TODO get owner id from Firebase Auth
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null || adapter == null) return;
                        ArrayList<Map<String, String>> output = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : value) {
                            Map<String, String> data = readData(snapshot);
                            if (data != null) {
                                data.put("id", snapshot.getId());
                                output.add(data);
                            }
                        }
                        adapter.setData(output);
                    }
                });
    }

    public static Map<String, String> readData(DocumentSnapshot documentSnapshot) {
        Map<String, Object> data = documentSnapshot.getData();
        if (data == null) return null;
        Map<String, String> output = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            output.put(entry.getKey(), entry.getValue().toString());
        }
        return output;
    }
}