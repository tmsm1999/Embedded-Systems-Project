package com.cashierapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cashierapp.Classes.Cashier;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;

import java.util.ArrayList;

public class CashierAdapter extends RecyclerView.Adapter<CashierAdapter.CashierViewHolder> {

    private final Context context;

    private final ArrayList<Cashier> mDataModel;

    public CashierAdapter(Context context) {
        this.context = context;
        mDataModel = new ArrayList<>();
    }

    public void addData(Cashier data){
        mDataModel.add(data);
    }

    public void changeData(Cashier data){

        for (int i = 0; i < mDataModel.size(); i++){
            if (mDataModel.get(i).getName().equalsIgnoreCase(data.getName())){
                mDataModel.get(i).setWeight(data.getWeight());
            }
        }
    }

    public void removeData(Cashier data){

        for (int i = 0; i < mDataModel.size(); i++){
            if (mDataModel.get(i).getName().equalsIgnoreCase(data.getName())){
                mDataModel.remove(i);
            }
        }
    }

    @NonNull
    @Override
    public CashierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cashier_detail_list_object, parent, false);
        return new CashierViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CashierViewHolder holder, int position) {
        Cashier data = mDataModel.get(position);

        holder.sName.setText(data.getName());
        holder.sWeight.setText(data.getWeight());
    }

    @Override
    public int getItemCount() {
        return mDataModel.size();
    }

    static class CashierViewHolder extends RecyclerView.ViewHolder{
        TextView sName;
        TextView sWeight;

        public CashierViewHolder(@NonNull View itemView) {
            super(itemView);
            sName = (TextView) itemView.findViewById(R.id.cashier_object);
            sWeight = (TextView) itemView.findViewById(R.id.cashier_weight);
        }
    }

}