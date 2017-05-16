package br.usp.ime.checkattendance.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.ClickListener;

/**
 * Created by kanashiro on 5/6/17.
 */

public class SeminarAdapter extends RecyclerView.Adapter<SeminarAdapter.SeminarViewHolder> {

    private ArrayList<Seminar> seminars;
    private String type;
    private ClickListener listener;

    public class SeminarViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextView;
        private View itemView;

        public SeminarViewHolder(View view) {
            super(view);
            this.itemView = view;

            this.mCardView = (CardView) this.itemView.findViewById(R.id.card_view);
            this.mTextView = (TextView) this.itemView.findViewById(R.id.tv_text);
        }

        public void bind(final Seminar seminar, final ClickListener listener) {
            this.mTextView.setText(seminar.getName());
            if (listener != null)
                this.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSeminarClick(seminar);
                    }
                });
        }
    }

    public SeminarAdapter(ArrayList<Seminar> myDataset, String type, ClickListener listener) {
        this.seminars = myDataset;
        this.type = type;
        this.listener = listener;
    }

    @Override
    public SeminarAdapter.SeminarViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new SeminarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SeminarViewHolder holder, int position) {
        holder.bind(this.seminars.get(position), this.listener);
    }

    @Override
    public int getItemCount() {
        return this.seminars.size();
    }
}
