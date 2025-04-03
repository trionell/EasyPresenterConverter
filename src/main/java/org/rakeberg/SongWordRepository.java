package org.rakeberg;

import org.rakeberg.entity.FullSong;
import org.rakeberg.entity.Song;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongWordRepository {

  private final String songWordsDBUrl;

  public SongWordRepository(String songWordsDBUrl) {
    this.songWordsDBUrl = songWordsDBUrl;
  }

  public List<FullSong> getWordsForSongs(List<Song> songs) {
    try (Connection c = getConnection(); Statement stmt = c.createStatement()){
      Class.forName("org.sqlite.JDBC");
      c.setAutoCommit(false);
      System.out.println("Opened database successfully for " + songWordsDBUrl);

      return getWordsForSongs(stmt, songs);

    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return List.of();
    }
  }

  private static List<FullSong> getWordsForSongs(Statement stmt, List<Song> songs) throws SQLException {
    List<FullSong> list = new ArrayList<>();
    for (Song song : songs) {
      FullSong wordsForSong = getWordsForSong(stmt, song);
      list.add(wordsForSong);
    }
    return list;
  }

  private static FullSong getWordsForSong(Statement stmt, Song song) throws SQLException {
    try (ResultSet rs = stmt.executeQuery("SELECT words FROM word WHERE song_id = " + song.rowId() + " LIMIT 1")) {

      FullSong fullSong = null;
      while (rs.next()) {
        String rtfWords = rs.getString("words");

        if (fullSong != null) {
          throw new IllegalStateException("More than one row in result for song: " + song.rowId());
        }
        fullSong = new FullSong(song.rowId(), song.songItemUID(), song.title(), rtfWords);
      }
      return fullSong;
    }
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:" + songWordsDBUrl);
  }
}
