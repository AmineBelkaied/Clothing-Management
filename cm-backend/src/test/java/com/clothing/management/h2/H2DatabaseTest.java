package com.clothing.management.h2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class H2DatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testH2DatabaseIsRunning() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result);
        assertEquals(1, result);
    }
}
