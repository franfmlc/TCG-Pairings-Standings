package com.southernsoft.tcgtournament.pairings.dialog;

import androidx.fragment.app.DialogFragment;

public interface DialogListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogContinue(DialogFragment dialog);
}
