package tn.esprit.eventsphere.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.eventsphere.entity.Category;

@Dao
public interface CategoryDao {

    @Insert
    long insertCategory(Category category);

    @Query("SELECT * FROM category")  // Assurez-vous que le nom de la table est correct
    List<Category> getAllCategories();

    @Delete
    void deleteCategory(Category category);

    @Update
    void updateCategory(Category category);
}
