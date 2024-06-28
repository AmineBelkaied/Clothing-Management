package com.clothing.management.services;

import com.clothing.management.entities.Note;

import java.util.List;
import java.util.Optional;

public interface NoteService {
    List<Note> findAllNotes();
    Optional<Note> findNoteById(Long noteId);
    Note addNote(Note note);
    Note updateNote(Note note);
    void deleteNoteById(Long noteId);
}
