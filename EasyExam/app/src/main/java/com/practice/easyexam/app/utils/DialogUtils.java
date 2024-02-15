package com.practice.easyexam.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.practice.easyexam.R;

public class DialogUtils {
    public static void showRenameDialog(Activity activity,
                                        final OnSetNameClickListener positiveBtnListener,
                                        final View.OnClickListener negativeBtnListener,
                                        String initialName
                                       ) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setCancelable(true);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_name_exam, null, false);
        EditText editText = view.findViewById(R.id.input_name);
        editText.setText(initialName);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        dialogBuilder.setView(view);
        AlertDialog alertDialog = dialogBuilder.create();
        if (negativeBtnListener != null) {
            TextView negativeBtn = view.findViewById(R.id.dialog_negative_btn);
            negativeBtn.setText("Cancel");
            negativeBtn.setOnClickListener(v -> {
                negativeBtnListener.onClick(v);
                alertDialog.cancel();
            });
        } else {
            view.findViewById(R.id.dialog_negative_btn).setVisibility(View.GONE);
        }
        if (positiveBtnListener != null) {
            TextView positiveBtn = view.findViewById(R.id.dialog_positive_btn);
            positiveBtn.setOnClickListener(v -> {
                positiveBtnListener.onClick(editText.getText().toString());
                alertDialog.dismiss();
            });
        } else {
            view.findViewById(R.id.dialog_positive_btn).setVisibility(View.GONE);
        }
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        alertDialog.show();
    }

    public static void showNotificationDialog(
            Context context,
            String title,
            String content,
            boolean isShowNegative,
            DialogInterface.OnClickListener positiveBtnListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton("Yes", positiveBtnListener)
                .setCancelable(false);

        // Add negative button if isShowNegative is true
        if (isShowNegative) {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle negative button click if needed
                }
            });
        }
        AlertDialog dialog = builder.create();

        // Set background drawable to the dialog
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_answer_default);

        dialog.show();
//        builder.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
//
//        builder.show();
    }

    public interface OnSetNameClickListener {
        void onClick(String name);
    }

}
