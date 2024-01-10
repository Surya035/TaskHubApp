package com.example.taskhubapp.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskhubapp.R;
import com.example.taskhubapp.localstorage.TasksDBHelper;
import com.example.taskhubapp.model.Task;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnItemClickListener clickListener;
    private RecyclerView recyclerView;
    private TasksDBHelper dbHelper;



    public TasksAdapter(List<Task> taskList, RecyclerView recyclerView) {
        this.taskList = taskList;
        this.recyclerView = recyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dueDateTextView;
        private TextView categoryTextView;
        private TextView priorityTextView;
        private TextView statusTextView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(taskList.get(position));// Mark the task as not new after clicking
                        notifyItemChanged(position); // Update the view
                    }
                }
            });
        }

        void bind(Task task) {
            titleTextView.setText(task.getTitle());
            dueDateTextView.setText("Due Date: " + task.getDueDate());
            categoryTextView.setText("Category: " + task.getCategory());
            priorityTextView.setText("Priority: " + task.getPriority());
            statusTextView.setText(task.getStatus());

        }
    }


    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

}

