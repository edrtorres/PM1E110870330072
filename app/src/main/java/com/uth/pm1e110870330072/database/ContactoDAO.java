package com.uth.pm1e110870330072.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uth.pm1e110870330072.models.Contacto;

import java.util.ArrayList;

public class ContactoDAO {

    private ContactosDbHelper dbHelper;

    public ContactoDAO(Context context) {
        dbHelper = new ContactosDbHelper(context);
    }

    public long insertarContacto(Contacto contacto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(ContactosDbHelper.COL_PAIS, contacto.getPais());
        valores.put(ContactosDbHelper.COL_NOMBRE, contacto.getNombre());
        valores.put(ContactosDbHelper.COL_TELEFONO, contacto.getTelefono());
        valores.put(ContactosDbHelper.COL_NOTA, contacto.getNota());
        valores.put(ContactosDbHelper.COL_IMAGEN, contacto.getImagen());

        long resultado = db.insert(ContactosDbHelper.TABLE_CONTACTOS, null, valores);
        db.close();

        return resultado;
    }

    public ArrayList<Contacto> obtenerContactos() {
        ArrayList<Contacto> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                ContactosDbHelper.TABLE_CONTACTOS,
                null,
                null,
                null,
                null,
                null,
                ContactosDbHelper.COL_NOMBRE + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Contacto contacto = new Contacto();

                contacto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_ID)));
                contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_PAIS)));
                contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_NOMBRE)));
                contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_TELEFONO)));
                contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_NOTA)));
                contacto.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_IMAGEN)));

                lista.add(contacto);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public Contacto obtenerContactoPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                ContactosDbHelper.TABLE_CONTACTOS,
                null,
                ContactosDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Contacto contacto = null;

        if (cursor.moveToFirst()) {
            contacto = new Contacto();

            contacto.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_ID)));
            contacto.setPais(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_PAIS)));
            contacto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_NOMBRE)));
            contacto.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_TELEFONO)));
            contacto.setNota(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_NOTA)));
            contacto.setImagen(cursor.getString(cursor.getColumnIndexOrThrow(ContactosDbHelper.COL_IMAGEN)));
        }

        cursor.close();
        db.close();

        return contacto;
    }

    public int actualizarContacto(Contacto contacto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(ContactosDbHelper.COL_PAIS, contacto.getPais());
        valores.put(ContactosDbHelper.COL_NOMBRE, contacto.getNombre());
        valores.put(ContactosDbHelper.COL_TELEFONO, contacto.getTelefono());
        valores.put(ContactosDbHelper.COL_NOTA, contacto.getNota());
        valores.put(ContactosDbHelper.COL_IMAGEN, contacto.getImagen());

        int resultado = db.update(
                ContactosDbHelper.TABLE_CONTACTOS,
                valores,
                ContactosDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(contacto.getId())}
        );

        db.close();

        return resultado;
    }

    public int eliminarContacto(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int resultado = db.delete(
                ContactosDbHelper.TABLE_CONTACTOS,
                ContactosDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return resultado;
    }
}