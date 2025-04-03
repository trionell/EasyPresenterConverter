package org.rakeberg;

import org.rakeberg.entity.FullSong;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SongFileWriter {

  public static void writeSongToFile(FullSong song, String outputLocation) throws IOException {
    Files.createDirectories(Paths.get(outputLocation));

    File file = new File(outputLocation + File.separator + song.title() + ".txt");

    System.out.println("Attempting to write to " + file.getAbsolutePath());

    if (!file.createNewFile() && !file.canWrite()) {
      throw new IllegalStateException("Cannot write to file " + file.getAbsolutePath());
    }

    try (FileWriter fw = new FileWriter(file)) {
      fw.write(song.lyrics());
    }
  }
}
