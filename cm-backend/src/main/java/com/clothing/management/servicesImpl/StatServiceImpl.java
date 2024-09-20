package com.clothing.management.servicesImpl;

import com.clothing.management.dto.*;
import com.clothing.management.dto.DayCount.*;
import com.clothing.management.entities.*;
import com.clothing.management.enums.SystemStatus;
import com.clothing.management.mappers.OfferMapper;
import com.clothing.management.mappers.StatOfferTableMapper;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IPacketRepository;
import com.clothing.management.repository.IProductsPacketRepository;
import com.clothing.management.services.PacketService;
import com.clothing.management.services.StatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional("tenantTransactionManager")
public class StatServiceImpl implements StatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatService.class);
    private final IProductsPacketRepository productsPacketRepository;
    private final IModelStockHistoryRepository modelStockHistoryRepository;
    private final IPacketRepository packetRepository;
    private final OfferMapper offerMapper;
    private final StatOfferTableMapper statOfferTableMapper;

    public StatServiceImpl(IProductsPacketRepository productsPacketRepository,
                           IModelStockHistoryRepository modelStockHistoryRepository,
                           IPacketRepository packetRepository, OfferMapper offerMapper, StatOfferTableMapper statOfferTableMapper) {
        this.productsPacketRepository = productsPacketRepository;
        this.modelStockHistoryRepository = modelStockHistoryRepository;
        this.packetRepository = packetRepository;
        this.offerMapper = offerMapper;
        this.statOfferTableMapper = statOfferTableMapper;
    }

    @Override
    public Map<String, List<?>> statAllModelsChart(String beginDate, String endDate, Boolean countProgressEnabler) {
        LOGGER.info("Generating model chart for date range: {} - {} with count progress enabled: {}", beginDate, endDate, countProgressEnabler);

        // Initialize lists
        List<Long> countModelsList;
        ArrayList<Integer> countTotalList = new ArrayList<>();

        // Fetch product data
        List<ProductsDayCountDTO> existingProductsPacket = productsPacketRepository.statAllModels(beginDate, endDate);
        LOGGER.info("Fetched {} product packets for the date range.", existingProductsPacket.size());

        // Get unique dates and models
        Map<String, List<?>> uniqueValues = getUnique(existingProductsPacket);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<String> uniqueModels = (List<String>) uniqueValues.get("uniqueModelNames");

        LOGGER.debug("Unique dates: {}, Unique models: {}", uniqueDates, uniqueModels);

        // Initialize model count lists and recap
        List<List<Long>> listModelsCount = new ArrayList<>();
        List<StatTableDTO> modelsRecapCount = new ArrayList<>();

        StatTableDTO modelRecap = null;

        // Process each unique model
        for (String uniqueModel : uniqueModels) {
            LOGGER.debug("Processing model: {}", uniqueModel);

            countModelsList = new ArrayList<>();
            double countProfits = 0;
            modelRecap = new StatTableDTO(uniqueModel);

            // Process each unique date for the current model
            for (Date uniqueDate : uniqueDates) {
                long count = 0;
                long countRetour = 0;
                long countProgress = 0;

                // Calculate counts for the current model on the given date
                for (ProductsDayCountDTO row : existingProductsPacket) {
                    if (row.getDate().equals(uniqueDate) && row.getModelName().equals(uniqueModel)) {
                        if (countProgressEnabler) {
                            count += row.getCountPayed() + row.getCountProgress();
                            countProfits += row.getProfits();
                        } else {
                            count += row.getCountPayed();
                            countProfits += row.getProfits();
                        }

                        countProgress += row.getCountProgress();
                        countRetour += row.getCountReturn();
                    }
                }

                // Update the recap for the current model
                modelRecap.setPayed(count + modelRecap.getPayed());
                modelRecap.setRetour(countRetour + modelRecap.getRetour());
                modelRecap.setProgress(countProgress + modelRecap.getProgress());
                countModelsList.add(count);
            }

            // Add the model's data to the list and recap counts
            listModelsCount.add(countModelsList);
            modelRecap.setMin(Collections.min(countModelsList));
            modelRecap.setMax(Collections.max(countModelsList));
            modelRecap.setAvg(modelRecap.getPayed() / uniqueDates.size());
            modelRecap.setProfits(countProfits + modelRecap.getProfits());
            modelsRecapCount.add(modelRecap);
        }

        LOGGER.info("Processed {} models.", uniqueModels.size());

        // Total models count by date
        listModelsCount.add(countTotal(uniqueDates, listModelsCount));

        // Create total recap product table
        LOGGER.info("Generating total recap product table.");
        StatTableDTO modelTotalRecap = createTableRecap(modelsRecapCount);
        modelsRecapCount.add(modelTotalRecap);

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("modelsCount", listModelsCount);
        data.put("modelsRecapCount", modelsRecapCount);

        LOGGER.info("Model chart data generated successfully.");
        return data;
    }

    private ArrayList<Long> countTotal(List<Date> uniqueDates, List<List<Long>> listModelsCount) {
        LOGGER.info("Counting totals for {} dates and {} model counts.", uniqueDates.size(), listModelsCount.size());

        ArrayList<Long> countTotalList = new ArrayList<>();
        long index = 0;

        for (Date uniqueDate : uniqueDates) {
            long sum = 0;
            for (List<Long> totalPerDay : listModelsCount) {
                if (index < totalPerDay.size()) {
                    sum += totalPerDay.get(Math.toIntExact(index));
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
    public Map<String, List<?>> statAllStockChart(String beginDate, String endDate) {
        LOGGER.info("Generating stock chart for date range: {} - {}", beginDate, endDate);

        // Fetch stock history
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statStockByDate(beginDate, endDate);
        LOGGER.info("Fetched {} stock history entries", statStock.size());

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
                                return statStockRow.getDate().equals(uniqueDate) && statStockRow.getModelId().equals(uniqueModelId);
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
        modelsStockHistory.add(countTotal(uniqueDates, modelsStockHistory));
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

            if (!uniquemodelsIds.contains(model.getModelId())) {
                uniquemodelsIds.add(model.getModelId());
                uniqueModelNames.add(model.getModelName());
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
        LOGGER.info("Generating offers chart for date range: {} - {}", beginDate, endDate);

        // Initialize lists
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

        StatOfferTableDTO offerRecap = null;

        // Process each unique offer
        for (OfferDTO uniqueOffer : uniqueOffers) {
            LOGGER.debug("Processing offer: {}", uniqueOffer.getName());

            countOffersList = new ArrayList<>();
            offerRecap = statOfferTableMapper.offerToStatOfferTableDTO(uniqueOffer);

            // Process each unique date for the current offer
            for (Date uniqueDate : uniqueDates) {
                long countPayed = 0;
                long countProgress = 0;
                long countRetour = 0;
                double countProfits = 0;

                // Calculate counts for the current offer on the given date
                for (OffersDayCountDTO row : existingOffersPacket) {
                    if (row.getDate().equals(uniqueDate) && row.getOffer().getId() == uniqueOffer.getId()) {
                        if (row.getCountPayed() > 0) {
                            countPayed += 1;
                            countProfits += row.getProfits();
                        }
                        if (row.getCountReturn() > 0) countRetour += 1;
                        if (row.getCountProgress() > 0) countProgress += 1;
                    }
                }

                // Update the recap for the current offer
                offerRecap.setPayed(countPayed + offerRecap.getPayed());
                offerRecap.setRetour(countRetour + offerRecap.getRetour());
                offerRecap.setProgress(countProgress + offerRecap.getProgress());
                offerRecap.setProfits(countProfits + offerRecap.getProfits());
                countOffersList.add(countPayed);
            }

            // Add the offer's data to the list and recap counts
            listOffersCount.add(countOffersList);
            offerRecap.setMin(Collections.min(countOffersList));
            offerRecap.setMax(Collections.max(countOffersList));
            offerRecap.setAvg(offerRecap.getPayed() / uniqueDates.size());

            // Calculate purchase price
            Double purchasePrice = calculateOfferPurshasePrice(uniqueOffer);
            offerRecap.setPurchasePrice(purchasePrice * offerRecap.getPayed());
            offerRecap.setSellingPrice(offerRecap.getPurchasePrice() + offerRecap.getProfits());

            offersRecapCount.add(offerRecap);
        }

        LOGGER.info("Processed {} offers.", uniqueOffers.size());

        // Total offers count by date
        listOffersCount.add(countTotal(uniqueDates, listOffersCount));

        // Create total recap table for offers
        LOGGER.info("Generating total recap table for offers.");
        StatOfferTableDTO offerTotalRecap = createOfferTableTotalRecap(offersRecapCount);
        offersRecapCount.add(offerTotalRecap);

        // Prepare the final data map
        Map<String, List<?>> data = new HashMap<>();
        data.put("dates", uniqueDates);
        data.put("countOffersLists", listOffersCount);
        data.put("offersRecapCount", offersRecapCount);

        LOGGER.info("Offers chart data generated successfully.");
        return data;
    }

    public StatTableDTO createTableRecap(List<StatTableDTO> recapCount) {
        LOGGER.info("Creating table recap from {} StatTableDTO objects.", recapCount.size());
        StatTableDTO totalRecap = new StatTableDTO("Total");
        return createTotalRecap(recapCount, totalRecap);
    }

    public StatOfferTableDTO createOfferTableTotalRecap(List<StatOfferTableDTO> recapCount) {
        StatOfferTableDTO totalRecap = statOfferTableMapper.offerToStatOfferTableDTO(
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
            totalRecap.setPayed(totalRecap.getPayed() + uniqueRecapCount.getPayed());
            totalRecap.setRetour(totalRecap.getRetour() + uniqueRecapCount.getRetour());
            totalRecap.setProfits(totalRecap.getProfits() + uniqueRecapCount.getProfits());

            // Specific fields for subclasses
            if (uniqueRecapCount instanceof StatOfferTableDTO) {
                StatOfferTableDTO offerRecap = (StatOfferTableDTO) uniqueRecapCount;
                StatOfferTableDTO offerTotalRecap = (StatOfferTableDTO) totalRecap;
                offerTotalRecap.setPurchasePrice(offerTotalRecap.getPurchasePrice() + offerRecap.getPurchasePrice());
                offerTotalRecap.setSellingPrice(offerTotalRecap.getSellingPrice() + offerRecap.getSellingPrice());
            }
        }
        return totalRecap;
    }


    private Double calculateOfferPurshasePrice(OfferDTO offer) {
        double purshasePrice = 0;
        Set<OfferModelsDTO> offerModels= offer.getOfferModels();
        for (OfferModelsDTO uniqueOfferModel : offerModels) {
            purshasePrice +=uniqueOfferModel.getQuantity()*uniqueOfferModel.getModel().getPurchasePrice();
        }
        return purshasePrice;
    }

    @Override
    public Map<String, List<?>> statAllColorsChart(String beginDate, String endDate) {
        LOGGER.info("Generating color chart for date range: {} - {}", beginDate, endDate);

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
        List<StatTableDTO> colorsRecapCount = new ArrayList<>();
        StatTableDTO colorRecap;
        ArrayList<Integer> uniqueColorCountList;

        // Process each unique color
        for (Color uniqueColor : uniqueColors) {
            LOGGER.debug("Processing color: {}", uniqueColor.getName());

            uniqueColorCountList = new ArrayList<>();
            colorRecap = new StatTableDTO(uniqueColor.getName());

            // Process each unique date for the current color
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                int countRetour = 0;
                int countProgress = 0;

                // Calculate counts for the current color on the given date
                for (ColorsDayCountDTO product : existingProductsPacketColor) {
                    if (product.getDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId())) {
                        count += product.getCountPayed();
                        countProgress += product.getCountProgress();
                    }
                }

                // Update the color recap
                if (count == 0) colorRecap.setMin(0L);
                colorRecap.setPayed(colorRecap.getPayed() + count);
                colorRecap.setRetour(countRetour + colorRecap.getRetour());
                colorRecap.setProgress(countProgress + colorRecap.getProgress());
                uniqueColorCountList.add(count);
            }

            // Add the color's data to the list and recap counts
            countColorsLists.add(uniqueColorCountList);
            colorRecap.setMin(Long.valueOf(Collections.min(uniqueColorCountList)));
            colorRecap.setMax(Long.valueOf(Collections.max(uniqueColorCountList)));
            colorRecap.setAvg(colorRecap.getPayed() / uniqueDates.size());
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
        LOGGER.info("Generating packet stats chart for date range: {} - {}, Delivery Company: {}", beginDate, endDate, deliveryCompanyName);

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
        LOGGER.debug("Creating status count lists from packet data.");

        List<Long> countAll = new ArrayList<>();
        List<Long> countExchange = new ArrayList<>();
        List<Long> countOut = new ArrayList<>();
        List<Long> countPayed = new ArrayList<>();
        List<Long> countReturn = new ArrayList<>();
        List<Long> countOos = new ArrayList<>();
        List<Long> countProgress = new ArrayList<>();

        // Iterate through packet stats and populate the lists
        for (PacketsStatCountDTO dayStat : existingPackets) {
            countAll.add(dayStat.getCountAll());
            countExchange.add(dayStat.getCountExchange());
            countOut.add(dayStat.getCountOut());
            countPayed.add(dayStat.getCountPayed());
            countReturn.add(dayStat.getCountReturn());
            countOos.add(dayStat.getCountOos());
            countProgress.add(dayStat.getCountProgress());
        }

        LOGGER.debug("Status count lists created successfully.");
        return Arrays.asList(countExchange, countReturn, countPayed, countOut, countProgress, countOos, countAll);
    }

    private List<StatTableDTO> createStatusRecapCount(List<PacketsStatCountDTO> existingPackets, int datesSize) {
        LOGGER.debug("Creating status recap count from packet data.");

        StatTableDTO exchangeRecap = new StatTableDTO(SystemStatus.EXCHANGE.getStatus());
        StatTableDTO returnRecap = new StatTableDTO(SystemStatus.RETURN.getStatus());
        StatTableDTO payedRecap = new StatTableDTO(SystemStatus.PAID.getStatus());
        StatTableDTO oosRecap = new StatTableDTO(SystemStatus.OOS.getStatus());
        StatTableDTO outRecap = new StatTableDTO("Sortie");
        StatTableDTO allRecap = new StatTableDTO("All");
        StatTableDTO progressRecap = new StatTableDTO("En Cours");

        // Update recap stats
        for (PacketsStatCountDTO dayStat : existingPackets) {
            updateRecapStats(exchangeRecap, dayStat.getCountExchange());
            updateRecapStats(outRecap, dayStat.getCountOut());
            updateRecapStats(allRecap, dayStat.getCountAll());
            updateRecapStats(payedRecap, dayStat.getCountPayed());
            updateRecapStats(returnRecap, dayStat.getCountReturn());
            updateRecapStats(oosRecap, dayStat.getCountOos());
            updateRecapStats(progressRecap, dayStat.getCountProgress());
        }

        // Finalize recap stats
        finalizeRecapStats(exchangeRecap, datesSize, payedRecap.getPayed());
        finalizeRecapStats(outRecap, datesSize, allRecap.getPayed());
        finalizeRecapStats(allRecap, datesSize, allRecap.getPayed());
        finalizeRecapStats(payedRecap, datesSize, outRecap.getPayed());
        finalizeRecapStats(returnRecap, datesSize, outRecap.getPayed());
        finalizeRecapStats(oosRecap, datesSize, allRecap.getPayed());
        finalizeRecapStats(progressRecap, datesSize, allRecap.getPayed()); // No percentage for progressRecap

        LOGGER.debug("Status recap count created successfully.");
        return Arrays.asList(exchangeRecap, returnRecap, payedRecap, outRecap, progressRecap, oosRecap, allRecap);
    }

    // For packets chart
    private void updateRecapStats(StatTableDTO recap, long count) {
        LOGGER.debug("Updating recap stats. Current min: {}, Current max: {}, Current payed: {}", recap.getMin(), recap.getMax(), recap.getPayed());
        recap.setMin(Math.min(count, recap.getMin()));
        recap.setMax(Math.max(count, recap.getMax()));
        recap.setPayed(count + recap.getPayed());
        LOGGER.debug("Updated recap stats. New min: {}, New max: {}, New payed: {}", recap.getMin(), recap.getMax(), recap.getPayed());
    }

    // For packets chart
    private void finalizeRecapStats(StatTableDTO recap, int uniqueDatesSize, double total) {
        if (uniqueDatesSize == 0) uniqueDatesSize = 1;
        double avg = recap.getPayed() / uniqueDatesSize;
        double percentage = total == 0 ? 0 : Math.round(recap.getPayed() * 10000.0 / total) / 100.0;

        recap.setAvg((long) avg);
        recap.setPer(percentage);

        LOGGER.debug("Finalizing recap stats. Avg: {}, Percentage: {}", avg, percentage);
    }

    public static Map<String, List<?>> getUnique(List<ProductsDayCountDTO> productsList) {
        LOGGER.info("Extracting unique attributes from products list. Size: {}", productsList.size());

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();
        List<Size> uniqueSizes = new ArrayList<>();
        List<String> uniqueProductRefs = new ArrayList<>();
        List<String> uniqueModelNames = new ArrayList<>();
        List<Long> uniqueModelIds = new ArrayList<>();
        List<Long> uniqueProductsIds = new ArrayList<>();

        for (ProductsDayCountDTO product : productsList) {
            Date packetDate = product.getDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }

            Color color = product.getColor();
            if (color != null && !uniqueColors.contains(color)) {
                uniqueColors.add(color);
            }

            String modelName = product.getModelName();
            if (!uniqueModelNames.contains(modelName)) {
                uniqueModelNames.add(modelName);
                uniqueModelIds.add(product.getModelId());
            }

            if (product.getSize() != null) {
                Size size = product.getSize();
                if (!uniqueSizes.contains(size)) {
                    uniqueSizes.add(size);
                }
            }

            if (product.getId() != null) {
                long id = product.getId();
                if (!uniqueProductsIds.contains(id)) {
                    uniqueProductsIds.add(id);
                }
            }

            if (product.getColor() != null) {
                String productRef = product.getColor().getName() + " " + product.getSize().getReference();
                if (!uniqueProductRefs.contains(productRef)) {
                    uniqueProductRefs.add(productRef);
                }
            }
        }

        LOGGER.info("Unique attributes extracted. Dates: {}, Colors: {}, Sizes: {}, Product Refs: {}, Model Names: {}, Model Ids: {}, Product Ids: {}",
                uniqueDates.size(), uniqueColors.size(), uniqueSizes.size(), uniqueProductRefs.size(), uniqueModelNames.size(), uniqueModelIds.size(), uniqueProductsIds.size());

        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueColors", uniqueColors);
        uniqueAttributes.put("uniqueModelNames", uniqueModelNames);
        uniqueAttributes.put("uniqueModelIds", uniqueModelIds);
        uniqueAttributes.put("uniqueSizes", uniqueSizes);
        uniqueAttributes.put("uniqueProductRefs", uniqueProductRefs);
        uniqueAttributes.put("uniqueProductIds", uniqueProductsIds);

        return uniqueAttributes;
    }

    public Map<String, List<?>> getUniqueColors(List<ColorsDayCountDTO> productsList) {
        LOGGER.info("Extracting unique colors and dates from products list. Size: {}", productsList.size());

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();

        for (ColorsDayCountDTO product : productsList) {
            Date packetDate = product.getDate();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
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
        LOGGER.info("Extracting unique offers and dates from products list. Size: {}", productsList.size());

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<OfferDTO> uniqueOffers = new ArrayList<>();
        List<Long> uniqueOffersIds = new ArrayList<>();

        for (OffersDayCountDTO product : productsList) {
            Date packetDate = product.getDate();
            Offer offer = product.getOffer();
            if (!uniqueDates.contains(packetDate)) {
                uniqueDates.add(packetDate);
            }
            if (offer != null && !uniqueOffersIds.contains(offer.getId())) {
                uniqueOffersIds.add(offer.getId());
                uniqueOffers.add(new OfferDTO(offer));
            }
        }

        LOGGER.info("Unique attributes extracted. Offers: {}, Dates: {}", uniqueOffers.size(), uniqueDates.size());

        uniqueAttributes.put("uniqueOffers", uniqueOffers);
        uniqueAttributes.put("uniqueDates", uniqueDates);

        return uniqueAttributes;
    }

    private Map<String, List<?>> getUniqueValues(List<ModelDayCountDTO> existingProductsPacket) {
        LOGGER.info("Extracting unique values from model day count list. Size: {}", existingProductsPacket.size());

        Map<String, List<?>> uniqueValues = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<Color> uniqueColors = new ArrayList<>();
        List<Size> uniqueSizes = new ArrayList<>();
        List<String> uniqueProductRefs = new ArrayList<>();
        List<Long> uniqueProductIds = new ArrayList<>();

        for (ModelDayCountDTO product : existingProductsPacket) {
            if (product.getDate() != null && !uniqueDates.contains(product.getDate())) uniqueDates.add(product.getDate());
            if (product.getColor() != null && !uniqueColors.contains(product.getColor())) uniqueColors.add(product.getColor());
            if (product.getSize() != null && !uniqueSizes.contains(product.getSize())) uniqueSizes.add(product.getSize());
            String colorAndSize = product.getColor().getName() + " " + product.getSize().getReference();
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
        LOGGER.info("Generating sold model chart for Model ID: {}, Date range: {} - {}", modelId, beginDate, endDate);

        // Fetch existing product packets
        List<ModelDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(modelId, beginDate, endDate);
        LOGGER.debug("Fetched {} product packets for Model ID: {}", existingProductsPacket.size(), modelId);

        // Get unique values (dates, colors, sizes, product refs)
        Map<String, List<?>> uniqueValues = getUniqueValues(existingProductsPacket);

        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        LOGGER.debug("Unique dates: {}", uniqueDates);

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

    private List<List<Integer>> getProductCounts(List<ModelDayCountDTO> existingProductsPacket, List<String> uniqueProductRefs, List<Date> uniqueDates) {
        LOGGER.info("Fetching product counts for {} product references across {} unique dates", uniqueProductRefs.size(), uniqueDates.size());

        List<List<Integer>> listProductsCount = new ArrayList<>();
        for (String uniqueProductRef : uniqueProductRefs) {
            LOGGER.debug("Processing product reference: {}", uniqueProductRef);

            List<Integer> countProductsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ModelDayCountDTO productDto : existingProductsPacket) {
                    String colorAndSize = productDto.getColor().getName() + " " + productDto.getSize().getReference();
                    if (productDto.getDate().equals(uniqueDate) && uniqueProductRef.equals(colorAndSize)) {
                        count += productDto.getCountPayed();
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

    private List<List<Integer>> getColorCounts(List<ModelDayCountDTO> existingProductsPacket, List<Color> uniqueColors, List<Date> uniqueDates) {
        LOGGER.info("Fetching color counts for {} unique colors across {} unique dates", uniqueColors.size(), uniqueDates.size());

        List<List<Integer>> listColorsCount = new ArrayList<>();
        for (Color uniqueColor : uniqueColors) {
            LOGGER.debug("Processing color: {}", uniqueColor.getName());

            List<Integer> countColorsList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ModelDayCountDTO product : existingProductsPacket) {
                    if (product.getDate().equals(uniqueDate) && product.getColor().getId().equals(uniqueColor.getId())) {
                        count += product.getCountPayed();
                    }
                }
                countColorsList.add(count);
            }
            listColorsCount.add(countColorsList);
        }

        LOGGER.info("Completed fetching color counts.");
        return listColorsCount;
    }

    private List<List<Integer>> getSizeCounts(List<ModelDayCountDTO> existingProductsPacket, List<Size> uniqueSizes, List<Date> uniqueDates) {
        LOGGER.info("Fetching size counts for {} unique sizes across {} unique dates", uniqueSizes.size(), uniqueDates.size());

        List<List<Integer>> listSizesCount = new ArrayList<>();
        for (Size uniqueSize : uniqueSizes) {
            LOGGER.debug("Processing size: {}", uniqueSize.getReference());

            List<Integer> countSizesList = new ArrayList<>();
            for (Date uniqueDate : uniqueDates) {
                int count = 0;
                for (ModelDayCountDTO product : existingProductsPacket) {
                    if (product.getDate().equals(uniqueDate) && product.getSize().getId().equals(uniqueSize.getId())) {
                        count += product.getCountPayed();
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
        LOGGER.info("Fetching products count by date for Model ID: {}, Date range: {} - {}", modelId, beginDate, endDate);

        List<ProductsDayCountDTO> productCounts = productsPacketRepository.productsCountByDate(modelId, beginDate, endDate);

        LOGGER.debug("Fetched {} product count entries for Model ID: {}", productCounts.size(), modelId);
        return productCounts;
    }

    @Override
    public List<PagesStatCountDTO> findAllPacketsPages(String beginDate, String endDate) {
        LOGGER.info("Fetching packet pages statistics for Date range: {} - {}", beginDate, endDate);

        List<PagesStatCountDTO> pagesStatCounts = packetRepository.findAllPacketsPages(beginDate, endDate);

        LOGGER.debug("Fetched {} packet page statistics entries.", pagesStatCounts.size());
        return pagesStatCounts;
    }

    @Override
    public List<StatesStatCountDTO> findAllPacketsStates(String beginDate, String endDate) {
        LOGGER.info("Fetching packet states statistics for Date range: {} - {}", beginDate, endDate);

        List<StatesStatCountDTO> statesStatCounts = packetRepository.findAllPacketsStates(beginDate, endDate);

        LOGGER.debug("Fetched {} packet state statistics entries.", statesStatCounts.size());
        return statesStatCounts;
    }
}