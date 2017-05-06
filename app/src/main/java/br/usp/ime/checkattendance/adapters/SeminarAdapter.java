package br.usp.ime.checkattendance.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.models.Seminar;

/**
 * Created by kanashiro on 5/6/17.
 */

public class SeminarAdapter extends RecyclerView.Adapter<SeminarAdapter.SeminarViewHolder> {

    private ArrayList<Seminar> seminars;

    public static class SeminarViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView;
        public SeminarViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.card_view);
            mTextView = (TextView) v.findViewById(R.id.tv_text);
        }
    }

    public SeminarAdapter(ArrayList<Seminar> myDataset) {
        this.seminars = myDataset;
    }

    @Override
    public SeminarAdapter.SeminarViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        SeminarViewHolder vh = new SeminarViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SeminarViewHolder holder, int position) {
        holder.mTextView.setText(this.seminars.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.seminars.size();
    }
}
