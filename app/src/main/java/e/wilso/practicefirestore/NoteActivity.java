package e.wilso.practicefirestore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import e.wilso.practicefirestore.model.Note;

public class NoteActivity extends AppCompatActivity {

   private static final String TAG = "AddNoteActivity";

   TextView edtTitle;
   TextView edtContent;
   Button btAdd;

   private FirebaseFirestore firestoreDB;
   String id = "";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_note);

      edtTitle = findViewById(R.id.edtTitle);
      edtContent = findViewById(R.id.edtContent);
      btAdd = findViewById(R.id.btAdd);

      firestoreDB = FirebaseFirestore.getInstance();

      Bundle bundle = getIntent().getExtras();
      if (bundle != null) {
         id = bundle.getString("UpdateNoteId");

         edtTitle.setText(bundle.getString("UpdateNoteTitle"));
         edtContent.setText(bundle.getString("UpdateNoteContent"));
      }

      btAdd.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String title = edtTitle.getText().toString();
            String content = edtContent.getText().toString();

            if (title.length() > 0) {
               if (id.length() > 0) updateNote(id, title, content);
               else addNote(title, content);
            }
            finish();
         }
      });
   }

   private void updateNote(String id, String title, String content) {
      Map<String, Object> note = (new Note(id, title, content)).toMap();

      firestoreDB.collection("notes")
              .document(id)
              .set(note)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
                    Log.e(TAG, "Note document update successful!");
                    Toast.makeText(getApplicationContext(), "Note has been updated!", Toast.LENGTH_SHORT).show();
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error adding Note document", e);
                    Toast.makeText(getApplicationContext(), "Note could not be updated!", Toast.LENGTH_SHORT).show();
                 }
              });
   }

   private void addNote(String title, String content) {
      Map<String, Object> note = new Note(title, content).toMap();

      firestoreDB.collection("notes")
              .add(note)
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                 @Override
                 public void onSuccess(DocumentReference documentReference) {
                    Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    Toast.makeText(getApplicationContext(), "Note has been added!", Toast.LENGTH_SHORT).show();
                 }
              })
              .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error adding Note document", e);
                    Toast.makeText(getApplicationContext(), "Note could not be added!", Toast.LENGTH_SHORT).show();
                 }
              });
   }
}
