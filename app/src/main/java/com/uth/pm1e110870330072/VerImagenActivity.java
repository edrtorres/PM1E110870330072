package com.uth.pm1e110870330072;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uth.pm1e110870330072.database.ContactoDAO;
import com.uth.pm1e110870330072.models.Contacto;
import com.uth.pm1e110870330072.utils.ImagenUtils;

public class VerImagenActivity extends AppCompatActivity {

    private TextView btnBackVerImagen;
    private ImageView imgVerContacto;
    private TextView txtNombreVerImagen, txtTelefonoVerImagen;

    private ContactoDAO contactoDAO;
    private Contacto contacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);

        inicializarVistas();
        cargarDatosContacto();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnBackVerImagen = findViewById(R.id.btnBackVerImagen);
        imgVerContacto = findViewById(R.id.imgVerContacto);
        txtNombreVerImagen = findViewById(R.id.txtNombreVerImagen);
        txtTelefonoVerImagen = findViewById(R.id.txtTelefonoVerImagen);

        contactoDAO = new ContactoDAO(this);
    }

    private void cargarDatosContacto() {
        int idContacto = getIntent().getIntExtra("idContacto", -1);

        if (idContacto == -1) {
            mostrarAlerta("Error", "No se recibió el contacto.");
            return;
        }

        contacto = contactoDAO.obtenerContactoPorId(idContacto);

        if (contacto == null) {
            mostrarAlerta("Error", "No se encontró el contacto.");
            return;
        }

        txtNombreVerImagen.setText(contacto.getNombre());
        txtTelefonoVerImagen.setText(contacto.getTelefono());

        Bitmap bitmap = ImagenUtils.base64ToBitmap(contacto.getImagen());

        if (bitmap != null) {
            imgVerContacto.setImageBitmap(bitmap);
            imgVerContacto.setPadding(0, 0, 0, 0);
        } else {
            imgVerContacto.setImageResource(android.R.drawable.ic_menu_camera);
            imgVerContacto.setPadding(30, 30, 30, 30);
        }
    }

    private void configurarEventos() {
        btnBackVerImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);
        builder.show();
    }
}