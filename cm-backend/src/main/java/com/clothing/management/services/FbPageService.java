package com.clothing.management.services;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.FbPage;

import java.util.List;
import java.util.Optional;

public interface FbPageService {

    List<FbPage> findAllFbPages();
    Optional<FbPage> findFbPageById(Long idFbPage);

    Optional<FbPage> findFbPageById(String name);

    FbPage saveFbPage(FbPage fbPage);
    void deleteFbPage(FbPage fbPage);
    void deleteFbPageById(Long idFbPage);
    Long checkFbPageUsage(Long id);
}
