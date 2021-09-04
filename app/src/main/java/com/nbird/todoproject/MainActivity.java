package com.nbird.todoproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<Model> arrayList;
    DownloadTask task;
    TodoAdapter myAdapter;
    CardView addTodo;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTodo=(CardView) findViewById(R.id.addTodo);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);




        setMyAdapter("https://jsonplaceholder.typicode.com/users/1/todos");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.open,R.string.close);

        navigationView.bringToFront();


        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        navigationView.setNavigationItemSelectedListener( MainActivity.this);


        navigationView.setCheckedItem(R.id.nav_view);

        setLoadingDialog();


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

    private void setLoadingDialog(){
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_screen);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public void setMyAdapter(String str){
        arrayList=new ArrayList<>();
        task = new DownloadTask();
        task.execute(str);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        myAdapter=new TodoAdapter(this,arrayList,recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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


                if(loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }



            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Data Not Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        setLoadingDialog();
        switch (menuItem.getItemId()){
            case R.id.todo1:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/2/todos");
                break;
            case R.id.todo2:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/3/todos");
                break;
            case R.id.todo3:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/4/todos");
                break;
            case R.id.todo4:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/5/todos");
                break;
            case R.id.todo5:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/6/todos");
                break;
            case R.id.todo6:
                setMyAdapter("https://jsonplaceholder.typicode.com/users/7/todos");
                break;
            default :
                return true;
        }
        return true;
    }

    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen((GravityCompat.START)))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


}