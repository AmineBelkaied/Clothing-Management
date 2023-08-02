package com.clothing.management.services;

import com.clothing.management.entities.FbPage;

import java.util.List;
import java.util.Optional;

public interface FbPageService {

    public List<FbPage> findAllFbPages();
    public Optional<FbPage> findFbPageById(Long idFbPage);
    public FbPage addFbPage(FbPage fbPage);
    public FbPage updateFbPage(FbPage fbPage);
    public void deleteFbPage(FbPage fbPage);
    public void deleteFbPageById(Long idFbPage);
}
