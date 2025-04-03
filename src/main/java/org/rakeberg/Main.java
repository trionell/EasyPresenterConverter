package org.rakeberg;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.rakeberg.entity.FullSong;
import org.rakeberg.entity.Song;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {
    SongRepository songRepository = new SongRepository("Songs.db");
    SongWordRepository wordRepository = new SongWordRepository("SongWords.db");

    List<Song> songs = songRepository.getAllSongs();
    List<FullSong> fullSongs = wordRepository.getWordsForSongs(songs);

    String targetOutputLocation = System.getProperty("user.home") + File.separator + "converterSongs";
    for (FullSong song : fullSongs) {
      FullSong convertedSong = convertDecodeRTFLyrics(song);
      if (convertedSong.lyrics().isBlank()) {
        System.out.println("Skipping writing " + song.title() + " due to lyrics being blank");
      } else {
        SongFileWriter.writeSongToFile(convertedSong, targetOutputLocation);
      }
    }

    System.out.println("Done");
  }

  private static FullSong convertDecodeRTFLyrics(FullSong song) {
    return new FullSong(song.rowId(), song.songItemUID(), song.title(), extractTextFromRTF(song.lyrics()));
  }

  public static String extractTextFromRTF(String rtfContent) {
    try (InputStream inputStream = new ByteArrayInputStream(rtfContent.getBytes())) {
      ContentHandler handler = new BodyContentHandler();
      Metadata metadata = new Metadata();
      AutoDetectParser parser = new AutoDetectParser();
      parser.parse(inputStream, handler, metadata);
      return handler.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}