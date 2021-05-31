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

import com.cashierapp.Activities.Cashier.CashierActivity;
import com.cashierapp.Activities.Cashier.CashiersActivity;
import com.cashierapp.Activities.History.HistoryActivity;
import com.cashierapp.Activities.History.HistoryDateActivity;
import com.cashierapp.Activities.History.HistoryDetailActivity;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class CashiersAdapter extends RecyclerView.Adapter<CashiersAdapter.CashiersViewHolder> {

    private final Context context;
    private final ArrayList<Cashiers> mDataModel;
    private final RecyclerView mRecyclerView;
    private final AdapterType mType;
    private final String mCashierID;

    public enum AdapterType{
        Cashier,
        History,
        HistoryDate
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            int itemPosition = mRecyclerView.getChildLayoutPosition(view);
            String item = mDataModel.get(itemPosition).getID();

            Intent intent;
            switch (mType){
                case History:
                    intent = new Intent(context, HistoryDateActivity.class);
                    intent.putExtra(HistoryActivity.MESSAGE, item);
                    break;
                case HistoryDate:
                    intent = new Intent(context, HistoryDetailActivity.class);
                    intent.putExtra(HistoryDateActivity.MESSAGE_DATE, item);
                    intent.putExtra(HistoryDateActivity.MESSAGE_CASHIER_ID, mCashierID);
                    break;
                default:
                    intent = new Intent(context, CashierActivity.class);
                    intent.putExtra(CashiersActivity.MESSAGE, item);
            }


            context.startActivity(intent);

        }
    };

    public CashiersAdapter(Context context, RecyclerView recyclerView, AdapterType type, String cashierID) {
        this.context = context;
        this.mDataModel = new ArrayList<>();
        this.mRecyclerView = recyclerView;
        this.mType = type;
        this.mCashierID = cashierID;
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
        switch (mType){
            case HistoryDate:
                Date time = new Date((long)Long.parseLong(data.getID())*1000);
                String strTime = time.toString();
                int gmtpos = strTime.indexOf('G');
                strTime = strTime.substring(0, gmtpos);
                holder.sID.setText(strTime);
                break;
            default:
                holder.sID.setText("Cashier: " + data.getID());
        }


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