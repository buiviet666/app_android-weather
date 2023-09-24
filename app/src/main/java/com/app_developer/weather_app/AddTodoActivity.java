package com.app_developer.weather_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddTodoActivity extends AppCompatActivity {

    private EditText inputText;
    private ListView listView;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();
        inputText = findViewById(R.id.inputText);
        listView = findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item_layout, R.id.textViewItem, list);
        listView.setAdapter(adapter);

        // Xử lý khi người dùng chọn một mục trong ListView để xóa hoặc sửa
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoActivity.this);
                builder.setTitle("Chọn hành động");
                builder.setItems(new CharSequence[]{"Sửa", "Xóa"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // Xử lý khi người dùng chọn sửa
                            showEditDialog(position);
                        } else {
                            // Xử lý khi người dùng chọn xóa
                            deleteTask(position);
                        }
                    }
                });
                builder.show();
            }
        });

        // Load danh sách công việc khi ứng dụng khởi động
        updateListView();
    }

    public void onClickAdd(View v) {
        String text = inputText.getText().toString().trim();

        if (text.isEmpty()) {
            // Hiển thị thông báo khi không có nội dung nhập
            Toast.makeText(this, "Hãy nhập nội dung.", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm công việc vào cơ sở dữ liệu
            addTask(text);
            inputText.setText(""); // Xóa nội dung sau khi thêm
        }
    }


    private void addTask(String task) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK, task);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
        updateListView();
    }

    private void deleteTask(int position) {
        String task = list.get(position);
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_TASK + "=?", new String[]{task});
        updateListView();
    }

    // Hiển thị dialog để sửa nội dung
    private void showEditDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextItem = dialogView.findViewById(R.id.editTextItem);
        editTextItem.setText(list.get(position));

        dialogBuilder.setTitle("Sửa nội dung");
        dialogBuilder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String editedText = editTextItem.getText().toString().trim();
                if (!editedText.isEmpty()) {
                    editTask(position, editedText);
                }
            }
        });
        dialogBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Bỏ qua
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void editTask(int position, String editedText) {
        String oldTask = list.get(position);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK, editedText);
        database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_TASK + "=?", new String[]{oldTask});
        updateListView();
    }

    // Cập nhật hiển thị danh sách
    private void updateListView() {
        list.clear();

        // Lấy dữ liệu từ cơ sở dữ liệu
        Cursor cursor = database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String task = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK));
                list.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
