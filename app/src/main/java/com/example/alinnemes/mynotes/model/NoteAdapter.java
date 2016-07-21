package com.example.alinnemes.mynotes.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alinnemes.mynotes.R;
import com.example.alinnemes.mynotes.model.Note;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by alin.nemes on 13-Jul-16.
 */
public class NoteAdapter extends ArrayAdapter<Note> {

    public static class ViewHolder {
        TextView noteSubject;
        TextView noteBody;
    }

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);

        ViewHolder holder;

        //check if an existing view is being reused, otherwise inflate a new view from custom row layout
        if (convertView == null) {
            //instance the newViewHolder to hold the references
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_item, parent, false);
            holder.noteSubject = (TextView) convertView.findViewById(R.id.listItemNoteSubject);
            holder.noteBody = (TextView) convertView.findViewById(R.id.listItemNoteBody);

            //set tag to remember in holder the references for the widgets :)
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //fill each new reference view with concrete data
        holder.noteSubject.setText(note.getSubject());
        holder.noteBody.setText(note.getBody());

        return convertView;
    }

}
