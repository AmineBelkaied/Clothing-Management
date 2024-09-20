package com.clothing.management.servicesImpl;

import com.clothing.management.entities.FbPage;
import com.clothing.management.exceptions.custom.alreadyexists.FbPageAlreadyExistsException;
import com.clothing.management.repository.IFbPageRepository;
import com.clothing.management.services.FbPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional("tenantTransactionManager")
public class FbPageServiceImpl implements FbPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FbPageServiceImpl.class);

    private final IFbPageRepository fbPageRepository;

    public FbPageServiceImpl(IFbPageRepository fbPageRepository) {
        this.fbPageRepository = fbPageRepository;
    }

    @Override
    public List<FbPage> findAllFbPages() {
        LOGGER.info("Retrieving all Facebook pages.");
        List<FbPage> fbPages = fbPageRepository.findAll();
        LOGGER.info("Found {} Facebook pages.", fbPages.size());
        return fbPages;
    }

    @Override
    public Optional<FbPage> findFbPageById(Long idFbPage) {
        LOGGER.info("Retrieving Facebook page by ID: {}", idFbPage);
        Optional<FbPage> fbPage = fbPageRepository.findById(idFbPage);
        fbPage.ifPresentOrElse(
                page -> LOGGER.info("Found Facebook page: {}", page.getName()),
                () -> LOGGER.warn("Facebook page with ID: {} not found.", idFbPage)
        );
        return fbPage;
    }

    @Override
    public Optional<FbPage> findFbPageById(String name) {
        LOGGER.info("Retrieving Facebook page by name: {}", name);
        Optional<FbPage> fbPage = fbPageRepository.findByNameIsIgnoreCase(name);
        fbPage.ifPresentOrElse(
                page -> LOGGER.info("Found Facebook page: {}", page.getName()),
                () -> LOGGER.warn("Facebook page with name: {} not found.", name)
        );
        return fbPage;
    }

    @Override
    public FbPage saveFbPage(FbPage fbPage) {
        LOGGER.info("Saving Facebook page: {}", fbPage.getName());
        if (fbPageRepository.findByNameIsIgnoreCase(fbPage.getName()).isPresent()) {
            LOGGER.error("Facebook page with name: {} already exists.", fbPage.getName());
            throw new FbPageAlreadyExistsException(fbPage.getName());
        }
        FbPage savedPage = fbPageRepository.save(fbPage);
        LOGGER.info("Facebook page saved successfully with ID: {}", savedPage.getId());
        return savedPage;
    }

    @Override
    public void deleteFbPage(FbPage fbPage) {
        LOGGER.info("Deleting Facebook page: {}", fbPage.getName());
        fbPageRepository.delete(fbPage);
        LOGGER.info("Facebook page deleted: {}", fbPage.getName());
    }

    @Override
    public void deleteFbPageById(Long idFbPage) {
        LOGGER.info("Deleting Facebook page by ID: {}", idFbPage);
        fbPageRepository.deleteById(idFbPage);
        LOGGER.info("Facebook page with ID: {} deleted successfully.", idFbPage);
    }
}
