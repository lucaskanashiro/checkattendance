package br.usp.ime.checkattendance.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.models.Student;
import br.usp.ime.checkattendance.utils.ClickListener;

/**
 * Created by kanashiro on 5/11/17.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private ArrayList<Student> students;

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewStudent;
        private View itemView;

        public StudentViewHolder(View view) {
            super(view);
            this.itemView = view;

            this.mTextViewStudent = (TextView) this.itemView.findViewById(R.id.tv_text_student);
        }

        public void bind(final Student student) {
            this.mTextViewStudent.setText(student.getName() + " (NUSP: " + student.getNusp() + ")");
        }
    }

    public StudentAdapter(ArrayList<Student> myDataset) {
        this.students = myDataset;
    }

    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_students,
                parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        holder.bind(this.students.get(position));
    }

    @Override
    public int getItemCount() {
        return this.students.size();
    }
}
