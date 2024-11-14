package tn.esprit.eventsphere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.eventsphere.entity.Category;  // Import Category class
import tn.esprit.eventsphere.database.AppDatabase;  // Import AppDatabase class

public class AddCategoryActivity extends AppCompatActivity {

    private EditText categoryName;
    private Button saveCategoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Bind UI elements
        categoryName = findViewById(R.id.categoryName);
        saveCategoryButton = findViewById(R.id.saveCategoryButton);

        // Save Button click listener
        saveCategoryButton.setOnClickListener(v -> {
            String name = categoryName.getText().toString().trim();

            // Check if the category name is empty
            if (name.isEmpty()) {
                Toast.makeText(AddCategoryActivity.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Category object
            Category category = new Category(name);

            // Insert category into the database in a background thread
            new Thread(() -> {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    long categoryId = db.categoryDao().insertCategory(category);

                    // Run on UI thread to show the result and redirect
                    runOnUiThread(() -> {
                        if (categoryId > 0) {
                            Toast.makeText(AddCategoryActivity.this, "Category added!", Toast.LENGTH_SHORT).show();

                            // Redirect to CategoryListActivity after adding the category
                            Intent intent = new Intent(AddCategoryActivity.this, CategoryListActivity.class);
                            startActivity(intent);
                            finish();  // Close the current activity
                        } else {
                            Toast.makeText(AddCategoryActivity.this, "Error adding category", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    // Handle any errors in database operation
                    runOnUiThread(() -> Toast.makeText(AddCategoryActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });
    }
}
