package com.uth.pm1e110870330072;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uth.pm1e110870330072.adapters.ContactoAdapter;
import com.uth.pm1e110870330072.database.ContactoDAO;
import com.uth.pm1e110870330072.models.Contacto;

import java.util.ArrayList;

public class ListaContactosActivity extends AppCompatActivity {

    private ImageView btnBackLista;
    private TextView txtContadorContactos;
    private EditText txtBuscarContacto;
    private ListView listViewContactos;

    private TextView btnCompartirContacto;
    private TextView btnVerImagen;
    private TextView btnActualizarContacto;
    private TextView btnEliminarContacto;

    private ContactoDAO contactoDAO;
    private ContactoAdapter contactoAdapter;

    private ArrayList<Contacto> listaContactos;
    private ArrayList<Contacto> listaFiltrada;

    private Contacto contactoSeleccionado = null;
    private int posicionSeleccionadaActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        inicializarVistas();
        configurarEventos();
        cargarContactos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarContactos();
    }

    private void inicializarVistas() {
        btnBackLista = findViewById(R.id.btnBackLista);
        txtContadorContactos = findViewById(R.id.txtContadorContactos);
        txtBuscarContacto = findViewById(R.id.txtBuscarContacto);
        listViewContactos = findViewById(R.id.listViewContactos);

        btnCompartirContacto = findViewById(R.id.btnCompartirContacto);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnActualizarContacto = findViewById(R.id.btnActualizarContacto);
        btnEliminarContacto = findViewById(R.id.btnEliminarContacto);

        contactoDAO = new ContactoDAO(this);

        listaContactos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();
    }

    private void configurarEventos() {

        btnBackLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listViewContactos.setOnItemClickListener((parent, view, position, id) -> {
            Contacto contactoActual = listaFiltrada.get(position);

            if (contactoSeleccionado != null && contactoSeleccionado.getId() == contactoActual.getId()) {
                confirmarAbrirLlamada(contactoActual);
            } else {
                contactoSeleccionado = contactoActual;
                posicionSeleccionadaActual = position;
                contactoAdapter.setPosicionSeleccionada(position);
            }
        });

        txtBuscarContacto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence texto, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence texto, int start, int before, int count) {
                filtrarContactos(texto.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnCompartirContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compartirContacto();
            }
        });

        btnVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verImagenContacto();
            }
        });

        btnActualizarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarContacto();
            }
        });

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminarContacto();
            }
        });
    }

    private void cargarContactos() {
        listaContactos = contactoDAO.obtenerContactos();
        listaFiltrada = new ArrayList<>(listaContactos);

        contactoAdapter = new ContactoAdapter(this, listaFiltrada);
        listViewContactos.setAdapter(contactoAdapter);

        contactoSeleccionado = null;
        posicionSeleccionadaActual = -1;

        actualizarContador();
    }

    private void filtrarContactos(String textoBusqueda) {
        listaFiltrada.clear();

        String busqueda = textoBusqueda.toLowerCase().trim();

        if (busqueda.isEmpty()) {
            listaFiltrada.addAll(listaContactos);
        } else {
            for (Contacto contacto : listaContactos) {
                String nombre = contacto.getNombre().toLowerCase();
                String telefono = contacto.getTelefono().toLowerCase();
                String pais = contacto.getPais().toLowerCase();

                if (nombre.contains(busqueda) || telefono.contains(busqueda) || pais.contains(busqueda)) {
                    listaFiltrada.add(contacto);
                }
            }
        }

        contactoSeleccionado = null;
        posicionSeleccionadaActual = -1;

        contactoAdapter.setPosicionSeleccionada(-1);
        contactoAdapter.actualizarLista(listaFiltrada);

        actualizarContador();
    }

    private void actualizarContador() {
        int total = listaFiltrada.size();

        if (total == 1) {
            txtContadorContactos.setText("Total: 1 contacto");
        } else {
            txtContadorContactos.setText("Total: " + total + " contactos");
        }
    }

    private boolean validarSeleccion() {
        if (contactoSeleccionado == null) {
            mostrarAlerta("Seleccione un contacto", "Debe seleccionar un contacto de la lista.");
            return false;
        }

        return true;
    }

    private void compartirContacto() {
        if (!validarSeleccion()) {
            return;
        }

        String textoCompartir =
                "Contacto:\n" +
                        "País: " + contactoSeleccionado.getPais() + "\n" +
                        "Nombre: " + contactoSeleccionado.getNombre() + "\n" +
                        "Teléfono: " + contactoSeleccionado.getTelefono() + "\n" +
                        "Nota: " + contactoSeleccionado.getNota();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);

        startActivity(Intent.createChooser(intent, "Compartir contacto"));
    }

    private void verImagenContacto() {
        if (!validarSeleccion()) {
            return;
        }

        Intent intent = new Intent(ListaContactosActivity.this, VerImagenActivity.class);
        intent.putExtra("idContacto", contactoSeleccionado.getId());
        startActivity(intent);
    }

    private void actualizarContacto() {
        if (!validarSeleccion()) {
            return;
        }

        Intent intent = new Intent(ListaContactosActivity.this, MainActivity.class);
        intent.putExtra("modo", "editar");
        intent.putExtra("idContacto", contactoSeleccionado.getId());
        startActivity(intent);
    }

    private void confirmarAbrirLlamada(Contacto contacto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Llamar contacto");
        builder.setMessage("¿Desea abrir la pantalla de llamada para " + contacto.getNombre() + "?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            Intent intent = new Intent(ListaContactosActivity.this, LlamarActivity.class);
            intent.putExtra("idContacto", contacto.getId());
            startActivity(intent);
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void confirmarEliminarContacto() {
        if (!validarSeleccion()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar contacto");
        builder.setMessage("¿Desea eliminar a " + contactoSeleccionado.getNombre() + "?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            int resultado = contactoDAO.eliminarContacto(contactoSeleccionado.getId());

            if (resultado > 0) {
                mostrarAlerta("Eliminado", "El contacto fue eliminado correctamente.");
                cargarContactos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el contacto.");
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);
        builder.show();
    }
}