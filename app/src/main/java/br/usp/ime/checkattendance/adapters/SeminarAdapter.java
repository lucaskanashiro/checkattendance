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
    private String type;

    public static class SeminarViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextView;

        public SeminarViewHolder(View v) {
            super(v);

            this.mCardView = (CardView) v.findViewById(R.id.card_view);
            this.mTextView = (TextView) v.findViewById(R.id.tv_text);
        }

    }

    public SeminarAdapter(ArrayList<Seminar> myDataset, String type) {
        this.seminars = myDataset;
        this.type = type;
    }

    @Override
    public SeminarAdapter.SeminarViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new SeminarViewHolder(v);
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
