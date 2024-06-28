package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Note;
import com.clothing.management.repository.INoteRepository;
import com.clothing.management.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    INoteRepository noteRepository;
    
    @Override
    public List<Note> findAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public List<Note> findAllNotesByPacketId(Long packetId) {
        return noteRepository.findNotesByPacketId(packetId);
    }

    @Override
    public Optional<Note> findNoteById(Long idNote) {
        return noteRepository.findById(idNote);
    }

    @Override
    public Note addNote(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public Note updateNote(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public void deleteNoteById(Long noteId) {
        noteRepository.deleteById(noteId);
    }
}
