package com.uth.pm1e110870330072.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uth.pm1e110870330072.R;
import com.uth.pm1e110870330072.models.Contacto;
import com.uth.pm1e110870330072.utils.ImagenUtils;

import java.util.ArrayList;

public class ContactoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Contacto> listaContactos;
    private int posicionSeleccionada = -1;

    public ContactoAdapter(Context context, ArrayList<Contacto> listaContactos) {
        this.context = context;
        this.listaContactos = listaContactos;
    }

    @Override
    public int getCount() {
        return listaContactos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaContactos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaContactos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vista = convertView;

        if (vista == null) {
            vista = LayoutInflater.from(context).inflate(R.layout.item_contacto, parent, false);
        }

        LinearLayout itemContacto = vista.findViewById(R.id.itemContacto);
        ImageView imgItemContacto = vista.findViewById(R.id.imgItemContacto);
        TextView txtItemNombre = vista.findViewById(R.id.txtItemNombre);
        TextView txtItemTelefono = vista.findViewById(R.id.txtItemTelefono);
        TextView txtItemPais = vista.findViewById(R.id.txtItemPais);

        Contacto contacto = listaContactos.get(position);

        txtItemNombre.setText(contacto.getNombre());
        txtItemTelefono.setText(contacto.getTelefono());
        txtItemPais.setText(contacto.getPais());

        Bitmap bitmap = ImagenUtils.base64ToBitmap(contacto.getImagen());

        if (bitmap != null) {
            imgItemContacto.setImageBitmap(bitmap);
            imgItemContacto.setPadding(0, 0, 0, 0);
        } else {
            imgItemContacto.setImageResource(android.R.drawable.ic_menu_camera);
            imgItemContacto.setPadding(10, 10, 10, 10);
        }

        if (position == posicionSeleccionada) {
            itemContacto.setBackgroundResource(R.drawable.bg_contact_item_selected);
            itemContacto.setAlpha(1.0f);
            txtItemNombre.setTextColor(context.getResources().getColor(R.color.primary_blue));
        } else {
            itemContacto.setBackgroundResource(R.drawable.bg_contact_item);
            itemContacto.setAlpha(1.0f);
            txtItemNombre.setTextColor(context.getResources().getColor(R.color.text_primary));
        }

        return vista;
    }

    public void actualizarLista(ArrayList<Contacto> nuevaLista) {
        this.listaContactos = nuevaLista;
        notifyDataSetChanged();
    }

    public void setPosicionSeleccionada(int posicionSeleccionada) {
        this.posicionSeleccionada = posicionSeleccionada;
        notifyDataSetChanged();
    }

    public int getPosicionSeleccionada() {
        return posicionSeleccionada;
    }

    public Contacto getContactoSeleccionado() {
        if (posicionSeleccionada >= 0 && posicionSeleccionada < listaContactos.size()) {
            return listaContactos.get(posicionSeleccionada);
        }

        return null;
    }
}s