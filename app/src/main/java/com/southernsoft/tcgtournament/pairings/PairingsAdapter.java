package com.southernsoft.tcgtournament.pairings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.FragmentScoped;

import static com.southernsoft.tcgtournament.service.StandingService.BYE_PLAYER_ID;

@FragmentScoped
public class PairingsAdapter extends RecyclerView.Adapter<PairingsAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private static List<PairingTuple> pairingsList;
    private static List<Pairing> pairingsToUpdate;

    @Inject
    public PairingsAdapter(@ActivityContext Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void replaceData(List<PairingTuple> pairings) {
        pairingsList = pairings;
        pairingsToUpdate = new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Pairing> getPairingsToUpdate() {
        return pairingsToUpdate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View pairingRowView = layoutInflater.inflate(R.layout.pairing_row, parent, false);
        return new ViewHolder(pairingRowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PairingTuple pairingTuple = pairingsList.get(position);
        holder.p1Name.setText(pairingTuple.firstPlayerName);
        holder.p2Name.setText(pairingTuple.secondPlayerName);
        holder.p1Score.setText(String.valueOf(pairingTuple.pairing.firstPlayerResult));
        holder.p2Score.setText(String.valueOf(pairingTuple.pairing.secondPlayerResult));
    }

    @Override
    public int getItemCount() {
        return pairingsList == null ? 0 : pairingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView p1Name;
        private final TextView p2Name;
        private final Button p1Score;
        private final Button p2Score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            p1Name = itemView.findViewById(R.id.p1_name);
            p2Name = itemView.findViewById(R.id.p2_name);
            p1Score = itemView.findViewById(R.id.p1_score);
            p2Score = itemView.findViewById(R.id.p2_score);

            p1Score.setOnClickListener(new scoreButtonListener());
            p2Score.setOnClickListener(new scoreButtonListener());
        }

        private class scoreButtonListener implements View.OnClickListener {
            @Override
            public void onClick(View view) {
                int pairingPosition = getBindingAdapterPosition();
                int pairingIndex;

                PairingTuple tuple = pairingsList.get(pairingPosition);

                if (tuple.pairing.secondPlayerId != BYE_PLAYER_ID) {
                    Button scoreButton = (Button) view;
                    int currentValue = Integer.parseInt(scoreButton.getText().toString());
                    int nextValue;

                    if (currentValue == 2)
                        nextValue = 0;
                    else
                        nextValue = ++currentValue;

                    if (scoreButton.getId() == R.id.p1_score) {
                        if (tuple.pairing.secondPlayerResult == 2 && currentValue == 2) {
                            tuple.pairing.firstPlayerResult = 0;
                            tuple.pairing.secondPlayerResult = 0;
                        } else
                            tuple.pairing.firstPlayerResult = nextValue;
                    } else {
                        if (tuple.pairing.firstPlayerResult == 2 && currentValue == 2) {
                            tuple.pairing.firstPlayerResult = 0;
                            tuple.pairing.secondPlayerResult = 0;
                        } else
                            tuple.pairing.secondPlayerResult = nextValue;
                    }

                    notifyItemChanged(pairingPosition);

                    /*
                    * The following block is used to register those pairings
                    * results updated by the user. Only these will be
                    * saved to database in case of pausing the activity.
                    */
                    pairingIndex = pairingsToUpdate.indexOf(tuple.pairing);
                    if (pairingIndex >= 0)
                        pairingsToUpdate.set(pairingIndex, tuple.pairing);
                    else
                        pairingsToUpdate.add(tuple.pairing);
                }
            }
        }
    }
}
