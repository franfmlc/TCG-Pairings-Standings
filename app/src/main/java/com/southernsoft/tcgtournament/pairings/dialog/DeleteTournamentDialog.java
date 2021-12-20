package com.southernsoft.tcgtournament.pairings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.southernsoft.tcgtournament.R;

public class DeleteTournamentDialog extends DialogFragment {
    DeleteTournamentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DeleteTournamentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DeleteTournamentListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tournament_finished_title)
                .setMessage(R.string.remove_tournament_msg)
                .setNegativeButton(R.string.keep, (dialog, which) -> {
                    listener.onDialogKeepTournament();
                })
                .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                    listener.onDialogDeleteTournament();
                });
        return builder.create();
    }
}