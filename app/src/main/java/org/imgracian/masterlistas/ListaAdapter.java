package org.imgracian.masterlistas;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter <ListaAdapter.ListaViewHolder> {
    private List<Lista> items;

    public static class ListaViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView imagen;
        public TextView nombre;
        public TextView elementos;

        public ListaViewHolder(View v) {
            super(v);
            imagen =  v.findViewById(R.id.imagen);
            nombre =  v.findViewById(R.id.nombre);
            elementos =  v.findViewById(R.id.elementos);
        }
    }

    public ListaAdapter(List<Lista> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ListaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.elemento_lista, viewGroup, false);
        return new ListaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListaViewHolder viewHolder, int i) {
        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.nombre.setText(items.get(i).getNombre());
        viewHolder.elementos.setText("Items:"
                +String.valueOf(items.get(i).getElementos()));
    }
}
