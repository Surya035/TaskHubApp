package com.example.taskhubapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskhubapp.adapter.TasksAdapter;
import com.example.taskhubapp.localstorage.TasksContract;
import com.example.taskhubapp.localstorage.TasksDBHelper;
import com.example.taskhubapp.model.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AddTasksBottomSheetFragment.OnTaskAddedListener, TasksAdapter.OnItemClickListener {
    private TasksDBHelper dbHelper;

    private ArrayList<Task> taskList;
    private TextView dateTextView;
    private TextView monthTextView;
    private TextView yearTextView;
    private TasksAdapter adapter; // Create a custom adapter for your Task class
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateTextView = findViewById(R.id.dateTextView);
        monthTextView = findViewById(R.id.monthTextView);
        yearTextView = findViewById(R.id.yearTextView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        dbHelper = new TasksDBHelper(this);
        taskList = new ArrayList<>();
        loadTasksFromDatabase();
        RecyclerView recyclerView = findViewById(R.id.todoRecyclerView);
        View emptyStateLayout = findViewById(R.id.emptyStateLayout);

        adapter = new TasksAdapter(taskList, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        if (taskList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        dateTextView.setText(String.valueOf(dayOfMonth));

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        String monthName = monthFormat.format(calendar.getTime());
        monthTextView.setText(monthName);

        yearTextView.setText(String.valueOf(year));


        RecyclerView todoRecyclerView = findViewById(R.id.todoRecyclerView);
        adapter = new TasksAdapter(taskList,todoRecyclerView);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        // Floating Action Button (FAB)
        FloatingActionButton addTaskFAB = findViewById(R.id.addTaskFAB);
        addTaskFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskBottomSheet();
            }
        });

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        loadTasksFromDatabase();
        adapter.setOnItemClickListener(this);
    }
    private void loadTasksFromDatabase() {
        RecyclerView recyclerView = findViewById(R.id.todoRecyclerView);
        View emptyStateLayout = findViewById(R.id.emptyStateLayout);

        if (taskList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TasksContract.TaskEntry._ID,
                TasksContract.TaskEntry.COLUMN_TITLE,
                TasksContract.TaskEntry.COLUMN_DESCRIPTION,
                TasksContract.TaskEntry.COLUMN_DUE_DATE,
                TasksContract.TaskEntry.COLUMN_PRIORITY,
                TasksContract.TaskEntry.COLUMN_CATEGORY,
                TasksContract.TaskEntry.COLUMN_STATUS
        };

        Cursor cursor = db.query(
                TasksContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        if (taskList == null) {
            taskList = new ArrayList<>();
        } else {
            // Clear the existing data
            taskList.clear();
        }

        while (cursor.moveToNext()) {
            int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_DESCRIPTION));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_DUE_DATE));
            String priority = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_PRIORITY));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_CATEGORY));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(TasksContract.TaskEntry.COLUMN_STATUS));

            Task task = new Task(taskId,title, description, dueDate, priority, category, status);
            taskList.add(task);
        }

        cursor.close();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateVisibility();

    }
    private void updateVisibility() {
        RecyclerView recyclerView = findViewById(R.id.todoRecyclerView);
        View emptyStateLayout = findViewById(R.id.emptyStateLayout);

        if (taskList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
    private void addTaskToDatabase(Task newTask) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TasksContract.TaskEntry.COLUMN_TITLE, newTask.getTitle());
        values.put(TasksContract.TaskEntry.COLUMN_DESCRIPTION, newTask.getDescription());
        values.put(TasksContract.TaskEntry.COLUMN_DUE_DATE, newTask.getDueDate());
        values.put(TasksContract.TaskEntry.COLUMN_PRIORITY, newTask.getPriority());
        values.put(TasksContract.TaskEntry.COLUMN_CATEGORY, newTask.getCategory());
        values.put(TasksContract.TaskEntry.COLUMN_STATUS, newTask.getStatus());
        long newRowId = db.insert(TasksContract.TaskEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            loadTasksFromDatabase();
        } else {

        }
    }
    @Override
    public void onItemClick(Task task) {
        if (task != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (task.getStatus().equals("New")) {
                builder.setTitle("Task Description")
                        .setMessage(task.getDescription())
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task.setStatus("In-Progress");
                                task.setNew(false);
                                updateTaskStatusInDatabase(task); // Update status in the database
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else if ("In-Progress".equals(task.getStatus())) {
                builder.setTitle("Task Description")
                        .setMessage(task.getDescription())
                        .setPositiveButton("Yes, Completed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                task.setStatus("Completed");
                                updateTaskStatusInDatabase(task); // Update status in the database
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }else if ("Completed".equals(task.getStatus())) {
                builder.setTitle("Task Description")
                        .setMessage(task.getDescription())
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTaskFromDatabase(task);
                                updateVisibility();// Update status in the database
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    private void updateTaskStatusInDatabase(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TasksContract.TaskEntry.COLUMN_STATUS, task.getStatus());

        String selection = TasksContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        db.update(TasksContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }
    private void deleteTaskFromDatabase(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TasksContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(task.getId())};

        db.delete(TasksContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
        loadTasksFromDatabase();
    }


    private void showAddTaskBottomSheet() {
        AddTasksBottomSheetFragment bottomSheetFragment = new AddTasksBottomSheetFragment();
        bottomSheetFragment.setOnTaskAddedListener(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    @Override
    public void onTaskAdded(Task newTask) {
        addTaskToDatabase(newTask);
        updateVisibility();
    }


}
