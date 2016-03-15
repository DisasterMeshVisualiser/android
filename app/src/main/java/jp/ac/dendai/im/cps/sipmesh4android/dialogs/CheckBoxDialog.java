package jp.ac.dendai.im.cps.sipmesh4android.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import jp.ac.dendai.im.cps.sipmesh4android.utils.SharedPreferencesUtil;

public class CheckBoxDialog extends DialogFragment {
    private static final String TAG = CheckBoxDialog.class.getSimpleName();
    private static final String PARAM_DATA = "data_array";
    private OnButtonClickListener mListener;

    public static CheckBoxDialog newInstance(int[] dataArray) {
        CheckBoxDialog fragment = new CheckBoxDialog();
        Bundle args = new Bundle();
        args.putIntArray(PARAM_DATA, dataArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        final int[] dataArray = args.getIntArray(PARAM_DATA);
        final String[] nameArray;
        final boolean[] boolArray;

        nameArray = new String[dataArray.length];
        boolArray = new boolean[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            nameArray[i] = SharedPreferencesUtil.getName(dataArray[i], getContext());
            boolArray[i] = SharedPreferencesUtil.getBool(dataArray[i], getContext());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("select")
                .setMultiChoiceItems(nameArray, boolArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        boolArray[which] = isChecked;
//                        if (isChecked)
//                            selectedItems.add(dataArray[which]);
//                        else if (selectedItems.contains(which)) {
//                            selectedItems.remove(which);
//                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < dataArray.length; i++) {
                            Log.d(TAG, "onClick" + i + ": dataArray" + dataArray[i] + ": boolArray :" + boolArray[i]);
                            SharedPreferencesUtil.putBool(dataArray[i], boolArray[i], getContext());
                        }
                        mListener.onPositiveClick(dataArray, boolArray);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onNegativeClick();
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.mListener = (OnButtonClickListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public interface OnButtonClickListener {
        void onPositiveClick(int[] dataArray, boolean[] boolArray);
        void onNegativeClick();
    }
}
