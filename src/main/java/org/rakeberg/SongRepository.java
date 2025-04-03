package org.rakeberg;

import org.rakeberg.entity.Song;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongRepository {

  private final String songDBUrl;

  public SongRepository(String songDBUrl) {
    this.songDBUrl = songDBUrl;
  }

  public List<Song> getAllSongs() {
    try (Connection c = getConnection(); Statement stmt = c.createStatement()){
      Class.forName("org.sqlite.JDBC");
      c.setAutoCommit(false);
      System.out.println("Opened database successfully for " + songDBUrl);

      return selectAllSongs(stmt);

    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
      return List.of();
    }
  }

  private static List<Song> selectAllSongs(Statement stmt) throws SQLException {
    try (ResultSet rs = stmt.executeQuery("SELECT * FROM song")) {

      List<Song> songs = new ArrayList<>();
      while (rs.next()) {
        int rowId = rs.getInt("rowid");
        String songItemUID = rs.getString("song_item_uid");
        String title = cleanupTitle(rs.getString("title"));

        Song song = new Song(rowId, songItemUID, title);

        if (title.isBlank()) {
          System.out.println("Skipping " + songItemUID + " since title is blank");
        } else {
          songs.add(song);
        }
      }
      System.out.println("Found " + songs.size() + " songs");
      return songs;
    }
  }

  /**Cleans up characters from the title that is not compatible with filenames,
   * since it will be used as the filename when writing to disk later on
   * @param title The string to clean up
   * @return cleaned up title
   */
  private static String cleanupTitle(String title) {
    return title.replace("/", "-")
        .replace("?", "")
        .replace(":", "-");
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:sqlite:" + songDBUrl);
  }
}
