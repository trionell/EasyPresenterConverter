package org.rakeberg.entity;

public record FullSong(
    int rowId,
    String songItemUID,
    String title,
    String lyrics
) {
}
