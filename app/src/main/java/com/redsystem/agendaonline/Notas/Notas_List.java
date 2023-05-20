package com.redsystem.agendaonline.Notas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.redsystem.agendaonline.Objetos.Nota;
import com.redsystem.agendaonline.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.redsystem.agendaonline.ViewModels.ViewModel_Nota;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

public class Notas_List extends AppCompatActivity {

    RecyclerView recyclerviewNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_Usuarios;

    LinearLayoutManager linearLayoutManager;

    FirebaseRecyclerAdapter<Nota, ViewModel_Nota> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;

    Dialog dialog, dialog_filtrar;

    FirebaseAuth auth;
    FirebaseUser user;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_notas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        recyclerviewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerviewNotas.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dialog = new Dialog(Notas_List.this);
        dialog_filtrar = new Dialog(Notas_List.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        BD_Usuarios = firebaseDatabase.getReference("Usuarios");
        Estado_Filtro();

    }

    private void ListarTodasNotas(){
        //consulta
        Query query = BD_Usuarios.child(user.getUid()).child("Notas_Publicadas").orderByChild("fecha_nota");
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewModel_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewModel_Nota viewModel_nota, int position, @NotNull Nota nota) {
                viewModel_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }


            @Override
            public ViewModel_Nota onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewModel_Nota viewModel_nota = new ViewModel_Nota(view);
                viewModel_nota.setOnClickListener(new ViewModel_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(Notas_List.this, Detail_Nota.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha_registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Listar_Notas.this, "Actualizar nota", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Notas_List.this, Modify_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha_registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    }
                });
                return viewModel_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Notas_List.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firebaseRecyclerAdapter);

    }

    private void ListarNotasFinalizadas(){
        //consulta
        String estado_nota = "Finalizado";
        Query query = BD_Usuarios.child(user.getUid()).child("Notas_Publicadas").orderByChild("estado").equalTo(estado_nota);
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewModel_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewModel_Nota viewModel_nota, int position, @NotNull Nota nota) {
                viewModel_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }


            @Override
            public ViewModel_Nota onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewModel_Nota viewModel_nota = new ViewModel_Nota(view);
                viewModel_nota.setOnClickListener(new ViewModel_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(Notas_List.this, Detail_Nota.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha_registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Listar_Notas.this, "Actualizar nota", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Notas_List.this, Modify_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha_registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    }
                });
                return viewModel_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Notas_List.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firebaseRecyclerAdapter);

    }

    private void ListarNotasNoFinalizadas(){
        //consulta
        String estado_nota = "No finalizado";
        Query query = BD_Usuarios.child(user.getUid()).child("Notas_Publicadas").orderByChild("estado").equalTo(estado_nota);
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewModel_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewModel_Nota viewModel_nota, int position, @NotNull Nota nota) {
                viewModel_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );
            }


            @Override
            public ViewModel_Nota onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewModel_Nota viewModel_nota = new ViewModel_Nota(view);
                viewModel_nota.setOnClickListener(new ViewModel_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Enviamos los datos a la siguiente actividad
                        Intent intent = new Intent(Notas_List.this, Detail_Nota.class);
                        intent.putExtra("id_nota", id_nota);
                        intent.putExtra("uid_usuario", uid_usuario);
                        intent.putExtra("correo_usuario", correo_usuario);
                        intent.putExtra("fecha_registro", fecha_registro);
                        intent.putExtra("titulo", titulo);
                        intent.putExtra("descripcion", descripcion);
                        intent.putExtra("fecha_nota", fecha_nota);
                        intent.putExtra("estado", estado);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                        //Obtener los datos de la nota seleccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conexión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //Inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EliminarNota(id_nota);
                                dialog.dismiss();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Listar_Notas.this, "Actualizar nota", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                Intent intent = new Intent(Notas_List.this, Modify_Nota.class);
                                intent.putExtra("id_nota", id_nota);
                                intent.putExtra("uid_usuario", uid_usuario);
                                intent.putExtra("correo_usuario", correo_usuario);
                                intent.putExtra("fecha_registro", fecha_registro);
                                intent.putExtra("titulo", titulo);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("fecha_nota", fecha_nota);
                                intent.putExtra("estado", estado);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    }
                });
                return viewModel_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Notas_List.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firebaseRecyclerAdapter);

    }

    private void EliminarNota(String id_nota) {

        AlertDialog.Builder builder = new AlertDialog.Builder(Notas_List.this);
        builder.setTitle("Eliminar nota");
        builder.setMessage("¿Desea eliminar la nota?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ELIMINAR NOTA EN BD
                Query query = BD_Usuarios.child(user.getUid()).child("Notas_Publicadas").orderByChild("id_nota").equalTo(id_nota);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Notas_List.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(Notas_List.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Notas_List.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    private void Vaciar_Registro_De_Notas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Notas_List.this);
        builder.setTitle("Vaciar todos los registros");
        builder.setMessage("¿Estás seguro(a) de eliminar todas las notas?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Eliminación de todas las notas
                Query query = BD_Usuarios.child(user.getUid()).child("Notas_Publicadas");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Notas_List.this, "Todas las notas se han eliminado correctamente", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Notas_List.this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Vaciar_Todas_Las_Notas){
            Vaciar_Registro_De_Notas();
        }
        if (item.getItemId() == R.id.Filtrar_Notas){
            FiltrarNotas();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void FiltrarNotas(){
        Button Todas_notas, Notas_Finalizadas, Notas_No_Finalizadas;

        dialog_filtrar.setContentView(R.layout.cuadro_dialogo_filtrar_notas);

        Todas_notas = dialog_filtrar.findViewById(R.id.Todas_notas);
        Notas_Finalizadas = dialog_filtrar.findViewById(R.id.Notas_Finalizadas);
        Notas_No_Finalizadas = dialog_filtrar.findViewById(R.id.Notas_No_Finalizadas);

        Todas_notas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Todas");
                editor.apply();
                recreate();
                Toast.makeText(Notas_List.this, "Todas las notas", Toast.LENGTH_SHORT).show();
                dialog_filtrar.dismiss();
            }
        });

        Notas_Finalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "Finalizados");
                editor.apply();
                recreate();
                Toast.makeText(Notas_List.this, "Notas finalizadas", Toast.LENGTH_SHORT).show();
                dialog_filtrar.dismiss();
            }
        });

        Notas_No_Finalizadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Listar", "No finalizados");
                editor.apply();
                recreate();
                Toast.makeText(Notas_List.this, "Notas no finalizadas", Toast.LENGTH_SHORT).show();
                dialog_filtrar.dismiss();
            }
        });

        dialog_filtrar.show();
    }

    private void Estado_Filtro(){
        sharedPreferences = Notas_List.this.getSharedPreferences("Notas", MODE_PRIVATE);

        String estado_filtro = sharedPreferences.getString("Listar", "Todas");

        switch (estado_filtro){
            case "Todas":
                ListarTodasNotas();
                break;
            case "Finalizados":
                ListarNotasFinalizadas();
                break;
            case "No finalizados":
                ListarNotasNoFinalizadas();
                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}