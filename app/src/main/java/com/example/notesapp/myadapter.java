package com.example.notesapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class myadapter extends FirebaseRecyclerAdapter<model,myadapter.myviewholder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public myadapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position, @NonNull model model) {
        holder.t1.setText(model.getTitle());
        holder.t2.setText(model.getDetails());
        holder.txt_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.txt_option);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_edit:

                                final DialogPlus dialogPlus =DialogPlus.newDialog(holder.t1.getContext())
                                        .setContentHolder(new ViewHolder(R.layout.edit_content))
                                        .setExpanded(true,900)
                                        .create();
                                View view = dialogPlus.getHolderView();
                                final EditText e_title=view.findViewById(R.id.edit_title);
                                final EditText e_details=view.findViewById(R.id.edit_details);

                                Button submit  = view.findViewById(R.id.edit_submit);

                                e_title.setText(model.getTitle());
                                e_details.setText(model.getDetails());
                                dialogPlus.show();

                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Map<String,Object> map=new HashMap<>();
                                        map.put("title",e_title.getText().toString());
                                        map.put("details",e_details.getText().toString());

                                        FirebaseDatabase.getInstance().getReference().child("notes").child(GoogleSignIn.getLastSignedInAccount(v.getContext()).getId())
                                                .child(getRef(position).getKey()).updateChildren(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        dialogPlus.dismiss();
                                                        Toast.makeText(v.getContext(), "Updated Succesfully",Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialogPlus.dismiss();
                                                        Toast.makeText(v.getContext(), "Updated Failed!",Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });

//                                Intent i  = new Intent(v.getContext(),DetailsHome.class);
//                                i.putExtra("get_title",model.getTitle());
//                                i.putExtra("get_details",model.getDetails());
//
//                                v.getContext().startActivity(i);

                                break;
                            case R.id.menu_remove:

                                FirebaseDatabase.getInstance().getReference().child("notes").child(GoogleSignIn.getLastSignedInAccount(v.getContext()).getId())
                                        .child(getRef(position).getKey()).removeValue();
                                Log.d("TAG", "onMenuItemClick: "+getRef(position).getKey()+" mailid: "+GoogleSignIn.getLastSignedInAccount(v.getContext()).getId());
                                Toast.makeText(v.getContext(), "delete",Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.cardView
                    .setCardBackgroundColor(holder.itemView.getResources()
                            .getColor(getRandomcolor(),null));
        }

    }

    private int getRandomcolor() {

        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.c1);
        colorcode.add(R.color.c2);
        colorcode.add(R.color.c3);
        colorcode.add(R.color.c4);
        colorcode.add(R.color.c5);
        colorcode.add(R.color.c6);
        colorcode.add(R.color.c7);
        colorcode.add(R.color.c8);
        colorcode.add(R.color.purple_200);

        Random randomcolor = new Random();
        int number = randomcolor.nextInt(colorcode.size());
        return colorcode.get(number);
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView t1,t2,txt_option;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            t1 = (TextView) itemView.findViewById(R.id.title);
            t2 = (TextView) itemView.findViewById(R.id.details);
            txt_option = (TextView) itemView.findViewById(R.id.txt_option);
            cardView = (CardView) itemView.findViewById(R.id.single_card);
        }
    }
}
