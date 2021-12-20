package com.southernsoft.tcgtournament.pairings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.southernsoft.tcgtournament.R;

public class NoPlayersMatchedDialog extends DialogFragment {
    DialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DialogListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.no_players_matched_title)
                .setMessage(R.string.no_players_matched_msg)
                .setPositiveButton(R.string.continues, (dialogInterface, i) -> {
                    listener.onDialogContinue(this);
                });
        return builder.create();
    }
}