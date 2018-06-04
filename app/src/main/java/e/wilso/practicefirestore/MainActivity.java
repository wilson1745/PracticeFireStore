package e.wilso.practicefirestore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import e.wilso.practicefirestore.adapter.NoteRecyclerViewAdapter;
import e.wilso.practicefirestore.model.Note;

public class MainActivity extends AppCompatActivity {

   private static final String TAG = "MainActivity";

   private RecyclerView recyclerView;
   private NoteRecyclerViewAdapter mAdapter;

   private FirebaseFirestore firestoreDB;
   private ListenerRegistration firestoreListener;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      recyclerView = findViewById(R.id.rvNoteList);
      firestoreDB = FirebaseFirestore.getInstance();

      loadNotesList();

      firestoreListener = firestoreDB.collection("notes")
              .addSnapshotListener(new EventListener<QuerySnapshot>() {
                 @Override
                 public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                       Log.e(TAG, "Listen failed!", e);
                       return;
                    }

                    List<Note> notesList = new ArrayList<>();

                    for (DocumentSnapshot doc : documentSnapshots) {
                       Note note = doc.toObject(Note.class);
                       note.setId(doc.getId());
                       notesList.add(note);
                    }

                    mAdapter = new NoteRecyclerViewAdapter(notesList, getApplicationContext(), firestoreDB);
                    recyclerView.setAdapter(mAdapter);
                 }
              });
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();

      firestoreListener.remove();
   }

   private void loadNotesList() {
      firestoreDB.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
         @Override
         public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if(task.isSuccessful()) {
               List<Note> notesList = new ArrayList<Note>();

               for(DocumentSnapshot doc : task.getResult()) {
                  Note note = doc.toObject(Note.class);
                  note.setId(doc.getId());
                  notesList.add(note);
               }

               mAdapter = new NoteRecyclerViewAdapter(notesList, getApplicationContext(), firestoreDB);
               RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
               recyclerView.setLayoutManager(mLayoutManager);
               recyclerView.setItemAnimator(new DefaultItemAnimator());
               recyclerView.setAdapter(mAdapter);
            }
            else Log.d(TAG, "Error getting documents: ", task.getException());
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_main, menu);

      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      if (item != null) {
         if (item.getItemId() == R.id.addNote) {
            Intent intent = new Intent(this, NoteActivity.class);
            startActivity(intent);
         }
      }

      return super.onOptionsItemSelected(item);
   }
}