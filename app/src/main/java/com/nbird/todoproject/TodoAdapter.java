package com.nbird.todoproject;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.MyViewHolder> {
    private Context mContext;
    private List<Model> mData;
    private RecyclerView recyclerView;
    public TodoAdapter(Context mContext, List<Model> mData, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.mData = mData;
        this.recyclerView=recyclerView;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_asset, parent, false);

        return new MyViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {



        holder.disTextView.setText(mData.get(position).getTitle());
        holder.indexTextView.setText("#"+mData.get(position).getUserId());
        holder.titleTextView.setText(String.valueOf(mData.get(position).getId()));

        if(mData.get(position).getCompleted()){
            holder.iconimageView.setBackgroundResource(R.drawable.correct);
            holder.isDoneTextView.setText("Completed");
        }else{
            holder.iconimageView.setBackgroundResource(R.drawable.wrong);
            holder.isDoneTextView.setText("Not Completed");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext,R.style.Theme_AppCompat_Dialog_Alert);

                final View view1= LayoutInflater.from(mContext).inflate(R.layout.update_asset,(ConstraintLayout) v.findViewById(R.id.layoutDialogContainer));
                builder.setView(view1);
                builder.setCancelable(false);
                Button submit=view1.findViewById(R.id.buttonYes);
                TextInputEditText codeEditTextView=(TextInputEditText) view1.findViewById(R.id.codeEditTextView);
                Button buttoncancel=view1.findViewById(R.id.buttonNo);
                CardView removeTodo=view1.findViewById(R.id.removeTodo);
                codeEditTextView.setText(mData.get(position).getTitle());

                if(mData.get(position).getCompleted()){
                    submit.setText("Mark As Not Completed");
                }else{
                    submit.setText("Mark As Completed");
                }


                final AlertDialog alertDialog=builder.create();
                if(alertDialog.getWindow()!=null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                try{
                    alertDialog.show();
                }catch (Exception e){

                }

                removeTodo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id=mData.get(position).getId();
                        mData.remove(position);
                        adapterCaller();
                        alertDialog.cancel();
                        Toast.makeText(mContext, "Todo "+id+" Removed From The Todo List", Toast.LENGTH_SHORT).show();
                    }
                });

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (mData.get(position).getCompleted()) {
                                    Model model = new Model(mData.get(0).getUserId(), mData.get(position).getId(), codeEditTextView.getText().toString(), false);
                                    mData.set(position, model);
                                } else {
                                    Model model = new Model(mData.get(0).getUserId(), mData.get(position).getId(), codeEditTextView.getText().toString(), true);
                                    mData.set(position, model);
                                }


                                if (mData.get(position).getCompleted()) {
                                    Toast.makeText(mContext, "Todo Marked As Completed!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, "Todo Marked As InCompleted!!", Toast.LENGTH_SHORT).show();
                                }


                               adapterCaller();
                                alertDialog.dismiss();
                            }
                        });

                buttoncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });


    }
    public void adapterCaller(){
        TodoAdapter adapter; // where adapter - your adapter
        adapter = new TodoAdapter(mContext, mData, recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView iconimageView;
        TextView disTextView,indexTextView;
        TextView isDoneTextView;
        CardView cardView;


        public MyViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            iconimageView = (ImageView) itemView.findViewById(R.id.image);
            disTextView = (TextView) itemView.findViewById(R.id.dis);
            indexTextView = (TextView) itemView.findViewById(R.id.index);
            isDoneTextView = (TextView) itemView.findViewById(R.id.isdone);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }

}