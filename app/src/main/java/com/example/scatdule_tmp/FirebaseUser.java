package com.example.scatdule_tmp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.user.model.User;

import java.util.Map;

public class FirebaseUser {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference("firebaseId");

    public int level;
    public int exp;

    public FirebaseUser() {
        this.level=1;
        this.exp=0;
    }
    public FirebaseUser(int level,int exp) {
        this.level=level;
        this.exp=exp;
    }

    public static void writeNewUser(String uesrId){

        FirebaseUser firebaseUser = new FirebaseUser();

        Log.i("why",uesrId);
        myRef.child("users").child(uesrId).setValue(firebaseUser);
    }
    public static void saveUserInfo(String uesrId, int level, int exp){

        FirebaseUser firebaseUser = new FirebaseUser(level,exp);

        myRef.child("users").child(uesrId).setValue(firebaseUser);

    }

    public static void findUser(String userId){
        myRef.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());

                }
                else {
                    Log.d("firebase1", String.valueOf(task.getResult().getValue()));
                    if (task.getResult().getValue()==null)
                        writeNewUser(userId);
                    else {
                        Constants.level = task.getResult().getValue(FirebaseUser.class).level;
                        Constants.exp = task.getResult().getValue(FirebaseUser.class).exp;
                    }
                }
            }
        });
    }
}
