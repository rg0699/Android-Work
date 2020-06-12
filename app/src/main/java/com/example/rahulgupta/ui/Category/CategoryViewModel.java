package com.example.rahulgupta.ui.Category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class CategoryViewModel extends ViewModel {

    public static MutableLiveData<ArrayList<String>> categoryList;
    public static ArrayList<String> categoryStringList;

    LiveData<ArrayList<String>> getCategoryList() {
        if (categoryList == null) {
            categoryList = new MutableLiveData<>();
            categoryStringList = new ArrayList<>();
            loadCategory();
        }
        return categoryList;
    }

    private void loadCategory() {
        categoryStringList.add("Food");
        categoryStringList.add("Travel");
        categoryStringList.add("Clothes");
        categoryStringList.add("Movies");
        categoryStringList.add("Health");
        categoryStringList.add("Grocery");
        //categoryStringList.add("Other");
        categoryList.setValue(categoryStringList);
    }
}