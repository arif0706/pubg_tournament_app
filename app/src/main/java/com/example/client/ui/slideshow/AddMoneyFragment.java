package com.example.client.ui.slideshow;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.client.ModelClasses.AdminTransactions;
import com.example.client.ModelClasses.internet_receiver;
import com.example.client.PinVerification;
import com.example.client.R;
import com.example.client.ModelClasses.Transactions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddMoneyFragment extends Fragment implements internet_receiver.getConnection {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextInputLayout amount;
    Button add_from_winnings;
    ImageView add_from_upi_paytm;
    ImageView add_from_gpay;
    View view;
    GoogleSignInAccount signInAccount;
    private static final int UPI_PAYMENT = 123;
    int internet_check;
    ConstraintLayout constraintLayout;
    private String PUBGID;
    private String mParam1;
    private String mParam2;
    DatabaseReference databaseReference;
    BroadcastReceiver internet_receiver=null;
    Snackbar snackbar;
    Toast toast;
    private String AdminUpiId;
    BottomNavigationView navigationView;


    int entered_amount;
    public AddMoneyFragment() {
        // Required empty public constructor
    }


    public static AddMoneyFragment newInstance(String param1, String param2, String PUBGID, BottomNavigationView navigationView) {
        AddMoneyFragment fragment = new AddMoneyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString("PUBGID",PUBGID);
        fragment.setArguments(args);
        fragment.navigationView=navigationView;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            PUBGID=getArguments().getString("PUBGID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view  =inflater.inflate(R.layout.fragment_add_money, container, false) ;
   amount=view.findViewById(R.id.amount);
   add_from_upi_paytm=view.findViewById(R.id.paytm);
   add_from_winnings=view.findViewById(R.id.add_from_winning);
      signInAccount =GoogleSignIn.getLastSignedInAccount(view.getContext());
      add_from_gpay=view.findViewById(R.id.Gpay);
     System.out.println(signInAccount.getEmail());
     databaseReference=FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
     System.out.println("ID:"+PUBGID);
     constraintLayout=view.findViewById(R.id.constraint_layout);

     constraintLayout.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
             if(snackbar!=null){
                 snackbar.dismiss();
                 snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                 snackbar=null;
             }
             if(toast!=null){
                 toast.cancel();
                 toast=null;
             }

             return false;
         }
     });
     amount.getEditText().addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
             if(!amount.getEditText().getText().toString().equals("")){
                 amount.setError(null);
             }
         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
             if(!amount.getEditText().getText().toString().equals("")){
                 amount.setError(null);
             }
         }

         @Override
         public void afterTextChanged(Editable s) {
            if(!amount.getEditText().getText().toString().equals("")){
                amount.setError(null);
            }
         }
     });



            DatabaseReference database=FirebaseDatabase.getInstance().getReference("BHIM_UPI");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AdminUpiId=snapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        internet_receiver = new internet_receiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        view.getContext().registerReceiver(internet_receiver, intentFilter);
        add_from_winnings.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(amount.getEditText().getWindowToken(), 0);
             if (internet_check == 1) {
                 if (PUBGID != null) {
                     databaseReference.child(PUBGID).child("winning_money").addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if (dataSnapshot.exists()) {
                                 if (amount.getEditText().getText().toString().equals(""))
                                     amount.setError("Enter the amount:");
                                 else {
                                      entered_amount = Integer.parseInt(amount.getEditText().getText().toString());
                                     System.out.println("Winning money" + dataSnapshot.getValue());
                                     long winning_money = (long) dataSnapshot.getValue();
                                     if (entered_amount <= winning_money) {
                                         if (entered_amount >= 10) {
                                             new MaterialAlertDialogBuilder(view.getContext()).setTitle("Summary").setMessage(Html.fromHtml("<font color='#1c2e4a'><b>Amount to be added:<i>" + entered_amount + "/-</i></b></font>")).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                 @Override
                                                 public void onClick(DialogInterface dialog, int which) {

                                                     Intent intent=new Intent(view.getContext(),PinVerification.class);
                                                     intent.putExtra("PUBGID",PUBGID);
                                                     intent.putExtra("displayMessage","Adding an amount of "+entered_amount+"/- from winnings to wallet.");
                                                     startActivityForResult(intent,1);


                                                 }
                                             }).show();

                                         } else {

                                             amount.setError("Amount should be more than 10/-");
                                         }
                                     } else {
                                         snackbar =Snackbar.make(view, "Not enough Balance", Snackbar.LENGTH_SHORT);
                                         snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                         snackbar.setAnchorView(navigationView);
                                         snackbar.show();

                                     }
                                 }
                             } else {
                                 snackbar=Snackbar.make(view, "Update your data", Snackbar.LENGTH_SHORT);
                                 snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                 snackbar.setAnchorView(navigationView);
                                 snackbar.show();
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }

                     });
                 } else {
                     snackbar=Snackbar.make(view, "Update your credentials", Snackbar.LENGTH_SHORT);
                     snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                     snackbar.setAnchorView(navigationView);
                     snackbar.show();
                 }
             }
             else{
                 snackbar=Snackbar.make(view, "No internet Connection", Snackbar.LENGTH_SHORT);
                 snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                 snackbar.setAnchorView(navigationView);
                 snackbar.show();

             }

             }
     });
        add_from_gpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(amount.getEditText().getWindowToken(), 0);
                if(internet_check==1) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                    databaseReference.orderByChild("email").equalTo(signInAccount.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (amount.getEditText().getText().toString().equals("")||amount.getEditText().getText().toString().equals("0")) {
                                    amount.setError("Enter the amount");
                                } else {
                                    payUsingUpi("SR Gamerz", AdminUpiId,
                                            "Paying to top up wallet", amount.getEditText().getText().toString(),"gpay");
                                }
                            } else {
                                snackbar=Snackbar.make(v, "Update your PUBG credentials", Snackbar.LENGTH_LONG);
                                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                                snackbar.setAnchorView(navigationView);
                                snackbar.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    snackbar=Snackbar.make(view,"No internet Connection",Snackbar.LENGTH_SHORT);
                    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    snackbar.setAnchorView(navigationView);
                    snackbar.show();
                }
            }
        });
   add_from_upi_paytm.setOnClickListener(new View.OnClickListener() {

       @Override
       public void onClick(View v) {
           InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
           imm.hideSoftInputFromWindow(amount.getEditText().getWindowToken(), 0);
           if(internet_check==1) {
               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
               databaseReference.orderByChild("email").equalTo(signInAccount.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()) {
                           if (amount.getEditText().getText().toString().equals("")||amount.getEditText().getText().toString().equals("0")) {
                               amount.setError("Enter the amount");
                           } else {
                               payUsingUpi("SR Gamerz", AdminUpiId,
                                       "Paying to top up wallet", amount.getEditText().getText().toString(),"paytm");
                           }
                       } else {
                           snackbar=Snackbar.make(v, "Update your PUBG credentials", Snackbar.LENGTH_LONG);
                           snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                           snackbar.setAnchorView(navigationView);
                           snackbar.show();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
           }
           else{
               snackbar=Snackbar.make(view,"No internet Connection",Snackbar.LENGTH_SHORT);
               snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
               snackbar.setAnchorView(navigationView);
               snackbar.show();
           }

           }





   });


        return view;
    }

    void payUsingUpi (String name, String upiId, String note, String amount,String wallet){
        Log.e("main ", "name " + name + "--up--" + upiId + "--" + note + "--" + amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();

        PackageManager packageManager=view.getContext().getPackageManager();
       switch(wallet) {
           case "paytm":
               String PAYTM_PACKAGE_NAME = "net.one97.paytm";
               int PAYTM_REQUEST_CODE = 123;
               if (isPackageInstalled(PAYTM_PACKAGE_NAME, packageManager)) {

                   Intent i = new Intent(Intent.ACTION_VIEW);
                   i.setData(uri);
                   i.setPackage(PAYTM_PACKAGE_NAME);
                   startActivityForResult(i, PAYTM_REQUEST_CODE);
               } else{
                   toast = Toast.makeText(view.getContext(), "Install the paytm to continue.", Toast.LENGTH_SHORT);
               toast.show();

              }

               break;
           case "gpay":
               String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
               int GOOGLE_PAY_REQUEST_CODE = 123;
                if(isPackageInstalled(GOOGLE_PAY_PACKAGE_NAME,packageManager)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
                    startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

                }
                else{
                    toast=Toast.makeText(view.getContext(), "Install the Google pay to continue.", Toast.LENGTH_SHORT);
                    toast.show();
                }
               break;
       }

    }
   boolean isPackageInstalled(String packageName, PackageManager packageManager){
       try {
           packageManager.getPackageInfo(packageName, 0);
           return true;
       } catch (PackageManager.NameNotFoundException e) {
           return false;
       }

   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response " + resultCode);
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }


        if(requestCode==1){
            if(resultCode==RESULT_OK){
                AddFromWinnings(entered_amount);
            }
            else{
                if(resultCode==RESULT_CANCELED){

                }
            }
        }
    }




    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(view.getContext())) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String[] response = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String[] equalStr = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Date date1=new Date();
                final Double[] newWalletMoney = new Double[1];

                SimpleDateFormat dateFormat;
                dateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm");
                DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("Users_Client").child("Users");
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.runTransaction(new Transaction.Function<Double>() {
                    @Nullable
                    @Override
                    public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentReference documentReference=db.collection("UsersBalance").document(PUBGID);
                        DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                        newWalletMoney[0] =documentSnapshot.getDouble("Balance")+Double.parseDouble(amount.getEditText().getText().toString());
                        transaction.update(documentReference,"Balance", newWalletMoney[0]);
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Double>() {
                    @Override
                    public void onSuccess(Double aDouble) {
                        databaseReference1.child(PUBGID).child("Balance").setValue(String.valueOf(newWalletMoney[0]));
                        Transactions obj=new Transactions(amount.getEditText().getText().toString(),dateFormat.format(date1),"0","Added to Wallet");
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users_Client");
                        databaseReference.child("Transactions").child(PUBGID).push().setValue(obj);
                        snackbar=Snackbar.make(view,"Amount added successfully",Snackbar.LENGTH_SHORT);
                        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                        snackbar.setAnchorView(navigationView);
                        snackbar.show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                DatabaseReference transactionsref=FirebaseDatabase.getInstance().getReference("User_Upi_Payments");
                AdminTransactions transaction =  new AdminTransactions();
                GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getContext());
                transaction.from=account.getDisplayName();
                transaction.to="Admin";
                transaction.money=amount.getEditText().getText().toString();
                Calendar calendar=Calendar.getInstance();
                final int year,date,month;
                year=calendar.get(Calendar.YEAR);
                date=calendar.get(Calendar.DATE);
                month=calendar.get(Calendar.MONTH);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
                String format = simpleDateFormat.format(new Date());
                Log.d("MainActivity", "Current Timestamp: " + format);
                transaction.date=format;
                transaction.profilepic=String.valueOf(account.getPhotoUrl());
                transactionsref.push().setValue(transaction);
                Toast.makeText(view.getContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();

                Log.e("UPI", "payment successfull: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(view.getContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: " + approvalRefNo);
            } else {
                Toast.makeText(view.getContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: " + approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(view.getContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }


    @Override
    public void getNoConnectionValue(String text) {
        internet_check=0;

    }

    @Override
    public void getYesConnectionValue(String online, String text) {
        internet_check=1;

    }


    void AddFromWinnings(int entered_amount){
        final ProgressDialog[] progressDialog = new ProgressDialog[1];
        progressDialog[0] = new ProgressDialog(view.getContext());
        progressDialog[0].setMessage("Transferring");
        progressDialog[0].setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog[0].setIndeterminate(true);
        progressDialog[0].show();
        final Double[] newWinningsValue = new Double[1];
        final Double[] newWalletValue = new Double[1];
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("UsersBalance").document(PUBGID);
        db.runTransaction(new Transaction.Function<Double>() {
            @Nullable
            @Override
            public Double apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                newWinningsValue[0] =documentSnapshot.getDouble("Winnings");
                if(entered_amount<=newWinningsValue[0]) {
                    newWinningsValue[0] = newWinningsValue[0] - entered_amount;
                    transaction.update(documentReference, "Winnings", newWinningsValue[0]);
                    newWalletValue[0] = documentSnapshot.getDouble("Balance") + entered_amount;
                    transaction.update(documentReference, "Balance", newWalletValue[0]);

                    return null;
                }
                else
                {
                    throw new FirebaseFirestoreException("Not enough balance",
                            FirebaseFirestoreException.Code.ABORTED);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Double>() {
            @Override
            public void onSuccess(Double aDouble) {
                databaseReference.child(PUBGID).child("Balance").setValue(String.valueOf(newWalletValue[0]));
                databaseReference.child(PUBGID).child("winning_money").setValue(newWinningsValue[0]);
                Date date1 = new Date();
                SimpleDateFormat dateFormat;
                dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Transactions trans = new Transactions(String.valueOf(entered_amount), dateFormat.format(date1), "0", "Added to wallet from winnings");
                DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users_Client");
                databaseReference1.child("Transactions").child(PUBGID).push().setValue(trans);
                amount.getEditText().setText("");
                progressDialog[0].dismiss();
                snackbar=Snackbar.make(view,"Amount transferred successfully",Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(navigationView);
                snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                snackbar.show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog[0].dismiss();
                if(e.getMessage().equals("Not enough balance")){
                    snackbar=Snackbar.make(view,"Not enough balance",Snackbar.LENGTH_SHORT);
                    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    snackbar.setAnchorView(navigationView);
                    snackbar.show();
                }
                else {
                    Snackbar.make(view, "Transaction Failed!", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddFromWinnings(entered_amount);
                        }
                    });
                    snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    snackbar.setAnchorView(navigationView);
                    snackbar.show();
                    System.out.println("meeeee" + e.getMessage());
                }
            }
        });



    }
}
