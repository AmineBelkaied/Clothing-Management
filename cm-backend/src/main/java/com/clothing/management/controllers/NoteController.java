package com.clothing.management.controllers;

import com.clothing.management.entities.Note;
import com.clothing.management.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("note")
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public class NoteController {
    
    @Autowired
    NoteService noteService;

    @GetMapping(path = "/findAllNotes")
    public List<Note> findAllNotes() {
        return noteService.findAllNotes();
    }

    @GetMapping(path = "/findAllNotesByPacketId/{packetId}")
    public List<Note> findAllNotesByPacketId(@PathVariable Long packetId) {
        return noteService.findAllNotesByPacketId(packetId);
    }

    @GetMapping(path = "/findNoteById/{noteId}")
    public Optional<Note> findNoteById(@PathVariable Long noteId) {
        return noteService.findNoteById(noteId);
    }

    @PostMapping(value = "/addNote" , produces = "application/json")
    public Note addNote(@RequestBody  Note Note) {
        return noteService.addNote(Note);
    }

    @PutMapping(value = "/updateNote" , produces = "application/json")
    public Note updateNote(@RequestBody Note Note) {
        return noteService.updateNote(Note);
    }

    @DeleteMapping(value = "/deleteNoteById/{noteId}")
    public void deleteNoteById(@PathVariable Long noteId) {
        noteService.deleteNoteById(noteId);
    }
}
