package com.clothing.management.servicesImpl;

import com.clothing.management.entities.FbPage;
import com.clothing.management.exceptions.custom.alreadyexists.FbPageAlreadyExistsException;
import com.clothing.management.repository.IFbPageRepository;
import com.clothing.management.services.FbPageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional("tenantTransactionManager")
public class FbPageServiceImpl implements FbPageService {

    private final IFbPageRepository fbPageRepository;

    public FbPageServiceImpl(IFbPageRepository fbPageRepository) {
        this.fbPageRepository = fbPageRepository;
    }

    @Override
    public List<FbPage> findAllFbPages() {
        return fbPageRepository.findAll();
    }

    @Override
    public Optional<FbPage> findFbPageById(Long idFbPage) {
        return fbPageRepository.findById(idFbPage);
    }

    @Override
    public Optional<FbPage> findFbPageById(String name) {
        return fbPageRepository.findByNameIsIgnoreCase(name);
    }

    @Override
    public FbPage addFbPage(FbPage fbPage) {
        if (fbPageRepository.findByNameIsIgnoreCase(fbPage.getName()).isPresent()) {

            throw new FbPageAlreadyExistsException(fbPage.getName());
        }
        return fbPageRepository.save(fbPage);
    }

    @Override
    public FbPage updateFbPage(FbPage fbPage) {
        return fbPageRepository.save(fbPage);
    }

    @Override
    public void deleteFbPage(FbPage fbPage) {
        fbPageRepository.delete(fbPage);
    }

    @Override
    public void deleteFbPageById(Long idFbPage) {
        fbPageRepository.deleteById(idFbPage);
    }
}
