package com.macbitsgoa.bitsgridwatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DisclaimerDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.disclaimer);
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(true);
                ((MainActivity) Objects.requireNonNull(getActivity())).startBackgroundWork();
            }
        });
        builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainActivity) Objects.requireNonNull(getActivity())).setAllowMonitoring(false);
                ((MainActivity) Objects.requireNonNull(getActivity())).cancelBackgroundWork();
            }
        });
        return builder.create();
    }
}
