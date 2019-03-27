package com.cmput301w19t12.bookbuddies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PendingTransactionsAdapter extends ArrayAdapter<Transaction> {
    private TextView transactionDetails;
    private Button startTransaction;
    private Context context;

    public PendingTransactionsAdapter(Context context, ArrayList<Transaction> entries){
        super(context,0,entries);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Transaction transaction = getItem(position);


        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_list_item,
                    parent, false);
        }

        transactionDetails = convertView.findViewById(R.id.transactionDetails);
        startTransaction = convertView.findViewById(R.id.startTransactionButton);
        String td = String.format("%s\n%s\n%s",transaction.getBook().getBookDetails().getTitle(),
                transaction.getBorrower().getUsername(),transaction.getTransactionType());
        transactionDetails.setText(td);

        startTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("STUFF","THIS EVEN GOT CLICKED");
                Intent intent = new Intent(context,BookTransactionActivity.class);
                intent.putExtra("Transaction",new Gson().toJson(transaction));
                context .startActivity(intent);
            }
        });


        return convertView;
    }
}
