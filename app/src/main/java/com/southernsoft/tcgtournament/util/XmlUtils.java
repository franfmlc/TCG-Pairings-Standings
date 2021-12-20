package com.southernsoft.tcgtournament.util;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.qualifiers.ApplicationContext;

public class XmlUtils {
    private final Context context;
    private final String TAG_PLAYER_OPPONENTS = "PlayerOpponents";
    private final String TAG_PLAYER = "player";
    private final String TAG_OPPONENT_ID = "opponentId";
    private final String TAG_ID = "id";
    private final String ATTR_ID = "id";
    private final String FILE_NAME = "tournament_%1$d_info.xml";
    private final String ENROLLED_PLAYERS_FILE_NAME = "enrolledPlayers.xml";

    @Inject
    public XmlUtils(@ApplicationContext Context context) {
        this.context = context;
    }

    public void serializeEnrolledPlayers(List<Player> enrolledPlayers) {
        try {
            File file = new File(context.getFilesDir(), ENROLLED_PLAYERS_FILE_NAME);
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, TAG_PLAYER);
            for (Player player : enrolledPlayers) {
                xmlSerializer.startTag(null, TAG_ID);
                xmlSerializer.text(String.valueOf(player.id));
                xmlSerializer.endTag(null, TAG_ID);
            }
            xmlSerializer.endTag(null, TAG_PLAYER);
            xmlSerializer.endDocument();
            xmlSerializer.flush();

            String dataWrite = writer.toString();
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serializeOpponents(int tournamentId, Map<Integer, Set<Integer>> opponentsByPlayerId) {
        try {
            String filename = String.format(FILE_NAME, tournamentId);
            File file = new File(context.getFilesDir(), filename);
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, TAG_PLAYER_OPPONENTS);

            for (Integer playerId : opponentsByPlayerId.keySet()) {
                xmlSerializer.startTag(null, TAG_PLAYER);
                xmlSerializer.attribute(null, ATTR_ID, String.valueOf(playerId));

                for (Integer opponentId : opponentsByPlayerId.get(playerId)) {
                    xmlSerializer.startTag(null, TAG_OPPONENT_ID);
                    xmlSerializer.text(String.valueOf(opponentId));
                    xmlSerializer.endTag(null, TAG_OPPONENT_ID);
                }
                xmlSerializer.endTag(null, TAG_PLAYER);
            }
            xmlSerializer.endTag(null, TAG_PLAYER_OPPONENTS);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> parseEnrolledPlayers() {
        try {
            File file = new File(context.getFilesDir(), ENROLLED_PLAYERS_FILE_NAME);
            FileInputStream fileInputStream = new FileInputStream(file);

            Set<Integer> enrolledPlayerIds = new HashSet<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(fileInputStream, "UTF-8");

            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.TEXT)
                    enrolledPlayerIds.add(Integer.parseInt(parser.getText()));
                eventType = parser.next();
            }
            return  enrolledPlayerIds;

        } catch (FileNotFoundException ex) {
            return new HashSet<>();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Set<Integer>> parseOpponents(int tournamentId) {
        try {
            String filename = String.format(FILE_NAME, tournamentId);
            File file = new File(context.getFilesDir(), filename);
            FileInputStream fileInputStream = new FileInputStream(file);

            Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(fileInputStream, "UTF-8");

            int playerId = 0;
            Set<Integer> opponentIds = null;
            int eventType = parser.next();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals(TAG_PLAYER)) {
                    opponentIds = new HashSet<>();
                    playerId = Integer.parseInt(parser.getAttributeValue(0));

                } else if (eventType == XmlPullParser.TEXT)
                    opponentIds.add(Integer.parseInt(parser.getText()));

                else if (eventType == XmlPullParser.END_TAG && parser.getName().equals(TAG_PLAYER))
                    opponentsByPlayerId.put(playerId, opponentIds);
                eventType = parser.next();
            }
            return opponentsByPlayerId;

        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEnrolledPlayers() {
        try {
            new File(context.getFilesDir(), ENROLLED_PLAYERS_FILE_NAME).delete();
        } catch (SecurityException ex) {
            Log.i(null, "Security error deleting file " + ex);
        }
    }

    public void deleteTournament(int tournamentId) {
        try {
            String filename = String.format(FILE_NAME, tournamentId);
            new File(context.getFilesDir(), filename).delete();
        } catch (SecurityException ex) {
            Log.i(null, "Security error deleting file " + ex);
        }
    }
}