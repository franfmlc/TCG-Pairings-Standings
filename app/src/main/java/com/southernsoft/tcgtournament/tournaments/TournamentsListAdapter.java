package com.southernsoft.tcgtournament.tournaments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Locale;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;

import com.southernsoft.tcgtournament.pojo.TournamentAndRound;
import com.southernsoft.tcgtournament.util.RoundTimeUtils;
import dagger.hilt.android.qualifiers.ActivityContext;

public class TournamentsListAdapter extends ListAdapter<TournamentAndRound, TournamentsListAdapter.ViewHolder> {
    private final TournamentClickCallback tournamentClickCallback;
    private final LayoutInflater layoutInflater;
    private DateFormat dateFormat;
    private Locale currentLocale;

    @Inject
    public TournamentsListAdapter(@ActivityContext Context context, TournamentClickCallback clickCallback) {
        super(TournamentAndRound.DIFF_CALLBACK);
        tournamentClickCallback = clickCallback;
        layoutInflater = LayoutInflater.from(context);
        currentLocale = Locale.getDefault();
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tournamentView = layoutInflater.inflate(R.layout.tournament_row, parent, false);
        return new ViewHolder(tournamentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TournamentAndRound item = getItem(position);
        holder.date.setText(dateFormat.format(item.date));
        holder.players.setText(String.valueOf(item.numberPlayers));
        holder.time.setText(String.valueOf(RoundTimeUtils.formatElapsedTime(item.remainingTime)));

        if (item.maxRound <= item.numberRounds)
            holder.round.setText(item.maxRound + "/" + item.numberRounds);
        else
            holder.round.setText(--item.maxRound + "/" + item.numberRounds);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView players;
        private final TextView round;
        private final TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            players = itemView.findViewById(R.id.players);
            round = itemView.findViewById(R.id.round);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(button -> {
                final Context context = itemView.getContext();
                PopupMenu popup = new PopupMenu(context, round);
                popup.setOnMenuItemClickListener(item -> {
                    int position = getBindingAdapterPosition();
                    if (item.getItemId() == R.id.resume_tournament) {
                        TournamentAndRound info = getItem(position);
                        tournamentClickCallback.onResume(info.tournamentId, info.roundId);
                    } else {
                        String msg = context.getString(R.string.delete_tournament_msg, date.getText(), players.getText());
                        new AlertDialog.Builder(context)
                            .setTitle(R.string.delete_tournament_title)
                            .setMessage(HtmlCompat.fromHtml(msg, HtmlCompat.FROM_HTML_MODE_COMPACT))
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, (dialog, which) -> tournamentClickCallback.onDelete(getItem(position).tournamentId))
                            .show();
                    }
                    return true;
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.tournament_actions, popup.getMenu());
                popup.show();
            });
        }
    }
}
