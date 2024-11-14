package tn.esprit.eventsphere;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.eventsphere.entity.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private CategoryActionListener categoryActionListener;

    public CategoryAdapter(List<Category> categoryList, CategoryActionListener categoryActionListener) {
        this.categoryList = categoryList;
        this.categoryActionListener = categoryActionListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getName());

        holder.itemView.setOnClickListener(v -> categoryActionListener.onEditCategory(category, position));
        holder.itemView.setOnLongClickListener(v -> {
            categoryActionListener.onDeleteCategory(category, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface CategoryActionListener {
        void onEditCategory(Category category, int position);
        void onDeleteCategory(Category category, int position);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryNameTextView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryNameTextView);
        }
    }
}
