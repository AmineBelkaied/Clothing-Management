package com.clothing.management.servicesImpl;

import com.clothing.management.dto.*;
import com.clothing.management.dto.StatDTO.*;
import com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO;
import com.clothing.management.dto.StatDTO.ChartDTO.IDNameDTO;
import com.clothing.management.dto.StatDTO.ChartDTO.StatChartDTO;
import com.clothing.management.dto.StatDTO.Response.StatModelsDTO;
import com.clothing.management.dto.StatDTO.Response.StatOffersDTO;
import com.clothing.management.dto.StatDTO.Response.StatPagesDTO;
import com.clothing.management.dto.StatDTO.Response.StatStockDTO;
import com.clothing.management.dto.StatDTO.TableDTO.ModelTableDTO;
import com.clothing.management.dto.StatDTO.TableDTO.OfferTableDTO;
import com.clothing.management.dto.StatDTO.TableDTO.PageTableDTO;
import com.clothing.management.entities.*;
import com.clothing.management.mappers.StatTableMapper;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IPacketRepository;
import com.clothing.management.repository.IProductsPacketRepository;
import com.clothing.management.services.StatService;
import com.clothing.management.utils.SystemStatusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static com.clothing.management.enums.SystemStatus.*;

@Service
public class StatServiceImpl implements StatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatServiceImpl.class);
    private final IProductsPacketRepository productsPacketRepository;
    private final IModelStockHistoryRepository modelStockHistoryRepository;
    private final IPacketRepository packetRepository;
    private final StatTableMapper statTableMapper;

    public StatServiceImpl(IProductsPacketRepository productsPacketRepository,
                           IModelStockHistoryRepository modelStockHistoryRepository,
                           IPacketRepository packetRepository,
                           StatTableMapper statTableMapper) {
        this.productsPacketRepository = productsPacketRepository;
        this.modelStockHistoryRepository = modelStockHistoryRepository;
        this.packetRepository = packetRepository;
        this.statTableMapper = statTableMapper;
    }

    @Override
    public StatPagesDTO statPages(String beginDate, String endDate, Boolean countProgressEnabler) {
        List<String> status = getCountStatus(countProgressEnabler);
        StatPagesDTO dto = new StatPagesDTO();
        List<ChartDTO> chartData = productsPacketRepository.statPagesChart(beginDate, endDate,status);
        dto.setChart(createListsCount(chartData));

        // Create model table
        List<PageTableDTO> pagesStat = productsPacketRepository.statAllPages(beginDate, endDate, status, SystemStatusUtil.getActiveAndConfirmedStatuses(),
                SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getActiveConfirmedDeliveredAndReturnStatuses());
        dto.setPagesStat(pagesStat);
        return dto;
    }

    @Override
    public StatModelsDTO statModels(String beginDate, String endDate, Boolean countProgressEnabler) {
        List<String> status = getCountStatus(countProgressEnabler);
        StatModelsDTO dto = new StatModelsDTO();
        List<ChartDTO> chartData = productsPacketRepository.statModelsChart(beginDate, endDate, status);
        dto.setChart(createListsCount(chartData));

        // Create model table
        List<ModelTableDTO> modelsStat = productsPacketRepository.statAllModels(beginDate, endDate, status, OOS.name(), RETURN.name(),
                RETURN_RECEIVED.name(), SystemStatusUtil.getActiveAndConfirmedStatuses(), SystemStatusUtil.getActiveConfirmedDeliveredReturnAndOosStatuses());
        dto.setModelsStat(modelsStat);

        // Create statValuesDashboard
        ArrayList<ModelStockValueDTO> statValuesDashboard = statValuesTotalDashboard();
        dto.setStatValuesDashboard(statValuesDashboard);
        return dto;
    }

    private List<String> getCountStatus(boolean countProgressEnabler){
        if (countProgressEnabler) {
            return SystemStatusUtil.getActiveConfirmedAndDeliveredAStatuses();
        } else {
            return SystemStatusUtil.getDeliveredStatuses();
        }
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


    public ArrayList<ModelStockValueDTO> statStockTable() {
        // Fetch stock history data
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statValues(new Date());

        // Initialize variables
        long totalQuantity = 0L;
        double totalPurchasePrice = 0.0;
        double totalSellingPrice = 0.0;
        ArrayList<ModelStockValueDTO> modelStockValuesList = new ArrayList<>();
        // Iterate through stock history entries
        for (ModelStockHistory modelStockHistory : statStock) {
            long quantity = modelStockHistory.getQuantity();
            if(quantity>0) {
                Model model = modelStockHistory.getModel();
                double purchasePriceForEntry = model.getPurchasePrice() * quantity;
                double sellingPriceForEntry = purchasePriceForEntry * model.getEarningCoefficient();
                modelStockValuesList.add(new ModelStockValueDTO(model.getName(), quantity, purchasePriceForEntry, sellingPriceForEntry, sellingPriceForEntry - purchasePriceForEntry));
                totalQuantity += quantity;
                totalPurchasePrice += purchasePriceForEntry;
                totalSellingPrice += sellingPriceForEntry;
            }
        }
        modelStockValuesList.add(new ModelStockValueDTO("Total",totalQuantity,totalPurchasePrice,totalSellingPrice,totalSellingPrice - totalPurchasePrice));
        return modelStockValuesList;
    }

    public ArrayList<ModelStockValueDTO> statValuesTotalDashboard() {
        // Fetch stock history data
        List<ModelStockHistory> statStock = modelStockHistoryRepository.statValues(new Date());

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
    public StatStockDTO statStock(String beginDate, String endDate) {

        StatStockDTO dto = new StatStockDTO();
        // Fetch stock history
        List<ChartDTO> statStock = modelStockHistoryRepository.statStockByDate(beginDate, endDate);
        dto.setChart(createListsCount(statStock));

        ArrayList<ModelStockValueDTO> statStockTable = statStockTable();
        dto.setStockTable(statStockTable);
        return dto;
    }

    @Override
    public StatOffersDTO statOffers(String beginDate, String endDate, Boolean countProgressEnabler) {
        List<String> status = getCountStatus(countProgressEnabler);
        StatOffersDTO dto = new StatOffersDTO();

        List<ChartDTO> offersChartData = productsPacketRepository.statOffersChart(beginDate, endDate,status);
        dto.setChart(createListsCount(offersChartData));

        List<OfferTableDTO> offersStat = productsPacketRepository.statOffersTable(beginDate, endDate, status, SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getActiveAndConfirmedStatuses(), SystemStatusUtil.getActiveConfirmedDeliveredAndReturnStatuses());
        dto.setOffersStat(offersStat);

        LOGGER.info("Model chart data generated successfully.");
        return dto;
    }

    private StatChartDTO createListsCount(List<ChartDTO> chartData){
        // Get unique dates and offers
        Map<String, List<?>> uniqueValues = getUniqueItems(chartData);
        List<Date> uniqueDates = (List<Date>) uniqueValues.get("uniqueDates");
        List<IDNameDTO> uniqueOffers = (List<IDNameDTO>) uniqueValues.get("uniqueItemsNames");

        // Initialize offers count list and recap
        List<List<Long>> listOffersCount = new ArrayList<>();

        for (IDNameDTO uniqueOffer : uniqueOffers) {
            List<ChartDTO> miniList = chartData.stream().filter(model-> model.getIdName().getId().equals(uniqueOffer.getId())).toList();
            listOffersCount.add(createChartCountList(uniqueOffer, miniList, uniqueDates));
        }
        // Add Total models count row
        listOffersCount.add(countTotal(uniqueDates, listOffersCount));
        uniqueOffers.add(new IDNameDTO(1000L,"Total"));
        return new StatChartDTO(uniqueDates,uniqueOffers,listOffersCount);
    }

    private List<Long> createChartCountList(IDNameDTO uniqueItem, List<ChartDTO> chartData, List<Date> uniqueDates){

        List<Long> countList = new ArrayList<>();
        for (Date uniqueDate : uniqueDates) {
            long value = 0;
            for (ChartDTO row : chartData) {
                if (row.getDate().equals(uniqueDate) && row.getIdName().getId().equals(uniqueItem.getId())) {
                        value += row.getValue();
                }
            }
            countList.add(value);
        }
        return countList;
    }

    public StatTableDTO createPageTableRecap(List<StatTableDTO> recapCount) {
        StatTableDTO totalRecap = statTableMapper.toStatTableDTO(("Total"));
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

    @Override
    public Map<String, List<?>> statAllColorsChart(String beginDate, String endDate) {

        // Fetch products data by color
        List<ColorsDayCountDTO> existingProductsPacketColor = productsPacketRepository.statAllModelsByColor(beginDate, endDate, SystemStatusUtil.getDeliveredStatuses(),
                SystemStatusUtil.getActiveAndConfirmedStatuses(), SystemStatusUtil.getActiveConfirmedAndDeliveredAStatuses());
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
            return productsPacketRepository.statAllPackets(beginDate, endDate, DELIVERED.name(), PAID.name(), CANCELED.name(), OOS.name(),
                    SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getActiveStatuses(), SystemStatusUtil.getActiveAndDeliveredStatuses());
        } else {
            LOGGER.debug("Fetching packets for specific delivery company: {}", deliveryCompanyName);
            return productsPacketRepository.statAllPackets(beginDate, endDate, deliveryCompanyName, DELIVERED.name(), PAID.name(), CANCELED.name(), OOS.name(),
                    SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getActiveStatuses(), SystemStatusUtil.getActiveAndDeliveredStatuses());
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

        StatTableDTO exchangeRecap = statTableMapper.toStatTableDTO(EXCHANGE.name());
        StatTableDTO returnRecap = statTableMapper.toStatTableDTO(RETURN.name());
        StatTableDTO paidRecap = statTableMapper.toStatTableDTO(PAID.name());
        StatTableDTO receivedRecap = statTableMapper.toStatTableDTO(DELIVERED.name());
        StatTableDTO oosRecap = statTableMapper.toStatTableDTO(OOS.name());
        StatTableDTO outRecap = statTableMapper.toStatTableDTO(RELEASED.getStatus());
        StatTableDTO allRecap = statTableMapper.toStatTableDTO(ALL.getStatus());
        StatTableDTO progressRecap = statTableMapper.toStatTableDTO(IN_PROGRESS.getStatus());

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

    public Map<String, List<?>> getUniqueItems(List<ChartDTO> productsList) {

        Map<String, List<?>> uniqueAttributes = new HashMap<>();
        List<Date> uniqueDates = new ArrayList<>();
        List<IDNameDTO> uniqueModels = new ArrayList<>();
        List<Long> uniqueItemsIds = new ArrayList<>();

        for (ChartDTO product : productsList) {
            if (!uniqueDates.contains(product.getDate())) {
                uniqueDates.add(product.getDate());
            }

            if (!uniqueItemsIds.contains(product.getIdName().getId())) {
                uniqueModels.add(product.getIdName());
                uniqueItemsIds.add(product.getIdName().getId());
            }
        }
        uniqueAttributes.put("uniqueDates", uniqueDates);
        uniqueAttributes.put("uniqueItemsNames", uniqueModels);
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
        List<ProductDayCountDTO> existingProductsPacket = productsPacketRepository.statModelSoldProgress(
                modelId, beginDate, endDate, SystemStatusUtil.getDeliveredStatuses(),
                SystemStatusUtil.getActiveAndConfirmedStatuses(), SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getActiveConfirmedDeliveredAndReturnStatuses());
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
        List<ProductsDayCountDTO> productCounts = productsPacketRepository.productsCountByDate(modelId, beginDate, endDate, OOS.name(), SystemStatusUtil.getReturnStatuses(),
                SystemStatusUtil.getDeliveredStatuses(), SystemStatusUtil.getActiveAndConfirmedStatuses(), SystemStatusUtil.getActiveConfirmedDeliveredReturnAndOosStatuses());

        LOGGER.debug("Fetched {} product count entries for Model ID: {}", productCounts.size(), modelId);
        return productCounts;
    }

    @Override
    public List<StatesStatCountDTO> findAllPacketsStates(String beginDate, String endDate) {
        List<StatesStatCountDTO> statesStatCounts = packetRepository.findAllPacketsStates(beginDate, endDate, SystemStatusUtil.getReturnStatuses(), SystemStatusUtil.getDeliveredStatuses(),
                SystemStatusUtil.getActiveStatuses(), SystemStatusUtil.getActiveDeliveredAndReturnStatuses());

        LOGGER.debug("Fetched {} packet state statistics entries.", statesStatCounts.size());
        return statesStatCounts;
    }
}