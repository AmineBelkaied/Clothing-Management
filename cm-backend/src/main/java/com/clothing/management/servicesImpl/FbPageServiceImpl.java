package com.clothing.management.servicesImpl;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.FbPage;
import com.clothing.management.exceptions.custom.alreadyexists.FbPageAlreadyExistsException;
import com.clothing.management.repository.IFbPageRepository;
import com.clothing.management.repository.IPacketRepository;
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
    private final IPacketRepository packetRepository;

    public FbPageServiceImpl(IFbPageRepository fbPageRepository, IPacketRepository packetRepository) {
        this.fbPageRepository = fbPageRepository;
        this.packetRepository = packetRepository;
    }

    @Override
    public List<FbPage> findAllFbPages() {
        List<FbPage> fbPages = fbPageRepository.findAll();
        LOGGER.info("Found {} Facebook pages.", fbPages.size());
        return fbPages;
    }

    @Override
    public Optional<FbPage> findFbPageById(Long idFbPage) {
        Optional<FbPage> fbPage = fbPageRepository.findById(idFbPage);
        fbPage.ifPresentOrElse(
                page -> LOGGER.info("Found Facebook page: {}", page.getName()),
                () -> LOGGER.warn("Facebook page with ID: {} not found.", idFbPage)
        );
        return fbPage;
    }

    @Override
    public Optional<FbPage> findFbPageById(String name) {
        Optional<FbPage> fbPage = fbPageRepository.findByNameIsIgnoreCase(name);
        fbPage.ifPresentOrElse(
                page -> LOGGER.info("Found Facebook page: {}", page.getName()),
                () -> LOGGER.warn("Facebook page with name: {} not found.", name)
        );
        return fbPage;
    }

    @Override
    public FbPage saveFbPage(FbPage fbPage) {
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
        fbPageRepository.delete(fbPage);
        LOGGER.info("Facebook page deleted: {}", fbPage.getName());
    }

    @Override
    public Long checkFbPageUsage(Long id) {
        return packetRepository.countPacketByFbPage_Id(id);
    }

    @Override
    public void deleteFbPageById(Long idFbPage) {
        fbPageRepository.deleteById(idFbPage);
        LOGGER.info("Facebook page with ID: {} deleted successfully.", idFbPage);
    }
}
