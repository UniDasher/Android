package com.uni.unidasher.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.utils.HandlerHelper;


/**
 * This class contains methods for creating DialogFragment to show notification with information
 * about changed API URL at the top of application
 */

public class TopNotificationDialogFragment extends DialogFragment {
    private static final String BUNDLE_KEY_MESSAGE = "message";
    private static final String BUNDLE_KEY_TIME = "time";

    public TopNotificationDialogFragment() {}


    /**
     * Method used for creating new instance of this dialog and passing message for notification
     * and time for how long notification should be showed
     * @param message message for notification
     * @param time time of how long notification should be showed (in milliseconds)
     */
    public static TopNotificationDialogFragment newInstance(String message, long time) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_MESSAGE, message);
        args.putLong(BUNDLE_KEY_TIME, time);
        TopNotificationDialogFragment fragment = new TopNotificationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates new Dialog with message and time from {@link #newInstance(String, long)}
     * @param savedInstanceState savedInstanceState
     * @return dialog
     */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        final TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dialog_top_notification, null);

        textView.setText(getArguments().getString(BUNDLE_KEY_MESSAGE));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().setWindowAnimations(R.style.dialog_animations_slide);
        dialog.setContentView(textView);
        dialog.setCanceledOnTouchOutside(true);
        final long showTime = getArguments().getLong(BUNDLE_KEY_TIME);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(showTime);
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            if (getDialog() != null && getDialog().isShowing()) {
                                getDialog().dismiss();
                            }
                        }
                    });
                } catch (Exception e) {
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            if (getDialog() != null && getDialog().isShowing()) {
                                getDialog().dismiss();
                            }
                        }
                    });
                }
            }
        }).start();

        return dialog;
    }
}
