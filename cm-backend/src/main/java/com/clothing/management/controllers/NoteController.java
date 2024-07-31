package com.clothing.management.controllers;

import com.clothing.management.entities.Note;
import com.clothing.management.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.findAllNotes();
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/packet/{packetId}")
    public ResponseEntity<List<Note>> getNotesByPacketId(@PathVariable Long packetId) {
        List<Note> notes = noteService.findAllNotesByPacketId(packetId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long noteId) {
        return noteService.findNoteById(noteId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note createdNote = noteService.addNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @PutMapping
    public ResponseEntity<Note> updateNote(@RequestBody Note note) {
        Note updatedNote = noteService.updateNote(note);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long noteId) {
        noteService.deleteNoteById(noteId);
        return ResponseEntity.noContent().build();
    }
}
