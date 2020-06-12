package com.example.rahulgupta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.rahulgupta.ui.Category.CategoryViewModel;
import com.example.rahulgupta.utils.Constants;

public class MyDialogFragment extends DialogFragment {

//    public interface YesNoListener {
//        void onYes();
//
//        void onNo();
//    }

    @Override
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
//        if (!(activity instanceof YesNoListener)) {
//            throw new ClassCastException(activity.toString() + " must implement YesNoListener");
//        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getTag().equals("Add Category")) {

            final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        alert.setTitle(R.string.add_category); //Set Alert dialog title here
        alert.setMessage(R.string.category_name_string); //Message here
        alert.setCancelable(true);
        // Set an EditText view to get user input
        final EditText input = new EditText(requireActivity());
        alert.setView(input);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        //End of alert.setPositiveButton

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = input.getText().toString();
                if(s.length()==0) {
                    input.setError(getResources().getString(R.string.error_empty_field));
                    return;
                }
                CategoryViewModel.categoryStringList.add(s);
                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
//                adapter.notifyDataSetChanged();
//                adapter1.notifyDataSetChanged();
                showStatusMessage(getResources().getString(R.string.category_added));
//                if (mCategoriesView.getAdapter() != null) {
//                    fab1.show();
//                }
                alertDialog.dismiss();
            }
        });
        return alertDialog;
        }

        else if(getTag().equals("Add")){
            final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
            alert.setTitle(R.string.add); //Set Alert dialog title here
            //alert.setMessage(R.string.category_name_string); //Message here
            alert.setCancelable(true);
            alert.setPositiveButton("Expense", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent =new Intent(requireActivity(), AddExpenseActivity.class);
                    intent.putExtra("from", Constants.addExpenseString);
                    startActivity(intent);
                }
            });
            //End of alert.setPositiveButton

            alert.setNeutralButton("Income", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    //dialog.cancel();
                    Intent intent =new Intent(requireActivity(), AddExpenseActivity.class);
                    intent.putExtra("from", Constants.addIncomeString);
                    startActivity(intent);
                }
            }); //End of alert.setNegativeButton
            final AlertDialog alertDialog = alert.create();
            alertDialog.show();
            return alertDialog;
        }

        else {
            final AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
        alert.setTitle(R.string.edit_category); //Set Alert dialog title here
        alert.setMessage(R.string.category_name_string); //Message here
        alert.setCancelable(true);
        // Set an EditText view to get user input
        final EditText input = new EditText(requireActivity());
        alert.setView(input);
        input.setText(CategoryViewModel.categoryStringList.get(Integer.parseInt(getTag())));

        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        //End of alert.setPositiveButton

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        final AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = input.getText().toString();
                if(s.length()==0) {
                    input.setError(getResources().getString(R.string.error_empty_field));
                    return;
                }
                CategoryViewModel.categoryStringList.set(Integer.parseInt(getTag()), s );
                CategoryViewModel.categoryList.setValue(CategoryViewModel.categoryStringList);
                showStatusMessage(getResources().getString(R.string.category_updated));

                alertDialog.dismiss();

            }
        });
        return alertDialog;
        }
    }

    private void showStatusMessage(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
