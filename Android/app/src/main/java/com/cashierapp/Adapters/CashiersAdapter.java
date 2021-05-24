package com.cashierapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cashierapp.Activities.CashierActivity;
import com.cashierapp.Activities.CashiersActivity;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;

import java.util.ArrayList;

public class CashiersAdapter extends RecyclerView.Adapter<CashiersAdapter.CashiersViewHolder> {

    private final Context context;
    private final ArrayList<Cashiers> mDataModel;
    private final RecyclerView mRecyclerView;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
            String item = mDataModel.get(itemPosition).getID();

            Intent intent = new Intent(context, CashierActivity.class);
            intent.putExtra(CashiersActivity.MESSAGE, item);
            context.startActivity(intent);
        }
    };

    public CashiersAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.mDataModel = new ArrayList<>();
        this.mRecyclerView = recyclerView;
    }

    public void addData(Cashiers data){
        mDataModel.add(data);
    }

    @NonNull
    @Override
    public CashiersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cashier_list_object, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new CashiersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CashiersViewHolder holder, int position) {
        Cashiers data = mDataModel.get(position);

        holder.sID.setText(data.getID());

    }

    @Override
    public int getItemCount() {
        return mDataModel.size();
    }

    static class CashiersViewHolder extends RecyclerView.ViewHolder{
        TextView sID;

        public CashiersViewHolder(@NonNull View itemView) {
            super(itemView);
            sID = (TextView) itemView.findViewById(R.id.cashier_list_id);
        }
    }

}