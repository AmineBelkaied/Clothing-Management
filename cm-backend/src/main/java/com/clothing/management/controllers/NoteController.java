package com.clothing.management.controllers;

import com.clothing.management.entities.Note;
import com.clothing.management.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/notes")
@CrossOrigin
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class NoteController {

    private final NoteService noteService;
    private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        LOGGER.info("Fetching all notes.");
        try {
            List<Note> notes = noteService.findAllNotes();
            LOGGER.info("Successfully fetched {} notes.", notes.size());
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            LOGGER.error("Error fetching notes: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/packet/{packetId}")
    public ResponseEntity<List<Note>> getNotesByPacketId(@PathVariable Long packetId) {
        LOGGER.info("Fetching notes for packet id: {}", packetId);
        try {
            List<Note> notes = noteService.findAllNotesByPacketId(packetId);
            LOGGER.info("Successfully fetched {} notes for packet id: {}", notes.size(), packetId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            LOGGER.error("Error fetching notes for packet id: {}: ", packetId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long noteId) {
        LOGGER.info("Fetching note with id: {}", noteId);
        return noteService.findNoteById(noteId)
                .map(note -> {
                    LOGGER.info("Successfully fetched note: {}", note);
                    return ResponseEntity.ok(note);
                })
                .orElseGet(() -> {
                    LOGGER.warn("Note with id: {} not found.", noteId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        LOGGER.info("Creating new note: {}", note);
        try {
            Note createdNote = noteService.addNote(note);
            LOGGER.info("Note created successfully: {}", createdNote);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
        } catch (Exception e) {
            LOGGER.error("Error creating note: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping
    public ResponseEntity<Note> updateNote(@RequestBody Note note) {
        LOGGER.info("Updating note: {}", note);
        try {
            Note updatedNote = noteService.updateNote(note);
            LOGGER.info("Note updated successfully: {}", updatedNote);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            LOGGER.error("Error updating note: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long noteId) {
        LOGGER.info("Deleting note with id: {}", noteId);
        try {
            noteService.deleteNoteById(noteId);
            LOGGER.info("Note with id: {} deleted successfully.", noteId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOGGER.error("Error deleting note with id: {}: ", noteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
