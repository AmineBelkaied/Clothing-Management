package com.clothing.management.services;

import com.clothing.management.entities.*;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PacketServiceTest {

    @Autowired
    private IFbPageRepository fbPageRepository;
    @Autowired
    private IDeliveryCompanyRepository deliveryCompanyRepository;
    @Autowired
    private IGovernorateRepository governorateRepository;
    @Autowired
    private ICityRepository cityRepository;
    @Autowired
    private IPacketRepository packetRepository;
    private Packet packet;

    @BeforeEach
    public void setUp() {
        FbPage fbPage = addFbPage();
        DeliveryCompany deliveryCompany = addDeliveryCompany();
        Governorate governorate = addGovernorate();
        City city = addCity(governorate);
        packet = new Packet();
        packet.setCustomerName("Amine Belkaied");
        packet.setCustomerPhoneNb("123456789");
        packet.setFbPage(fbPage);
        packet.setDeliveryCompany(deliveryCompany);
        packet.setCity(city);
        packet.setAddress("Rue des jasmins");
        packet.setPacketDescription("P6 Noir(XL)");
        packet.setBarcode("ABC123");
        packet.setPrice(100.0);
        packet.setDeliveryPrice(10.0);
        packet.setDiscount(5.0);
        packet.setDate(new Date());
        packet.setStatus(String.valueOf(SystemStatus.NOT_CONFIRMED));
        packet.setExchange(false);
        packet.setValid(false);
    }

    @Test
    public void testAddPacket() {
        Packet createdPacket = packetRepository.save(packet);
        assertNotNull(createdPacket);
        assertNotNull(createdPacket.getId());
        assertEquals(packet.getCustomerName(), createdPacket.getCustomerName());
        assertEquals(packet.getCustomerPhoneNb(), createdPacket.getCustomerPhoneNb());
        assertEquals(packet.getFbPage(), createdPacket.getFbPage());
        assertEquals(packet.getCity(), createdPacket.getCity());
        assertEquals(packet.getDeliveryCompany(), createdPacket.getDeliveryCompany());
        assertEquals(packet.getCustomerPhoneNb(), createdPacket.getCustomerPhoneNb());
        assertEquals(packet.getAddress(), createdPacket.getAddress());
        assertEquals(packet.getPacketDescription(), createdPacket.getPacketDescription());
        assertEquals(packet.getPrice(), createdPacket.getPrice());
        assertEquals(packet.getDeliveryPrice(), createdPacket.getDeliveryPrice());
        assertEquals(packet.getDate(), createdPacket.getDate());
        assertEquals(packet.getStatus(), createdPacket.getStatus());
        assertEquals(packet.isValid(), createdPacket.isValid());

        // Check the size of all packets after adding the new packet
        List<Packet> allPackets = packetRepository.findAll();
        assertNotNull(allPackets);
        assertTrue(allPackets.size() > 0);  // Ensure at least one packet exists
    }

    private City addCity(Governorate governorate) {
        City newCity = new City();
        newCity.setName("Hammamet");
        newCity.setPostalCode("8050");
        newCity.setGovernorate(governorate);
        return cityRepository.save(newCity);
    }

    private Governorate addGovernorate() {
        Governorate newGovernorate = new Governorate();
        newGovernorate.setName("Nabeul");
        return governorateRepository.save(newGovernorate);
    }

    private DeliveryCompany addDeliveryCompany() {
        DeliveryCompany newDeliveryCompany = new DeliveryCompany();
        newDeliveryCompany.setName("NAVEX");
        return deliveryCompanyRepository.save(newDeliveryCompany);
    }

    private FbPage addFbPage() {
        FbPage newFbPage = new FbPage();
        newFbPage.setName("Diggy");
        newFbPage.setEnabled(true);
        return fbPageRepository.save(newFbPage);
    }

}
