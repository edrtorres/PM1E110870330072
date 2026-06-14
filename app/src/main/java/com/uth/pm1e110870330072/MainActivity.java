package com.uth.pm1e110870330072;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uth.pm1e110870330072.database.ContactoDAO;
import com.uth.pm1e110870330072.models.Contacto;
import com.uth.pm1e110870330072.utils.ImagenUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Spinner spPais;
    private EditText txtNombre, txtTelefono, txtNota;
    private ImageView imgContacto;
    private TextView btnSeleccionarImagen, btnGuardar, btnVerLista;
    private TextView txtTituloMain, txtSubtituloMain;

    private ContactoDAO contactoDAO;
    private Bitmap imagenBitmap = null;

    private boolean modoEditar = false;
    private int idContactoEditar = -1;
    private Contacto contactoEditar = null;
    private String imagenActualBase64 = "";

    private static final int REQUEST_GALERIA = 100;
    private static final int REQUEST_CAMARA = 101;
    private static final int REQUEST_PERMISO_CAMARA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVistas();
        cargarPaises();
        configurarFiltrosRegex();
        detectarModoEditar();
        configurarEventos();
    }

    private void inicializarVistas() {
        txtTituloMain = findViewById(R.id.txtTituloMain);
        txtSubtituloMain = findViewById(R.id.txtSubtituloMain);

        spPais = findViewById(R.id.spPais);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);

        imgContacto = findViewById(R.id.imgContacto);

        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVerLista = findViewById(R.id.btnVerLista);

        contactoDAO = new ContactoDAO(this);
    }

    private void cargarPaises() {
        String[] paises = {
                "Seleccione un país",
                "Honduras (504)",
                "Costa Rica (506)",
                "Guatemala (502)",
                "El Salvador (503)",
                "Nicaragua (505)",
                "Panamá (507)",
                "Belice (501)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPais.setAdapter(adapter);
    }

    private void detectarModoEditar() {
        String modo = getIntent().getStringExtra("modo");
        idContactoEditar = getIntent().getIntExtra("idContacto", -1);

        if (modo != null && modo.equals("editar") && idContactoEditar != -1) {
            modoEditar = true;
            cargarContactoParaEditar();
        } else {
            modoEditar = false;
        }
    }

    private void cargarContactoParaEditar() {
        contactoEditar = contactoDAO.obtenerContactoPorId(idContactoEditar);

        if (contactoEditar == null) {
            mostrarAlerta("Error", "No se encontró el contacto a actualizar.");
            return;
        }

        txtTituloMain.setText("Actualizar Contacto");
        txtSubtituloMain.setText("Modifica la información del contacto");
        btnGuardar.setText("Actualizar Contacto");

        seleccionarPais(contactoEditar.getPais());
        txtNombre.setText(contactoEditar.getNombre());
        txtTelefono.setText(contactoEditar.getTelefono());
        txtNota.setText(contactoEditar.getNota());

        imagenActualBase64 = contactoEditar.getImagen();

        Bitmap bitmap = ImagenUtils.base64ToBitmap(imagenActualBase64);

        if (bitmap != null) {
            imagenBitmap = bitmap;
            imgContacto.setImageBitmap(bitmap);
            imgContacto.setPadding(0, 0, 0, 0);
        } else {
            imagenBitmap = null;
            imgContacto.setImageResource(android.R.drawable.ic_menu_camera);
            imgContacto.setPadding(22, 22, 22, 22);
        }
    }

    private void seleccionarPais(String paisGuardado) {
        for (int i = 0; i < spPais.getCount(); i++) {
            String paisSpinner = spPais.getItemAtPosition(i).toString();

            if (paisSpinner.equals(paisGuardado)) {
                spPais.setSelection(i);
                return;
            }
        }

        spPais.setSelection(0);
    }

    private void configurarFiltrosRegex() {

        InputFilter filtroLetras = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                String texto = source.toString();

                if (texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*")) {
                    return null;
                }

                return "";
            }
        };

        txtNombre.setFilters(new InputFilter[]{filtroLetras});
        txtTelefono.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});

        txtTelefono.addTextChangedListener(new TextWatcher() {

            private boolean editando = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editando) {
                    return;
                }

                editando = true;

                String numeros = editable.toString().replaceAll("[^0-9]", "");

                if (numeros.length() > 8) {
                    numeros = numeros.substring(0, 8);
                }

                String telefonoFormateado;

                if (numeros.length() > 4) {
                    telefonoFormateado = numeros.substring(0, 4) + "-" + numeros.substring(4);
                } else {
                    telefonoFormateado = numeros;
                }

                txtTelefono.setText(telefonoFormateado);
                txtTelefono.setSelection(txtTelefono.getText().length());

                editando = false;
            }
        });
    }

    private void configurarEventos() {
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOpcionesImagen();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modoEditar) {
                    actualizarContacto();
                } else {
                    guardarContacto();
                }
            }
        });

        btnVerLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListaContactosActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mostrarOpcionesImagen() {
        String[] opciones = {"Tomar foto", "Seleccionar de galería"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imagen del contacto");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                verificarPermisoCamara();
            } else {
                abrirGaleria();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_PERMISO_CAMARA
            );

        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMARA);
        } else {
            mostrarAlerta("Error", "No se encontró una aplicación de cámara disponible.");
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_GALERIA) {
            Uri uriImagen = data.getData();

            try {
                imagenBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImagen);
                imgContacto.setImageBitmap(imagenBitmap);
                imgContacto.setPadding(0, 0, 0, 0);
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo cargar la imagen seleccionada.");
            }
        }

        if (requestCode == REQUEST_CAMARA) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                imagenBitmap = (Bitmap) extras.get("data");

                if (imagenBitmap != null) {
                    imgContacto.setImageBitmap(imagenBitmap);
                    imgContacto.setPadding(0, 0, 0, 0);
                } else {
                    mostrarAlerta("Error", "No se pudo obtener la foto tomada.");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                mostrarAlerta("Permiso requerido", "Debe permitir el uso de la cámara para tomar una foto.");
            }
        }
    }

    private void guardarContacto() {
        String pais = spPais.getSelectedItem().toString();
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();

        if (!validarCampos(nombre, telefono, nota)) {
            return;
        }

        String imagenBase64 = ImagenUtils.bitmapToBase64(imagenBitmap);

        Contacto contacto = new Contacto(pais, nombre, telefono, nota, imagenBase64);

        long resultado = contactoDAO.insertarContacto(contacto);

        if (resultado > 0) {
            mostrarAlerta("Registro exitoso", "El contacto fue guardado correctamente.");
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el contacto.");
        }
    }

    private void actualizarContacto() {
        String pais = spPais.getSelectedItem().toString();
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();

        if (!validarCampos(nombre, telefono, nota)) {
            return;
        }

        String imagenBase64;

        if (imagenBitmap != null) {
            imagenBase64 = ImagenUtils.bitmapToBase64(imagenBitmap);
        } else {
            imagenBase64 = imagenActualBase64;
        }

        Contacto contacto = new Contacto(
                idContactoEditar,
                pais,
                nombre,
                telefono,
                nota,
                imagenBase64
        );

        int resultado = contactoDAO.actualizarContacto(contacto);

        if (resultado > 0) {
            mostrarAlertaConFinalizar("Actualizado", "El contacto fue actualizado correctamente.");
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el contacto.");
        }
    }

    private boolean validarCampos(String nombre, String telefono, String nota) {
        if (spPais.getSelectedItemPosition() == 0) {
            mostrarAlerta("Campo obligatorio", "Debe seleccionar un país.");
            spPais.requestFocus();
            return false;
        }

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo obligatorio", "Debe ingresar el nombre.");
            txtNombre.requestFocus();
            return false;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,}")) {
            mostrarAlerta("Nombre inválido", "El nombre solo debe contener letras y mínimo 3 caracteres.");
            txtNombre.requestFocus();
            return false;
        }

        if (telefono.isEmpty()) {
            mostrarAlerta("Campo obligatorio", "Debe ingresar el teléfono.");
            txtTelefono.requestFocus();
            return false;
        }

        if (!telefono.matches("[0-9]{4}-[0-9]{4}")) {
            mostrarAlerta("Teléfono inválido", "El teléfono debe tener el formato 9999-9999.");
            txtTelefono.requestFocus();
            return false;
        }

        if (nota.isEmpty()) {
            mostrarAlerta("Campo obligatorio", "Debe ingresar una nota.");
            txtNota.requestFocus();
            return false;
        }

        if (imagenBitmap == null && (imagenActualBase64 == null || imagenActualBase64.trim().isEmpty())) {
            mostrarAlerta("Imagen obligatoria", "Debe seleccionar o tomar una imagen para el contacto.");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        spPais.setSelection(0);
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");

        imagenBitmap = null;
        imagenActualBase64 = "";

        imgContacto.setImageResource(android.R.drawable.ic_menu_camera);
        imgContacto.setPadding(22, 22, 22, 22);

        spPais.requestFocus();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", null);
        builder.show();
    }

    private void mostrarAlertaConFinalizar(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar", (dialog, which) -> finish());
        builder.show();
    }
}