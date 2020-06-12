package com.example.rahulgupta.ui.MyExpense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rahulgupta.MyDialogFragment;
import com.example.rahulgupta.R;
import com.example.rahulgupta.adapters.CustomAdapter;
import com.example.rahulgupta.transactionDb.AppDatabase;
import com.example.rahulgupta.transactionDb.AppExecutors;
import com.example.rahulgupta.transactionDb.TransactionEntry;
import com.example.rahulgupta.transactionDb.TransactionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private FloatingActionButton fab1;
    private Toolbar toolbar;
    private static final String LOG_TAG = ExpenseFragment.class.getSimpleName();
    private RecyclerView rv;
    private List<TransactionEntry> transactionEntries;
    private CustomAdapter customAdapter;

    public TransactionViewModel transactionViewModel;

    private AppDatabase mAppDb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        expenseViewModel =
                ViewModelProviders.of(this).get(ExpenseViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_expense, container, false);
        FloatingActionButton fab=requireActivity().findViewById(R.id.fab);
        fab.hide();
        fab1=root.findViewById(R.id.fab1);
        fab1.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_add));
        fab1.show();
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss();
                openDialog();
//                Intent intent =new Intent(requireActivity(), AddExpenseActivity.class);
//                intent.putExtra("from", Constants.addExpenseString);
//                startActivity(intent);
            }
        });
        rv=root.findViewById(R.id.transactionRecyclerView);
        rv.setHasFixedSize(true);
        transactionEntries = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAppDb = AppDatabase.getInstance(getContext());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                // COMPLETED (1) Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();

                        List<TransactionEntry> transactionEntries = customAdapter.getTransactionEntries();
                        mAppDb.transactionDao().removeExpense(transactionEntries.get(position));

                    }
                });
                Snackbar.make(root,"Transaction Deleted",Snackbar.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(rv);

        setupViewModel();
        return root;
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
//            case R.id.action_sort_ascending:
//                Collections.sort();
//                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
//                customAdapter.notifyDataSetChanged();
//                return true;
//            case R.id.action_sort_descending:
//                Collections.reverse(CategoryViewModel.categoryStringList);
//                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
//                adapter.notifyDataSetChanged();
//                adapter1.notifyDataSetChanged();
//                return true;
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

    void filter(String text){
        List<TransactionEntry> temp = new ArrayList();
        for(TransactionEntry d: transactionEntries){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getCategory().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        customAdapter.updateList(temp);
    }

    public void setupViewModel(){
        transactionViewModel = ViewModelProviders.of(this)
                .get(TransactionViewModel.class);

        transactionViewModel.getExpenseList()
                .observe(getViewLifecycleOwner(), new Observer<List<TransactionEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TransactionEntry> transactionEntriesFromDb) {
                        Log.i(LOG_TAG,"Actively retrieving from DB");


                        transactionEntries = transactionEntriesFromDb;
//                        Logging to check DB values
                        for (int i =0 ; i < transactionEntries.size() ; i++){
                            String description = transactionEntries.get(i).getDescription();
                            int amount = transactionEntries.get(i).getAmount();
                            //Log.i("DESCRIPTION AMOUNT",description + String.valueOf(amount));
                        }

//                        Setting Adapter
                        customAdapter=new CustomAdapter(getActivity(),transactionEntries);
                        rv.setAdapter(customAdapter);
                    }
                });
    }

    private void openDialog(){
        new MyDialogFragment().show(getParentFragmentManager(),"Add");
    }
}