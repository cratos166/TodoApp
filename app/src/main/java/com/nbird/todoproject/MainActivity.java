package com.nbird.todoproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Model> arrayList;
    DownloadTask task;
    TodoAdapter myAdapter;
    CardView addTodo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTodo=(CardView) findViewById(R.id.addTodo);


        arrayList=new ArrayList<>();
        task = new DownloadTask();
        task.execute("https://jsonplaceholder.typicode.com/users/1/todos");

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
         myAdapter=new TodoAdapter(this,arrayList,recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(myAdapter);



        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Dialog_Alert);

                final View view1= LayoutInflater.from(MainActivity.this).inflate(R.layout.todo_maker_asset,(ConstraintLayout) findViewById(R.id.layoutDialogContainer));
                builder.setView(view1);
                builder.setCancelable(false);
                Button buttonsubmit=(Button) view1.findViewById(R.id.buttonYes);
                TextInputEditText codeEditTextView=(TextInputEditText) view1.findViewById(R.id.codeEditTextView);

                Button buttoncancel=view1.findViewById(R.id.buttonNo);


                final AlertDialog alertDialog=builder.create();
                if(alertDialog.getWindow()!=null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }


                try{
                    alertDialog.show();
                }catch (Exception e){

                }
                buttoncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                buttonsubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str=codeEditTextView.getText().toString();
                        Model model=new Model(arrayList.get(5).getUserId(),arrayList.size()+1,str,false);
                        arrayList.add(model);
                        myAdapter.notifyDataSetChanged();
                        alertDialog.cancel();

                        int position=arrayList.size();
                        Toast.makeText(MainActivity.this, "Todo Added in "+position+" position", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {



                JSONArray jsonarray = new JSONArray(s);
                for(int i=0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                     Model model=new Model(jsonobject.getInt("userId"),jsonobject.getInt("id"),jsonobject.getString("title"),jsonobject.getBoolean("completed"));
                    arrayList.add(model);
                }

                myAdapter.notifyDataSetChanged();





            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Data Not Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

}