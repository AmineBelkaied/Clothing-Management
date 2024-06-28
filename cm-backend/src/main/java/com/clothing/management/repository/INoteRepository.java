package com.clothing.management.repository;

import com.clothing.management.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface INoteRepository extends JpaRepository<Note, Long> {

    @Query("select n from Note n where n.packet.id = :packetId")
    List<Note> findNotesByPacketId(@Param("packetId") Long packetId);
}
