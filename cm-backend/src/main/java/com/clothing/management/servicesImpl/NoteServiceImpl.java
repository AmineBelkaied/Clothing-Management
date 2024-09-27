package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Note;
import com.clothing.management.repository.INoteRepository;
import com.clothing.management.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final INoteRepository noteRepository;

    public NoteServiceImpl(INoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public List<Note> findAllNotes() {
        List<Note> notes = noteRepository.findAll();
        LOGGER.info("Retrieved {} notes.", notes.size());
        return notes;
    }

    @Override
    public List<Note> findAllNotesByPacketId(Long packetId) {
        List<Note> notes = noteRepository.findNotesByPacketId(packetId);
        LOGGER.info("Retrieved {} notes for packetId: {}", notes.size(), packetId);
        return notes;
    }

    @Override
    public Optional<Note> findNoteById(Long idNote) {
        Optional<Note> note = noteRepository.findById(idNote);
        if (note.isPresent()) {
            LOGGER.info("Note with id: {} found.", idNote);
        } else {
            LOGGER.warn("Note with id: {} not found.", idNote);
        }
        return note;
    }

    @Override
    public Note addNote(Note note) {
        Note savedNote = noteRepository.save(note);
        LOGGER.info("Note added with id: {}", savedNote.getId());
        return savedNote;
    }

    @Override
    public Note updateNote(Note note) {
        Note updatedNote = noteRepository.save(note);
        LOGGER.info("Note updated with id: {}", updatedNote.getId());
        return updatedNote;
    }

    @Override
    public void deleteNoteById(Long noteId) {
        try {
            noteRepository.deleteById(noteId);
            LOGGER.info("Note with id: {} deleted successfully.", noteId);
        } catch (Exception e) {
            LOGGER.error("Error deleting note with id: {}: {}", noteId, e.getMessage());
            throw e; // Re-throwing exception after logging
        }
    }
}
