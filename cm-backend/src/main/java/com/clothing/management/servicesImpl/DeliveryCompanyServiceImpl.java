package com.clothing.management.servicesImpl;

import com.clothing.management.entities.DeliveryCompany;
import com.clothing.management.repository.IDeliveryCompanyRepository;
import com.clothing.management.repository.IPacketRepository;
import com.clothing.management.services.DeliveryCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeliveryCompanyServiceImpl implements DeliveryCompanyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryCompanyServiceImpl.class);

    private final IDeliveryCompanyRepository deliveryCompanyRepository;
    private final IPacketRepository packetRepository;

    @Autowired
    public DeliveryCompanyServiceImpl(IDeliveryCompanyRepository deliveryCompanyRepository, IPacketRepository packetRepository) {
        this.deliveryCompanyRepository = deliveryCompanyRepository;
        this.packetRepository = packetRepository;
    }

    @Override
    public List<DeliveryCompany> findAllDeliveryCompanies() {
        List<DeliveryCompany> deliveryCompanies = deliveryCompanyRepository.findAll();
        LOGGER.info("Found {} delivery companies.", deliveryCompanies.size());
        return deliveryCompanies;
    }

    @Override
    public Optional<DeliveryCompany> findDeliveryCompanyById(Long id) {
        Optional<DeliveryCompany> deliveryCompany = deliveryCompanyRepository.findById(id);
        deliveryCompany.ifPresentOrElse(
                company -> LOGGER.info("Found delivery company: {}", company.getName()),
                () -> LOGGER.warn("Delivery company with ID: {} not found.", id)
        );
        return deliveryCompany;
    }

    @Override
    public DeliveryCompany addDeliveryCompany(DeliveryCompany deliveryCompany) {
        DeliveryCompany savedCompany = deliveryCompanyRepository.save(deliveryCompany);
        LOGGER.info("Delivery company added successfully with ID: {}", savedCompany.getId());
        return savedCompany;
    }

    @Override
    public DeliveryCompany updateDeliveryCompany(DeliveryCompany deliveryCompany) {
        DeliveryCompany updatedCompany = deliveryCompanyRepository.save(deliveryCompany);
        LOGGER.info("Delivery company updated successfully with ID: {}", updatedCompany.getId());
        return updatedCompany;
    }

    @Override
    public Long checkDeliveryCompanyUsage(Long id) {
        return packetRepository.countPacketByDeliveryCompany_Id(id);
    }

    @Override
    public void deleteDeliveryCompany(DeliveryCompany deliveryCompany) {
        deliveryCompanyRepository.delete(deliveryCompany);
        LOGGER.info("Delivery company deleted: {}", deliveryCompany.getName());
    }

    @Override
    public void deleteDeliveryCompanyById(Long id) {
        deliveryCompanyRepository.deleteById(id);
        LOGGER.info("Delivery company with ID: {} deleted successfully.", id);
    }
}
