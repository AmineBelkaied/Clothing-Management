package com.clothing.management.servicesImpl;

import com.clothing.management.entities.FbPage;
import com.clothing.management.repository.IFbPageRepository;
import com.clothing.management.services.FbPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FbPageServiceImpl implements FbPageService {

    @Autowired
    IFbPageRepository fbPageRepository;

    @Override
    public List<FbPage> findAllFbPages() {
        return fbPageRepository.findAll();
    }

    @Override
    public Optional<FbPage> findFbPageById(Long idFbPage) {
        return fbPageRepository.findById(idFbPage);
    }

    @Override
    public FbPage addFbPage(FbPage FbPage) {
        return fbPageRepository.save(FbPage);
    }

    @Override
    public FbPage updateFbPage(FbPage FbPage) {
        return fbPageRepository.save(FbPage);
    }

    @Override
    public void deleteFbPage(FbPage FbPage) {
        fbPageRepository.delete(FbPage);
    }

    @Override
    public void deleteFbPageById(Long idFbPage) {
        fbPageRepository.deleteById(idFbPage);
    }
}
