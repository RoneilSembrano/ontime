package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SelectTimeActivity extends AppCompatActivity {
    private ArrayAdapter adapter;
    //Intent key words
    private static final String COMPANY_NAME = "CompanyName";
    private static final String APPOINTMENT_DATE = "AppointmentDate";
    private static final String APPOINTMENT_TIME = "AppointmentTime";
    private static final String CLIENT_NAME = "ClientName";
    private static final String CLIENT_PHONE_NUMBER = "ClientPhoneNumber";
    //Database collection/path names
    private static final String PATH_PROVIDER_COLLECTION = "Providers";
    private static final String PATH_DATE_COLLECTION = "Daily Schedule";
    private static final String PATH_CLIENT_COLLECTION = "Clients";
    //Database setup
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private CollectionReference colRef = db.collection(PATH_PROVIDER_COLLECTION);
    //Logging information
    private final String TAG = "SelectTime";

    private String companyName;
    private String appointmentDate;
    private String appointmentTime;

    ListView listViewSchedule;
    TextView textViewCompanyName;
    Provider provider;
    String startTime;
    String endTime;
    ArrayList<String> schedule = new ArrayList<>();
    ArrayList<String> bookedAppointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        listViewSchedule = (ListView) findViewById(R.id.listViewSchedule);
        textViewCompanyName = (TextView) findViewById(R.id.textViewBusinessName);

        //Getting the passed variables from previous Activity.
        Intent extraIntentInfo = getIntent();
        companyName = extraIntentInfo.getStringExtra(COMPANY_NAME);
        appointmentDate = extraIntentInfo.getStringExtra(APPOINTMENT_DATE);
        String nameAndDate = companyName + " " + appointmentDate;
        textViewCompanyName.setText(nameAndDate);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, schedule);


        colRef.document(companyName).collection(appointmentDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        bookedAppointments.add(document.getId());
                    }
                    listViewSchedule.setAdapter(adapter);
                    Log.d(TAG, bookedAppointments.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        final DocumentReference docRef = db.collection(PATH_PROVIDER_COLLECTION).document(companyName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        provider = document.toObject(Provider.class);
                        startTime = provider.getStartTime();
                        endTime = provider.getEndTime();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "provider: " + provider.toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Pulling data failed with ", task.getException());
                }
            }
        });


        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
//        int startWhereColonIs = startTime.indexOf(':');
//        String startHour = startTime.substring(0, startWhereColonIs);
//        int endWhereColonIs = endTime.indexOf(':');
//        String endHour = endTime.substring(0, endWhereColonIs);
//        for(int i = 0; i < 2; i ++)
//        {
//            for(int j = 1; j <= 12; j++)
//            {
//                if(i == 0 && j >= Integer.parseInt(startHour))
//                {
//                    schedule.add(j + ":00 AM");
//                }
//                if(i == 1 && j <= Integer.parseInt(endHour))
//                {
//                    schedule.add(j + ":00 PM");
//                }
//            }
//        }

        //Temporary schedule till business registration completed
//        schedule.add("7:00 AM");
//        schedule.add("8:00 AM");
        schedule.add("9:00 AM");
        schedule.add("10:00 AM");
        schedule.add("11:00 AM");
        schedule.add("12:00 PM");
        schedule.add("1:00 PM");
        schedule.add("2:00 PM");
        schedule.add("3:00 PM");
        schedule.add("4:00 PM");
        schedule.add("5:00 PM");
//        schedule.add("6:00 PM");
//        schedule.add("7:00 PM");
//        schedule.add("8:00 PM");

        listViewSchedule.setAdapter(adapter);

        listViewSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent goToClientInformation = new Intent(SelectTimeActivity.this, ClientInformationActivity.class);
                appointmentTime = (String) adapter.getItemAtPosition(position).toString();
                goToClientInformation.putExtra(COMPANY_NAME, companyName);
                goToClientInformation.putExtra(APPOINTMENT_DATE, appointmentDate);
                goToClientInformation.putExtra(APPOINTMENT_TIME, appointmentTime);
                startActivity(goToClientInformation);
            }
        });
    }

}
