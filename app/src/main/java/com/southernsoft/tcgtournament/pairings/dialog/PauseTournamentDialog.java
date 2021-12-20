package com.southernsoft.tcgtournament.pairings.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.southernsoft.tcgtournament.MainActivity;
import com.southernsoft.tcgtournament.R;

public class PauseTournamentDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.pause_tournament_title)
                .setMessage(R.string.pause_tournament_msg)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.continues, (dialogInterface, i) -> {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    activity.finish();
                });
        return builder.create();
    }
}
