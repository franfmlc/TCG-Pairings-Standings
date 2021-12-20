package com.southernsoft.tcgtournament;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.southernsoft.tcgtournament.dao.PairingDao;
import com.southernsoft.tcgtournament.dao.PlayerDao;
import com.southernsoft.tcgtournament.dao.RoundDao;
import com.southernsoft.tcgtournament.dao.StandingDao;
import com.southernsoft.tcgtournament.dao.TournamentDao;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;

@Database(entities = {Player.class, Tournament.class, Round.class, Pairing.class, Standing.class}, exportSchema = false, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract PlayerDao playerDao();
    public abstract TournamentDao tournamentDao();
    public abstract RoundDao roundDao();
    public abstract PairingDao pairingDao();
    public abstract StandingDao standingDao();
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    private static final String databaseName = "local_database";
    private static volatile LocalDatabase INSTANCE;

    public static LocalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabase.class, databaseName)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                PlayerDao dao = INSTANCE.playerDao();

                ArrayList<Player> players = new ArrayList<>();

                Player playerOne = new Player();
                playerOne.playerName = "Bye";
                players.add(playerOne);

                dao.insert(players);
            });
        }
    };
}