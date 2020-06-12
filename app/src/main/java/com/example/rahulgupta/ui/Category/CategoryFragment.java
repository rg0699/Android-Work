package com.example.rahulgupta.ui.Category;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.rahulgupta.MyDialogFragment;
import com.example.rahulgupta.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class CategoryFragment extends Fragment {//implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView mCategoriesView;
    private View mProgressBar;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter1;
    private FloatingActionButton fab1;
    private Toolbar toolbar;
    private static final String SHARED_PREFS_NAME = "MY_SHARED_PREF";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        mCategoriesView = (ListView) rootView.findViewById(R.id.categories_list_view);
        mProgressBar = rootView.findViewById(R.id.categories_progress_bar);
        fab1=requireActivity().findViewById(R.id.fab);
        fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_add));
        fab1.show();
        mCategoriesView.setEmptyView(rootView.findViewById(R.id.categories_empty_list_view));
        toolbar = requireActivity().findViewById(R.id.toolbar);
        mCategoriesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCategoriesView.getAdapter()==adapter) {
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.fragment_category_select_all);
                    toolbar.inflateMenu(R.menu.category_list_item_delete);
                    mCategoriesView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    mCategoriesView.setAdapter(adapter1);
                    fab1.hide();
                }
                else {
                    toolbar.inflateMenu(R.menu.list_main);
                    toolbar.getMenu().removeItem(R.id.select_all_category_menu_item);
                    toolbar.getMenu().removeItem(R.id.delete_category_menu_item);
                    mCategoriesView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                    mCategoriesView.setAdapter(adapter);
                    fab1.show();
                }
                return true;
            }
        });
        mCategoriesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCategoriesView.getAdapter()==adapter){
                    prepareCategoryToEdit(position);
                }
                else {
                    if (mCategoriesView.isItemChecked(position)) {
                        mCategoriesView.setItemChecked(position, true);
                    }
                    else {
                        mCategoriesView.setItemChecked(position, false);
                    }
                }
            }
        });
        rootView.findViewById(R.id.add_category_button_if_empty_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareCategoryToCreate();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareCategoryToCreate();
            }
        });
        mProgressBar.setVisibility(View.VISIBLE);
        CategoryViewModel model = ViewModelProviders.of(this).get(CategoryViewModel.class);
        model.getCategoryList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> fruitList) {
                adapter = new ArrayAdapter<String>(requireActivity(),
                        android.R.layout.simple_list_item_activated_1, fruitList);
                adapter1 = new ArrayAdapter<String>(requireActivity(),
                        android.R.layout.simple_list_item_multiple_choice, fruitList);
                mCategoriesView.setAdapter(adapter);
                mCategoriesView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
                mProgressBar.setVisibility(View.GONE);
                if(mCategoriesView.getAdapter().isEmpty()){
                    fab1.hide();
                }
                else {
                    fab1.show();
                }
            }
        });
        //registerForContextMenu(mCategoriesView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        inflater.inflate(R.menu.list_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_all_category_menu_item:
                if (mCategoriesView.getCheckedItemCount() != mCategoriesView.getAdapter().getCount())
                    prepareToSelectAll(mCategoriesView);
                else
                    prepareToUnselectAll(mCategoriesView);
                return true;
            case R.id.delete_category_menu_item:
                if(mCategoriesView.getCheckedItemCount()==0){
                    errMsg();
                }
                else {
                    deleteCategory();
                }
                return true;
            case R.id.action_sort_ascending:
                Collections.sort(CategoryViewModel.categoryStringList);
                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                return true;
            case R.id.action_sort_descending:
                Collections.reverse(CategoryViewModel.categoryStringList);
                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
                adapter.notifyDataSetChanged();
                adapter1.notifyDataSetChanged();
                return true;
            case R.id.action_share:
                Intent intent = new Intent ();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_app));
                intent.setType("text/plain");
                startActivity(
                        Intent.createChooser(
                                intent,
                                getResources().getString(R.string.share_via)
                        )
                );
                return true;
            case R.id.action_search:
                MenuItem mSearch = toolbar.getMenu().findItem(R.id.action_search);
                SearchView mSearchView = (SearchView) mSearch.getActionView();
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filter(newText);
                        return true;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filter(String text){
        ArrayList<String> temp = new ArrayList<String>();
        for(String d: CategoryViewModel.categoryStringList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        adapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_list_item_activated_1, temp);
        adapter.notifyDataSetChanged();
        mCategoriesView.setAdapter(adapter);
    }

//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        requireActivity().getMenuInflater().inflate(R.menu.category_list_item_delete,menu);
//    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        switch (item.getItemId()) {
//            case R.id.delete_category_menu_item:
//                deleteCategory(info.position);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    private void deleteSingleCategory() {
        mProgressBar.setVisibility(View.VISIBLE);
        SparseBooleanArray checkedItemPositions = mCategoriesView.getCheckedItemPositions();
        int itemCount = mCategoriesView.getCount();
        for(int i=itemCount-1; i >= 0; i--){
            if(checkedItemPositions.get(i)){
                CategoryViewModel.categoryStringList.remove(i);
                mCategoriesView.setItemChecked(i,false);
            }
        }
        checkedItemPositions.clear();
        adapter.notifyDataSetChanged();
        adapter1.notifyDataSetChanged();
        toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.list_main);
        toolbar.getMenu().removeItem(R.id.select_all_category_menu_item);
        toolbar.getMenu().removeItem(R.id.delete_category_menu_item);
        CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
        mCategoriesView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        mCategoriesView.setAdapter(adapter);
        if(!mCategoriesView.getAdapter().isEmpty()){
            fab1.show();
        }
        mProgressBar.setVisibility(View.GONE);
        showStatusMessage(getResources().getString(R.string.category_deleted));
    }

    private void deleteCategory() {
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.delete_category)
                .setMessage(R.string.delete_cat_dialog_msg)
                .setNeutralButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.delete_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSingleCategory();
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }

    private void showStatusMessage(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void prepareCategoryToCreate() {
        new MyDialogFragment().show(getParentFragmentManager(),"Add Category");
        adapter.notifyDataSetChanged();
        adapter1.notifyDataSetChanged();
    }

    private void prepareCategoryToEdit(final int id) {
        new MyDialogFragment().show(getParentFragmentManager(), String.valueOf(id));
    }

    private void prepareToSelectAll(@NonNull ListView lv){
        for ( int i=0; i < lv.getAdapter().getCount(); i++) {
            lv.setItemChecked(i, true);
        }
    }
    private void prepareToUnselectAll(@NonNull ListView lv){
        for ( int i=0; i < lv.getAdapter().getCount(); i++) {
            lv.setItemChecked(i, false);
        }
    }
    private void errMsg(){
        new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.delete_category)
                .setMessage(R.string.delete_cat_err_msg)
                .setIcon(R.drawable.ic_dialog_alert)
                .show();
    }


}


