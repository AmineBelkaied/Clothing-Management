package com.clothing.management.repository;

import com.clothing.management.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INoteRepository extends JpaRepository<Note, Long> {
}
