package com.hathoute.bacplus;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogHelper {

    private final Context mContext;
    private final OnChoiceListener listener;
    private final int strId;

    public DialogHelper(Context context, int strId, OnChoiceListener l) {
        listener = l;
        mContext = context;
        this.strId = strId;
    }

    public void show() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_yesno);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        final Button bCancel = dialog.findViewById(R.id.bCancel);
        final Button bConfirm = dialog.findViewById(R.id.bYes);

        tvMessage.setText(strId);
        bCancel.setText(R.string.no);
        bConfirm.setText(R.string.yes);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChoice(false);
                dialog.dismiss();
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChoice(true);
                dialog.dismiss();
            }
        });
    }

    public OnChoiceListener mOnChoiceListener;

    public interface OnChoiceListener {

        void onChoice(boolean accepted);
    }
}
