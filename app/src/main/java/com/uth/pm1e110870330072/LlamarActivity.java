package com.uth.pm1e110870330072;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uth.pm1e110870330072.database.ContactoDAO;
import com.uth.pm1e110870330072.models.Contacto;
import com.uth.pm1e110870330072.utils.ImagenUtils;

public class LlamarActivity extends AppCompatActivity {

    private ImageView btnBackLlamar, imgLlamarContacto;
    private TextView txtNombreLlamar, txtPaisLlamar, txtTelefonoLlamar, btnRealizarLlamada;

    private ContactoDAO contactoDAO;
    private Contacto contacto;

    private static final int REQUEST_CALL_PHONE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        inicializarVistas();
        cargarContacto();
        configurarEventos();

        boolean llamarAutomatico = getIntent().getBooleanExtra("llamarAutomatico", false);

        if (llamarAutomatico && contacto != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    String telefonoCompleto = obtenerTelefonoConCodigo(contacto);
                    verificarPermisoLlamada(telefonoCompleto);
                }
            }, 600);
        }
    }

    private void inicializarVistas() {
        btnBackLlamar = findViewById(R.id.btnBackLlamar);
        imgLlamarContacto = findViewById(R.id.imgLlamarContacto);

        txtNombreLlamar = findViewById(R.id.txtNombreLlamar);
        txtPaisLlamar = findViewById(R.id.txtPaisLlamar);
        txtTelefonoLlamar = findViewById(R.id.txtTelefonoLlamar);
        btnRealizarLlamada = findViewById(R.id.btnRealizarLlamada);

        contactoDAO = new ContactoDAO(this);
    }

    private void cargarContacto() {
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

        txtNombreLlamar.setText(contacto.getNombre());
        txtPaisLlamar.setText(contacto.getPais());
        txtTelefonoLlamar.setText(obtenerTelefonoConCodigo(contacto));

        Bitmap bitmap = ImagenUtils.base64ToBitmap(contacto.getImagen());

        if (bitmap != null) {
            imgLlamarContacto.setImageBitmap(bitmap);
            imgLlamarContacto.setPadding(0, 0, 0, 0);
        } else {
            imgLlamarContacto.setImageResource(android.R.drawable.ic_menu_camera);
            imgLlamarContacto.setPadding(18, 18, 18, 18);
        }
    }

    private void configurarEventos() {
        btnBackLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnRealizarLlamada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarLlamada();
            }
        });
    }

    private String obtenerTelefonoConCodigo(Contacto contacto) {
        String pais = contacto.getPais();
        String telefono = contacto.getTelefono().replace("-", "");

        if (pais.contains("504")) {
            return "+504" + telefono;
        } else if (pais.contains("506")) {
            return "+506" + telefono;
        } else if (pais.contains("502")) {
            return "+502" + telefono;
        } else if (pais.contains("503")) {
            return "+503" + telefono;
        } else if (pais.contains("505")) {
            return "+505" + telefono;
        } else if (pais.contains("507")) {
            return "+507" + telefono;
        } else if (pais.contains("501")) {
            return "+501" + telefono;
        }

        return telefono;
    }

    private void confirmarLlamada() {
        if (contacto == null) {
            mostrarAlerta("Error", "No hay contacto seleccionado.");
            return;
        }

        String telefonoCompleto = obtenerTelefonoConCodigo(contacto);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar llamada");
        builder.setMessage("¿Desea llamar a " + contacto.getNombre() + "?\n\n" + telefonoCompleto);

        builder.setPositiveButton("Sí", (dialog, which) -> verificarPermisoLlamada(telefonoCompleto));
        builder.setNegativeButton("No", null);

        builder.show();
    }

    private void verificarPermisoLlamada(String telefonoCompleto) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE
            );

        } else {
            realizarLlamada(telefonoCompleto);
        }
    }

    private void realizarLlamada(String telefonoCompleto) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + telefonoCompleto));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (contacto != null) {
                    realizarLlamada(obtenerTelefonoConCodigo(contacto));
                }
            } else {
                mostrarAlerta("Permiso requerido", "Debe permitir llamadas para usar esta función.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);
        builder.show();
    }
}