package tn.esprit.eventsphere;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.eventsphere.database.AppDatabase;
import tn.esprit.eventsphere.entity.Category;

public class CategoryListActivity extends AppCompatActivity implements CategoryAdapter.CategoryActionListener {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        recyclerView = findViewById(R.id.recyclerView);

        // Initialize RecyclerView and set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch categories from the database in a background thread
        new Thread(() -> {
            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
            categoryList = db.categoryDao().getAllCategories(); // Ensure getAllCategories() is defined in CategoryDao

            // Run UI operations on the main thread
            runOnUiThread(() -> {
                if (categoryList != null && !categoryList.isEmpty()) {
                    // Set the adapter if categories are available
                    if (categoryAdapter == null) {
                        categoryAdapter = new CategoryAdapter(categoryList, this);
                        recyclerView.setAdapter(categoryAdapter);
                    } else {
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    // Handle the case where no categories are found
                    Toast.makeText(CategoryListActivity.this, "No categories found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public void onEditCategory(Category category, int position) {
        // Show an edit dialog to update category details
        showEditCategoryDialog(category, position);
    }

    @Override
    public void onDeleteCategory(Category category, int position) {
        // Handle category deletion in a background thread
        new Thread(() -> {
            AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
            db.categoryDao().deleteCategory(category);  // Ensure deleteCategory() is defined in CategoryDao

            // Run UI operations on the main thread
            runOnUiThread(() -> {
                categoryList.remove(position);
                categoryAdapter.notifyItemRemoved(position);
                Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void showEditCategoryDialog(Category category, int position) {
        // Create an AlertDialog for editing the category
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category");

        // Inflate custom layout for editing
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
        builder.setView(dialogView);

        // Initialize EditText and set existing category details
        EditText editCategoryName = dialogView.findViewById(R.id.editCategoryName);
        editCategoryName.setText(category.getName());

        // Handle the "Save" button click
        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedName = editCategoryName.getText().toString();

            // Update category properties
            category.setName(updatedName);

            // Update category in the database in a background thread
            new Thread(() -> {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                db.categoryDao().updateCategory(category);  // Ensure updateCategory() is defined in CategoryDao

                // Run UI operations on the main thread
                runOnUiThread(() -> {
                    // Update the RecyclerView item
                    categoryList.set(position, category);
                    categoryAdapter.notifyItemChanged(position);
                    Toast.makeText(CategoryListActivity.this, "Category updated", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        // Handle the "Cancel" button click
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }
}
