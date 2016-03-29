package jp.ac.dendai.im.cps.sipmesh4android.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by naoya on 16/03/15.
 */
public class SpinningProgressDialog extends DialogFragment {
    private static final String TAG = SpinningProgressDialog.class.getSimpleName();
    private ProgressDialog progressDialog;
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_MESSAGE = "message";

    public static SpinningProgressDialog newInstance(String title, String message) {
        SpinningProgressDialog fragment = new SpinningProgressDialog();
        Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putString(PARAM_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(PARAM_TITLE);
        String message = getArguments().getString(PARAM_MESSAGE);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        return progressDialog;
    }
}
