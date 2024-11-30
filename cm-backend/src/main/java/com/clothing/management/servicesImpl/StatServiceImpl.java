package com.clothing.management.servicesImpl;

import com.clothing.management.dto.*;
import com.clothing.management.dto.DayCount.*;
import com.clothing.management.entities.*;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.mappers.ModelMapper;
import com.clothing.management.mappers.OfferMapper;
import com.clothing.management.mappers.StatTableMapper;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IPacketRepository;
import com.clothing.management.repository.IProductsPacketRepository;
import com.clothing.management.services.StatService;
import com.clothing.management.tenant.DataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class StatServiceImpl implements StatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatServiceImpl.class);
    private final IProductsPacketRepository productsPacketRepository;
    private final IModelStockHistoryRepository modelStockHistoryRepository;
    private final IPacketRepository packetRepository;
    private final OfferMapper offerMapper;
    private final ModelMapper modelMapper;
    private final StatTableMapper statTableMapper;
    private final DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProviderImpl;

    public StatServiceImpl(IProductsPacketRepository productsPacketRepository,
                           IModelStockHistoryRepository modelStockHistoryRepository,
                           IPacketRepository packetRepository, OfferMapper offerMapper, ModelMapper modelMapper, StatTableMapper statTableMapper, DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProviderImpl) {
        this.productsPacketRepository = productsPacketRepository;
        this.modelStockHistoryRepository = modelStockHistoryRepository;
        this.packetRepository = packetRepository;
        this.offerMapper = offerMapper;
        this.modelMapper = modelMapper;
        this.statTableMapper = statTableMapper;
        this.dataSourceBasedMultiTenantConnectionProviderImpl = dataSourceBasedMultiTenantConnectionProviderImpl;
    }

    public Map<String, List<?>> statAllPagesChart(String beginDate, String endDate) {
        List<Long> countPagesList;
        List<PagesDayCountDTO> existingProductsPacket = productsPacketRepository.statAllPages(beginDate, endDate);
        LOGGER.info("Fetched {} product packets for the date range.", existingProductsPacket.size());

        Map<String, List<?>> uniqueValues = getUniquePages(existingProductsPacket);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<FbPage> uniquePages = (List<FbPage>) uniqueValues.get("uniquePages");

        List<List<Long>> listPagesCount = new ArrayList<>();
        List<StatTableDTO> pagesRecapCount = new ArrayList<>();

        for (FbPage page : uniquePages) {
            StatTableDTO pageRecap = new StatTableDTO(page.getName());
            countPagesList = new ArrayList<>();
            double countProfits = 0;

            // Process each unique date for the current model
            for (Date uniqueDate : uniqueDates) {
                long count = 0;
                long countRetour = 0;
                long countProgress = 0;

                // Calculate counts for the current model on the given date
                for (PagesDayCountDTO row : existingProductsPacket) {
                    if (row.getDate().equals(uniqueDate) && row.getFbPage().getId().equals(page.getId())) {
                        count += row.getCountPaid();
                        countProfits += row.getProfits();
                        countProgress += row.getCountProgress();
                        countRetour += row.getCountReturn();
                    }
                }
                pageRecap.setPaid(count + pageRecap.getPaid());
                pageRecap.setRetour(countRetour + pageRecap.getRetour());
                pageRecap.setProgress(countProgress + pageRecap.getProgress());
                countPagesList.add(count);
            }

            // Add the model's data to the list and recap counts
            listPagesCount.add(countPagesList);
            pageRecap.setMin(Collections.min(countPagesList));
            pageRecap.setMax(Collections.max(countPagesList));
            pageRecap.setAvg(pageRecap.getPaid() / uniqueDates.size());
            pageRecap.setProfits(countProfits + pageRecap.getProfits());
            pagesRecapCount.add(pageRecap);
        }

        // Total models count by date
        listPagesCount.add(countTotal(uniqueDates, listPagesCount));

        // Create total recap product table
        StatTableDTO modelTotalRecap = createPageTableRecap(pagesRecapCount);
        pagesRecapCount.add(modelTotalRecap);

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("pagesCount", listPagesCount);
        data.put("pagesRecapCount", pagesRecapCount);

        LOGGER.info("PagesCount chart data generated successfully.");
        return data;
    }

    @Override
    public Map<String, List<?>> statAllModelsTable(String beginDate, String endDate,Boolean countProgressEnabler) {
        List<String> status = getCountStatus(countProgressEnabler);
        List<ModelsDayCountDTO> modelsStat = productsPacketRepository.statAllModels(beginDate, endDate, status);
        Map<String, List<?>> data = new HashMap<>();
        data.put("modelsStat", modelsStat);
        ArrayList<ModelStockValueDTO>  statValuesDashboard = statValuesTotalDashboard();
        data.put("statValuesDashboard", statValuesDashboard);
        LOGGER.info("Model chart data generated successfully.");
        return data;
    }

    @Override
    public Map<String, List<?>> statAllModelsChart(String beginDate, String endDate,Boolean countProgressEnabler) {
        List<Long> countModelsList;
        List<String> status = getCountStatus(countProgressEnabler);
        List<ModelsDayCountDTO> existingProductsPacket = productsPacketRepository.statAllModelsChart(beginDate, endDate,status);

        LOGGER.info("Fetched {} product packets for the date range.", existingProductsPacket.size());

        Map<String, List<?>> uniqueValues = getUniqueModels(existingProductsPacket);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<ModelDTO> uniqueModels = (List<ModelDTO>) uniqueValues.get("uniqueModels");

        List<List<Long>> listModelsCount = new ArrayList<>();

        StatOfferTableDTO modelRecap;
        for (ModelDTO uniqueModel : uniqueModels) {
            countModelsList = new ArrayList<>();
            modelRecap = statTableMapper.modelToStatModelTableDTO(uniqueModel.getName());
            for (Date uniqueDate : uniqueDates) {
                long count = 0;
                for (ModelsDayCountDTO row : existingProductsPacket) {
                    if (row.getDate().equals(uniqueDate) && row.getModel().getId().equals(uniqueModel.getId())) {
                        count += row.getCountPaid();
                    }
                }
                modelRecap.setPaid(count + modelRecap.getPaid());
                countModelsList.add(count);
            }
            listModelsCount.add(countModelsList);
        }

        // Total models count by date
        listModelsCount.add(countTotal(uniqueDates, listModelsCount));

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("modelsCount", listModelsCount);
        data.put("models", uniqueModels);
        return data;
    }

    private List<String> getCountStatus(boolean countProgressEnabler){
        if (countProgressEnabler) {
            return Arrays.asList("Livrée", "Payée", "Confirmée",
                    "En cours (1)", "En cours (2)",
                    "En cours (3)", "A vérifier");
        } else {
            return Arrays.asList("Livrée", "Payée");
        }
    }

    private ArrayList<Long> countTotalNoDates(List<List<Long>> listModelsCount) {

        ArrayList<Long> countTotalList = new ArrayList<>();
        int index = 0;
            long sum = 0;
            for (List<Long> totalPerDay : listModelsCount) {
                if (index < totalPerDay.size()) {
                    sum += totalPerDay.get(index);
                } else {
                    LOGGER.warn("Index {} out of bounds for totalPerDay list with size {}.", index, totalPerDay.size());
                }
                index++;
            }
            countTotalList.add(sum);
            LOGGER.debug("Date: {}, Total count: {}", sum);

        return countTotalList;
    }

    private ArrayList<Long> countTotal(List<Date> uniqueDates, List<List<Long>> listModelsCount) {

        ArrayList<Long> countTotalList = new ArrayList<>();
        int index = 0;
        for (Date uniqueDate : uniqueDates) {
            long sum = 0;
            for (List<Long> totalPerDay : listModelsCount) {
                if (index < totalPerDay.size()) {
                    sum += totalPerDay.get(index);
                } else {
                    LOGGER.warn("Index {} out of bounds for totalPerDay list with size {}.", index, totalPerDay.size());
                }
            }
            countTotalList.add(sum);
            LOGGER.debug("Date: {}, Total count: {}", uniqueDate, sum);
            index++;
        }

        return countTotalList;
    }

    @Override
    public ArrayList<ModelStockValueDTO> statValuesDashboard() {
        // Set date to October 7, 2024
        LocalDate localDate = LocalDate.of(2024, 10, 7);
        Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch stock history data
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statValues(today);

        // Initialize variables
        long totalQuantity = 0L;
        double totalPurchasePrice = 0.0;
        double totalSellingPrice = 0.0;
        ArrayList<ModelStockValueDTO> modelStockValuesList = new ArrayList<>();
        // Iterate through stock history entries
        for (ModelStockHistory modelStockHistory : statStock) {
            Model model = modelStockHistory.getModel();
            long quantity = modelStockHistory.getQuantity();
            double purchasePriceForEntry = model.getPurchasePrice() * quantity;
            double sellingPriceForEntry = purchasePriceForEntry * model.getEarningCoefficient();
            modelStockValuesList.add(new ModelStockValueDTO(model.getName(),quantity,purchasePriceForEntry,sellingPriceForEntry,sellingPriceForEntry-purchasePriceForEntry));
            totalQuantity += quantity;
            totalPurchasePrice += purchasePriceForEntry;
            totalSellingPrice += sellingPriceForEntry;
        }
        modelStockValuesList.add(new ModelStockValueDTO("Total",totalQuantity,totalPurchasePrice,totalSellingPrice,totalSellingPrice - totalPurchasePrice));
        return modelStockValuesList;
    }

    public ArrayList<ModelStockValueDTO> statValuesTotalDashboard() {
        // Set date to October 7, 2024
        LocalDate localDate = LocalDate.of(2024, 10, 7);
        Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Fetch stock history data
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statValues(today);

        // Initialize variables
        long totalQuantity = 0L;
        double totalPurchasePrice = 0.0;
        double totalSellingPrice = 0.0;
        ArrayList<ModelStockValueDTO> modelStockValuesList = new ArrayList<>();
        // Iterate through stock history entries
        for (ModelStockHistory modelStockHistory : statStock) {
            Model model = modelStockHistory.getModel();
            long quantity = modelStockHistory.getQuantity();
            double purchasePriceForEntry = model.getPurchasePrice() * quantity;
            double sellingPriceForEntry = purchasePriceForEntry * model.getEarningCoefficient();
            totalQuantity += quantity;
            totalPurchasePrice += purchasePriceForEntry;
            totalSellingPrice += sellingPriceForEntry;
        }
        modelStockValuesList.add(new ModelStockValueDTO("Total",totalQuantity,totalPurchasePrice,totalSellingPrice,totalSellingPrice - totalPurchasePrice));

        LOGGER.info("Fetched {} stock history entries", modelStockValuesList.size());
        return modelStockValuesList;
    }

    @Override
    public Map<String, List<?>> statAllStockChart(String beginDate, String endDate) {

        // Fetch stock history
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statStockByDate(beginDate, endDate);

        // Extract unique dates and model information
        Map<String, List<?>> uniqueValues = getUniqueStock(statStock);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Long> uniqueModelsIds = (List<Long>) uniqueValues.get("modelsIds");
        List<String> uniqueModels = (List<String>) uniqueValues.get("uniqueModelsNames");

        LOGGER.debug("Unique dates: {}, Unique model IDs: {}, Unique models: {}", uniqueDates, uniqueModelsIds, uniqueModels);

        List<List<Long>> modelsStockHistory = new ArrayList<>();
        // Process stock history for each model
        for (Long uniqueModelId : uniqueModelsIds) {
            LOGGER.debug("Processing stock history for model ID: {}", uniqueModelId);

            ArrayList<Long> uniqueModelStockHistory = new ArrayList<>();

            for (Date uniqueDate : uniqueDates) {
                LOGGER.debug("Processing stock for model ID: {} on date: {}", uniqueModelId, uniqueDate);

                Long quantity = statStock.stream()
                        .filter(statStockRow -> {
                            try {
                                return statStockRow.getDate().equals(uniqueDate) && statStockRow.getModel().getId().equals(uniqueModelId);
                            } catch (Exception e) {
                                LOGGER.error("Error parsing date for model ID: {} on date: {}", uniqueModelId, uniqueDate, e);
                                throw new RuntimeException("Error parsing date", e);
                            }
                        })
                        .map(ModelStockHistory::getQuantity)
                        .findFirst()
                        .orElse(2L); // Default value if no stock data found
                uniqueModelStockHistory.add(quantity);
            }

            modelsStockHistory.add(uniqueModelStockHistory);
        }

        // Calculate total models stock history by day
        LOGGER.info("Calculating total stock history by day.");
        List<Long> totalRow = countTotal(uniqueDates, modelsStockHistory);
        modelsStockHistory.add(totalRow);
        uniqueModels.add("Total");

        // Prepare final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("models", uniqueModels);
        data.put("statStock", modelsStockHistory);
        LOGGER.info("Stock chart data generated for {} models over {} dates.", uniqueModels.size(), uniqueDates.size());
        return data;
    }


    public static Map<String, List<?>> getUniqueStock(List<ModelStockHistory> modelList) {
        List<Date> uniqueDates = new ArrayList<>();
        List<Long> uniquemodelsIds = new ArrayList<>();
        List<String> uniqueModelNames = new ArrayList<>();
        for (ModelStockHistory model : modelList) {
            Date modelStockDate = model.getDate();
            if (!uniqueDates.contains(modelStockDate)) {
                uniqueDates.add(modelStockDate);
            }

            if (!uniquemodelsIds.contains(model.getModel().getId())) {
                uniquemodelsIds.add(model.getModel().getId());
                uniqueModelNames.add(model.getModel().getName());
            }
        }
        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("modelsIds", uniquemodelsIds);
        uniqueAttributes.put("uniqueModelsNames", uniqueModelNames);

        return uniqueAttributes;
    }

    @Override
    @Transactional("tenantTransactionManager")
    public Map<String, List<?>> statAllOffersChart(String beginDate, String endDate) {

        List<Long> countOffersList;
        // Fetch offer data
        List<OffersDayCountDTO> existingOffersPacket = productsPacketRepository.offersCountByDate(beginDate, endDate);
        LOGGER.info("Fetched {} offer packets for the date range.", existingOffersPacket.size());

        // Get unique dates and offers
        Map<String, List<?>> uniqueValues = getUniqueOffers(existingOffersPacket);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<OfferDTO> uniqueOffers = (List<OfferDTO>) uniqueValues.get("uniqueOffers");
        LOGGER.debug("Unique dates: {}, Unique offers: {}", uniqueDates, uniqueOffers);

        // Initialize offers count list and recap
        List<List<Long>> listOffersCount = new ArrayList<>();
        List<StatOfferTableDTO> offersRecapCount = new ArrayList<>();
        StatOfferTableDTO offerRecap;

        // Process each unique offer
        for (OfferDTO uniqueOffer : uniqueOffers) {
            LOGGER.debug("Processing offer: {}", uniqueOffer.getName());

            countOffersList = new ArrayList<>();
            offerRecap = statTableMapper.offerToStatOfferTableDTO(uniqueOffer);

            // Process each unique date for the current offer
            for (Date uniqueDate : uniqueDates) {
                long countPaid = 0;
                long countProgress = 0;
                long countRetour = 0;
                double countProfits = 0;

                // Calculate counts for the current offer on the given date
                for (OffersDayCountDTO row : existingOffersPacket) {
                    if (row.getDate().equals(uniqueDate) && row.getOffer().getId().equals(uniqueOffer.getId())) {
                        if (row.getCountPaid() > 0) {
                            countPaid += 1;
                            countProfits += row.getProfits();
                        }
                        if (row.getCountReturn() > 0) countRetour += 1;
                        if (row.getCountProgress() > 0) countProgress += 1;
                    }
                }

                // Update the recap for the current offer
                offerRecap.setPaid(countPaid + offerRecap.getPaid());
                offerRecap.setRetour(countRetour + offerRecap.getRetour());
                offerRecap.setProgress(countProgress + offerRecap.getProgress());
                offerRecap.setProfits(countProfits + offerRecap.getProfits());
                countOffersList.add(countPaid);
            }

            // Add the offer's data to the list and recap counts
            listOffersCount.add(countOffersList);
            offerRecap.setMin(Collections.min(countOffersList));
            offerRecap.setMax(Collections.max(countOffersList));
            offerRecap.setAvg(offerRecap.getPaid() / uniqueDates.size());

            // Calculate purchase price
            Double purchasePrice = calculateOfferpurchasePrice(uniqueOffer);
            offerRecap.setPurchasePrice(purchasePrice * offerRecap.getPaid());
            offerRecap.setSellingPrice(offerRecap.getPurchasePrice() + offerRecap.getProfits());

            offersRecapCount.add(offerRecap);
        }

        LOGGER.info("Processed {} offers.", uniqueOffers.size());

        // Total offers count by date
        listOffersCount.add(countTotal(uniqueDates, listOffersCount));

        // Create total recap table for offers
        LOGGER.info("Generating total recap table for offers.");
        StatOfferTableDTO offerTotalRecap = createTableTotalRecap(offersRecapCount);
        offersRecapCount.add(offerTotalRecap);

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("countOffersLists", listOffersCount);
        data.put("offersRecapCount", offersRecapCount);

        LOGGER.info("Offers chart data generated successfully.");
        return data;
    }

    public StatTableDTO createPageTableRecap(List<StatTableDTO> recapCount) {
        StatTableDTO totalRecap = statTableMapper.toStatTableDTO(("Total"));
        return createTotalRecap(recapCount, totalRecap);
    }

    public StatOfferTableDTO createTableTotalRecap(List<StatOfferTableDTO> recapCount) {
        StatOfferTableDTO totalRecap = statTableMapper.offerToStatOfferTableDTO(
                OfferDTO.builder()
                        .name("Total")
                        .build());
        return createTotalRecap(recapCount, totalRecap);
    }

    private <T extends StatTableDTO> T createTotalRecap(List<T> recapCount, T totalRecap) {
        totalRecap.setMin(0L);

        for (T uniqueRecapCount : recapCount) {
            totalRecap.setAvg(totalRecap.getAvg() + uniqueRecapCount.getAvg());
            totalRecap.setMax(totalRecap.getMax() + uniqueRecapCount.getMax());
            totalRecap.setMin(totalRecap.getMin() + uniqueRecapCount.getMin());
            totalRecap.setPaid(totalRecap.getPaid() + uniqueRecapCount.getPaid());
            totalRecap.setProgress(totalRecap.getProgress() + uniqueRecapCount.getProgress());
            totalRecap.setRetour(totalRecap.getRetour() + uniqueRecapCount.getRetour());
            totalRecap.setProfits(totalRecap.getProfits() + uniqueRecapCount.getProfits());

            // Specific fields for subclasses
            if (uniqueRecapCount instanceof StatOfferTableDTO offerRecap) {
                StatOfferTableDTO offerTotalRecap = (StatOfferTableDTO) totalRecap;
                offerTotalRecap.setPurchasePrice(offerTotalRecap.getPurchasePrice() + offerRecap.getPurchasePrice());
                offerTotalRecap.setSellingPrice(offerTotalRecap.getSellingPrice() + offerRecap.getSellingPrice());
            }
        }
        return totalRecap;
    }


    private <T extends StatTableDTO> T createPagesTotalRecap(List<T> recapCount, T totalRecap) {
        totalRecap.setMin(0L);

        for (T uniqueRecapCount : recapCount) {
            totalRecap.setAvg(totalRecap.getAvg() + uniqueRecapCount.getAvg());
            totalRecap.setMax(totalRecap.getMax() + uniqueRecapCount.getMax());
            totalRecap.setMin(totalRecap.getMin() + uniqueRecapCount.getMin());
            totalRecap.setPaid(totalRecap.getPaid() + uniqueRecapCount.getPaid());
        }
        return totalRecap;
    }


    private Double calculateOfferpurchasePrice(OfferDTO offer) {
        double purchasePrice = 0;
        Set<OfferModelsDTO> offerModels= offer.getOfferModels();
        for (OfferModelsDTO uniqueOfferModel : offerModels) {
            purchasePrice +=uniqueOfferModel.getQuantity()*uniqueOfferModel.getModel().getPurchasePrice();
        }
        return purchasePrice;
    }

    @Override
    public Map<String, List<?>> statAllColorsChart(String beginDate, String endDate) {

        // Fetch products data by color
        List<ColorsDayCountDTO> existingProductsPacketColor = productsPacketRepository.statAllModelsByColor(beginDate, endDate);
        LOGGER.info("Fetched {} product packets by color for the date range.", existingProductsPacketColor.size());

        // Get unique dates and colors
        Map<String, List<?>> uniqueValues = getUniqueColors(existingProductsPacketColor);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");

        LOGGER.debug("Unique dates: {}, Unique colors: {}", uniqueDates, uniqueColors);

        // Initialize lists for color counts and recap
        List<List<Integer>> countColorsLists = new ArrayList<>();
        List<StatColorTableDTO> colorsRecapCount = new ArrayList<>();
        StatColorTableDTO colorRecap;
        ArrayList<Integer> uniqueColorCountList;

        // Process each unique color
        for (Color uniqueColor : uniqueColors) {
            uniqueColorCountList = new ArrayList<>();
            colorRecap = new StatColorTableDTO(uniqueColor);

            // Process each unique date for the current color
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                int countRetour = 0;
                int countProgress = 0;

                // Calculate counts for the current color on the given date
                for (ColorsDayCountDTO product : existingProductsPacketColor) {
                    if (product.getDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId())) {
                        count += (int) product.getCountPaid();
                        countProgress += (int) product.getCountProgress();
                    }
                }

                // Update the color recap
                if (count == 0) colorRecap.setMin(0L);
                colorRecap.setPaid(colorRecap.getPaid() + count);
                colorRecap.setRetour(countRetour + colorRecap.getRetour());
                colorRecap.setProgress(countProgress + colorRecap.getProgress());
                uniqueColorCountList.add(count);
            }

            // Add the color's data to the list and recap counts
            countColorsLists.add(uniqueColorCountList);
            colorRecap.setMin(Long.valueOf(Collections.min(uniqueColorCountList)));
            colorRecap.setMax(Long.valueOf(Collections.max(uniqueColorCountList)));
            colorRecap.setAvg(colorRecap.getPaid() / uniqueDates.size());
            colorsRecapCount.add(colorRecap);
        }

        LOGGER.info("Processed {} unique colors.", uniqueColors.size());

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("countColorsLists", countColorsLists);
        data.put("colorsRecapCount", colorsRecapCount);

        LOGGER.info("Color chart data generated successfully.");
        return data;
    }

    public static List<Date> getUniqueDates(List<PacketsStatCountDTO> packetList) {
        return packetList.stream()
                .map(PacketsStatCountDTO::getDate)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<?>> statAllPacketsChart(String beginDate, String endDate, String deliveryCompanyName) {

        // Fetch existing packets
        List<PacketsStatCountDTO> existingPackets = existingPackets(beginDate, endDate, deliveryCompanyName);
        LOGGER.info("Fetched {} packets for delivery company: {}", existingPackets.size(), deliveryCompanyName);

        // Get unique dates
        List<Date> uniqueDates = getUniqueDates(existingPackets);
        LOGGER.debug("Unique dates: {}", uniqueDates);

        // Prepare the data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("statusCountLists", createStatusCountLists(existingPackets));
        data.put("statusRecapCount", createStatusRecapCount(existingPackets, uniqueDates.size()));

        LOGGER.info("Packet stats chart data generated successfully.");
        return data;
    }

    List<PacketsStatCountDTO> existingPackets(String beginDate, String endDate, String deliveryCompanyName) {
        LOGGER.debug("Fetching packets for date range: {} - {}, Delivery Company: {}", beginDate, endDate, deliveryCompanyName);

        if (deliveryCompanyName.equals("ALL")) {
            LOGGER.debug("Fetching all packets.");
            return productsPacketRepository.statAllPackets(beginDate, endDate);
        } else {
            LOGGER.debug("Fetching packets for specific delivery company: {}", deliveryCompanyName);
            return productsPacketRepository.statAllPackets(beginDate, endDate, deliveryCompanyName);
        }
    }

    private List<List<Long>> createStatusCountLists(List<PacketsStatCountDTO> existingPackets) {

        List<Long> countAll = new ArrayList<>();
        List<Long> countExchange = new ArrayList<>();
        List<Long> countOut = new ArrayList<>();
        List<Long> countPaid = new ArrayList<>();
        List<Long> countReturn = new ArrayList<>();
        List<Long> countOos = new ArrayList<>();
        List<Long> countProgress = new ArrayList<>();

        // Iterate through packet stats and populate the lists
        for (PacketsStatCountDTO dayStat : existingPackets) {
            countAll.add(dayStat.getCountAll());
            countExchange.add(dayStat.getCountExchange());
            countOut.add(dayStat.getCountOut());
            countPaid.add(dayStat.getCountPaid()+dayStat.getCountRecived());
            countReturn.add(dayStat.getCountReturn());
            countOos.add(dayStat.getCountOos());
            countProgress.add(dayStat.getCountProgress());
        }

        LOGGER.debug("Status count lists created successfully.");
        return Arrays.asList(countExchange, countReturn, countPaid, countOut, countProgress, countOos, countAll);
    }

    private List<StatTableDTO> createStatusRecapCount(List<PacketsStatCountDTO> existingPackets, int datesSize) {

        StatTableDTO exchangeRecap = statTableMapper.toStatTableDTO(SystemStatus.EXCHANGE.getStatus());
        StatTableDTO returnRecap = statTableMapper.toStatTableDTO(SystemStatus.RETURN.getStatus());
        StatTableDTO paidRecap = statTableMapper.toStatTableDTO(SystemStatus.PAID.getStatus());
        StatTableDTO receivedRecap = statTableMapper.toStatTableDTO(SystemStatus.LIVREE.getStatus());
        StatTableDTO oosRecap = statTableMapper.toStatTableDTO(SystemStatus.OOS.getStatus());
        StatTableDTO outRecap = statTableMapper.toStatTableDTO("Sortie");
        StatTableDTO allRecap = statTableMapper.toStatTableDTO("All");
        StatTableDTO progressRecap = statTableMapper.toStatTableDTO("En Cours");

        // Update recap stats
        for (PacketsStatCountDTO dayStat : existingPackets) {
            updateRecapStats(exchangeRecap, dayStat.getCountExchange());
            updateRecapStats(outRecap, dayStat.getCountOut());
            updateRecapStats(allRecap, dayStat.getCountAll());
            updateRecapStats(paidRecap, dayStat.getCountPaid());
            updateRecapStats(receivedRecap, dayStat.getCountRecived());
            updateRecapStats(returnRecap, dayStat.getCountReturn());
            updateRecapStats(oosRecap, dayStat.getCountOos());
            updateRecapStats(progressRecap, dayStat.getCountProgress());
        }

        // Finalize recap stats
        long totalPaidReceived = outRecap.getPaid()+outRecap.getReceived();
        finalizeRecapStats(exchangeRecap, datesSize, totalPaidReceived);
        finalizeRecapStats(outRecap, datesSize, allRecap.getPaid());
        finalizeRecapStats(allRecap, datesSize, allRecap.getPaid());
        finalizeRecapStats(paidRecap, datesSize, totalPaidReceived);
        finalizeRecapStats(receivedRecap, datesSize, totalPaidReceived);
        finalizeRecapStats(returnRecap, datesSize, outRecap.getPaid());
        finalizeRecapStats(oosRecap, datesSize, allRecap.getPaid());
        finalizeRecapStats(progressRecap, datesSize, allRecap.getPaid()); // No percentage for progressRecap

        LOGGER.debug("Status recap count created successfully.");
        return Arrays.asList(exchangeRecap, returnRecap, progressRecap, receivedRecap, paidRecap, outRecap, oosRecap, allRecap);
    }

    // For packets chart
    private void updateRecapStats(StatTableDTO recap, long count) {
        recap.setMin(Math.min(count, recap.getMin()));
        recap.setMax(Math.max(count, recap.getMax()));
        recap.setPaid(count + recap.getPaid());
        LOGGER.debug("Updated recap stats. New min: {}, New max: {}, New paid: {}", recap.getMin(), recap.getMax(), recap.getPaid());
    }

    // For packets chart
    private void finalizeRecapStats(StatTableDTO recap, int uniqueDatesSize, double total) {
        if (uniqueDatesSize == 0) uniqueDatesSize = 1;
        double avg = (double) recap.getPaid() / uniqueDatesSize;
        double percentage = total == 0 ? 0 : Math.round(recap.getPaid() * 10000.0 / total) / 100.0;

        recap.setAvg((long) avg);
        recap.setPer(percentage);

        LOGGER.debug("Finalizing recap stats. Avg: {}, Percentage: {}", avg, percentage);
    }

    public Map<String, List<?>> getUniqueModels(List<ModelsDayCountDTO> productsList) {

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<ModelDTO> uniqueModels = new ArrayList<>();
        List<Long> uniqueModelsIds = new ArrayList<>();

        for (ModelsDayCountDTO product : productsList) {
            if (!uniqueDates.contains(product.getDate())) {
                uniqueDates.add(product.getDate());
            }

            Model model = product.getModel();
            if (!uniqueModelsIds.contains(model.getId())) {
                uniqueModels.add(modelMapper.toDto(model));
                uniqueModelsIds.add(model.getId());
            }
        }
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueModels", uniqueModels);
        return uniqueAttributes;
    }

    public Map<String, List<?>> getUniquePages(List<PagesDayCountDTO> pagesList) {

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Long> uniquePagesIds = new ArrayList<>();
        List<FbPage> uniquePages = new ArrayList<>();

        for (PagesDayCountDTO page : pagesList) {
            if (!uniqueDates.contains(page.getDate())) {
                uniqueDates.add(page.getDate());
            }
            FbPage fbPage = page.getFbPage();
            if (!uniquePagesIds.contains(fbPage.getId())) {
                uniquePages.add(fbPage);
                uniquePagesIds.add(fbPage.getId());
            }

        }
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniquePages", uniquePages);
        return uniqueAttributes;
    }

    public Map<String, List<?>> getUniqueColors(List<ColorsDayCountDTO> productsList) {

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();

        for (ColorsDayCountDTO product : productsList) {
            if (!uniqueDates.contains(product.getDate())) {
                uniqueDates.add(product.getDate());
            }

            Color color = product.getColor();
            if (color != null && !uniqueColors.contains(color)) {
                uniqueColors.add(color);
            }
        }
        LOGGER.info("Unique attributes extracted. Dates: {}, Colors: {}", uniqueDates.size(), uniqueColors.size());
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueColors", uniqueColors);
        return uniqueAttributes;
    }

    @Transactional("tenantTransactionManager")
    public Map<String, List<?>> getUniqueOffers(List<OffersDayCountDTO> productsList) {

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<OfferDTO> uniqueOffers = new ArrayList<>();
        List<Long> uniqueOffersIds = new ArrayList<>();

        for (OffersDayCountDTO product : productsList) {
            Offer offer = product.getOffer();
            if (!uniqueDates.contains(product.getDate())) {
                uniqueDates.add(product.getDate());
            }
            if (offer != null && !uniqueOffersIds.contains(offer.getId())) {
                uniqueOffersIds.add(offer.getId());
                uniqueOffers.add(offerMapper.toDto(offer));
            }
        }

        LOGGER.info("Unique attributes extracted. Offers: {}, Dates: {}", uniqueOffers.size(), uniqueDates.size());

        uniqueAttributes.put("uniqueOffers", uniqueOffers);
        uniqueAttributes.put("uniqueDates", uniqueDates);

        return uniqueAttributes;
    }

    private Map<String, List<?>> getUniqueValues(List<ProductDayCountDTO> existingProductsPacket) {

        Map<String, List<?>> uniqueValues = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();
        List<Size> uniqueSizes = new ArrayList<>();
        List<String> uniqueProductRefs = new ArrayList<>();
        List<Long> uniqueProductIds = new ArrayList<>();

        for (ProductDayCountDTO product : existingProductsPacket) {
            if (product.getDate() != null && !uniqueDates.contains(product.getDate())) uniqueDates.add(product.getDate());
            if (product.getColor() != null && !uniqueColors.contains(product.getColor())) uniqueColors.add(product.getColor());
            if (product.getSize() != null && !uniqueSizes.contains(product.getSize())) uniqueSizes.add(product.getSize());
            String colorAndSize = null;
            if (product.getColor() != null && product.getSize() != null) {
                colorAndSize = product.getColor().getName() + " " + product.getSize().getReference();
            }
            if (!uniqueProductRefs.contains(colorAndSize)) uniqueProductRefs.add(colorAndSize);
            if (product.getProductId() != null && !uniqueProductIds.contains(product.getProductId())) uniqueProductIds.add(product.getProductId());
        }

        LOGGER.info("Unique values extracted. Dates: {}, Colors: {}, Sizes: {}, Product Refs: {}, Product Ids: {}",
                uniqueDates.size(), uniqueColors.size(), uniqueSizes.size(), uniqueProductRefs.size(), uniqueProductIds.size());

        uniqueValues.put("uniqueDates", uniqueDates);
        uniqueValues.put("uniqueColors", uniqueColors);
        uniqueValues.put("uniqueSizes", uniqueSizes);
        uniqueValues.put("uniqueProductRefs", uniqueProductRefs);
        uniqueValues.put("uniqueProductIds", uniqueProductIds);

        return uniqueValues;
    }



    @Override // Used in stock (stock par model)
    public Map<String, List<?>> statModelSoldChart(Long modelId, String beginDate, String endDate) {

        // Fetch existing product packets
        List<ProductDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId, beginDate, endDate);
        LOGGER.debug("Fetched {} product packets for Model ID: {}", existingProductsPacket.size(), modelId);

        // Get unique values (dates, colors, sizes, product refs)
        Map<String, List<?>> uniqueValues = getUniqueValues(existingProductsPacket);

        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");

        List<Color> uniqueColors = (List<Color>) uniqueValues.get("uniqueColors");
        LOGGER.debug("Unique colors: {}", uniqueColors);

        List<Size> uniqueSizes = (List<Size>) uniqueValues.get("uniqueSizes");
        LOGGER.debug("Unique sizes: {}", uniqueSizes);

        List<String> uniqueProductRefs = (List<String>) uniqueValues.get("uniqueProductRefs");
        LOGGER.debug("Unique product references: {}", uniqueProductRefs);

        // Generate counts for products, colors, and sizes
        List<List<Integer>> listProductsCount = getProductCounts(existingProductsPacket, uniqueProductRefs, uniqueDates);
        LOGGER.debug("Product counts generated.");

        List<List<Integer>> listColorsCount = getColorCounts(existingProductsPacket, uniqueColors, uniqueDates);
        LOGGER.debug("Color counts generated.");

        List<List<Integer>> listSizesCount = getSizeCounts(existingProductsPacket, uniqueSizes, uniqueDates);
        LOGGER.debug("Size counts generated.");

        // Prepare data for the response
        Map<String, List<?>> dataHashMap = new HashMap<>();
        dataHashMap.put("sizes", uniqueSizes);
        dataHashMap.put("colors", uniqueColors);
        dataHashMap.put("productRefs", uniqueProductRefs);
        dataHashMap.put("dates", uniqueDates);
        dataHashMap.put("productsCount", listProductsCount);
        dataHashMap.put("sizesCount", listSizesCount);
        dataHashMap.put("colorsCount", listColorsCount);

        LOGGER.info("Sold model chart generated successfully for Model ID: {}", modelId);
        return dataHashMap;
    }

    private List<List<Integer>> getProductCounts(List<ProductDayCountDTO> existingProductsPacket, List<String> uniqueProductRefs, List<Date> uniqueDates) {

        List<List<Integer>> listProductsCount = new ArrayList<>();
        for (String uniqueProductRef : uniqueProductRefs) {
            LOGGER.debug("Processing product reference: {}", uniqueProductRef);

            List<Integer> countProductsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductDayCountDTO productDto : existingProductsPacket) {
                    String colorAndSize = productDto.getColor().getName() + " " + productDto.getSize().getReference();
                    if (productDto.getDate().equals(uniqueDate) && uniqueProductRef.equals(colorAndSize)) {
                        count += (int) productDto.getCountPaid();
                    }
                }
                countProductsList.add(count);
                LOGGER.debug("Date: {}, Product Ref: {}, Count: {}", uniqueDate, uniqueProductRef, count);
            }
            listProductsCount.add(countProductsList);
        }

        LOGGER.info("Completed fetching product counts.");
        return listProductsCount;
    }

    private List<List<Integer>> getColorCounts(List<ProductDayCountDTO> existingProductsPacket, List<Color> uniqueColors, List<Date> uniqueDates) {

        List<List<Integer>> listColorsCount = new ArrayList<>();
        for (Color uniqueColor : uniqueColors) {

            List<Integer> countColorsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductDayCountDTO product : existingProductsPacket) {
                    if (product.getDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId())) {
                        count += (int) (product.getCountPaid()+ product.getCountProgress());
                    }
                }
                countColorsList.add(count);
            }
            listColorsCount.add(countColorsList);
        }

        LOGGER.info("Completed fetching color counts.");
        return listColorsCount;
    }

    private List<List<Integer>> getSizeCounts(List<ProductDayCountDTO> existingProductsPacket, List<Size> uniqueSizes, List<Date> uniqueDates) {

        List<List<Integer>> listSizesCount = new ArrayList<>();
        for (Size uniqueSize : uniqueSizes) {
            LOGGER.debug("Processing size: {}", uniqueSize.getReference());

            List<Integer> countSizesList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ProductDayCountDTO product : existingProductsPacket) {
                    if (product.getDate().equals(uniqueDate) && product.getSize().getId().equals(uniqueSize.getId())) {
                        count += (int) (product.getCountPaid()+product.getCountProgress());
                    }
                }
                countSizesList.add(count);
                LOGGER.debug("Date: {}, Size: {}, Count: {}", uniqueDate, uniqueSize.getReference(), count);
            }
            listSizesCount.add(countSizesList);
        }

        LOGGER.info("Completed fetching size counts.");
        return listSizesCount;
    }


    @Override
    public List<ProductsDayCountDTO> productsCountByDate(Long modelId, String beginDate, String endDate) {
        List<ProductsDayCountDTO> productCounts = productsPacketRepository.productsCountByDate(modelId, beginDate, endDate);

        LOGGER.debug("Fetched {} product count entries for Model ID: {}", productCounts.size(), modelId);
        return productCounts;
    }

    @Override
    public List<StatesStatCountDTO> findAllPacketsStates(String beginDate, String endDate) {
        List<StatesStatCountDTO> statesStatCounts = packetRepository.findAllPacketsStates(beginDate, endDate);

        LOGGER.debug("Fetched {} packet state statistics entries.", statesStatCounts.size());
        return statesStatCounts;
    }
}