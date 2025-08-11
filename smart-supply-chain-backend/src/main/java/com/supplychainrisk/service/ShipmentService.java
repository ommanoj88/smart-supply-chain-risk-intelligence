package com.supplychainrisk.service;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.*;
import com.supplychainrisk.entity.Shipment.ShipmentStatus;
import com.supplychainrisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingEventRepository trackingEventRepository;
    private final CarrierRepository carrierRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final ShipmentDocumentRepository shipmentDocumentRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShipmentService(
            ShipmentRepository shipmentRepository,
            ShipmentTrackingEventRepository trackingEventRepository,
            CarrierRepository carrierRepository,
            ShipmentItemRepository shipmentItemRepository,
            ShipmentDocumentRepository shipmentDocumentRepository,
            SupplierRepository supplierRepository,
            UserRepository userRepository) {
        this.shipmentRepository = shipmentRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.carrierRepository = carrierRepository;
        this.shipmentItemRepository = shipmentItemRepository;
        this.shipmentDocumentRepository = shipmentDocumentRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    public ShipmentDTO createShipment(ShipmentDTO shipmentDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shipment shipment = convertToEntity(shipmentDTO);
        shipment.setCreatedBy(user);
        shipment.setUpdatedBy(user);

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Create initial tracking event
        ShipmentTrackingEvent initialEvent = new ShipmentTrackingEvent(
                savedShipment,
                "CREATED",
                "Shipment created in system",
                LocalDateTime.now()
        );
        initialEvent.setEventType(ShipmentTrackingEvent.EventType.PICKUP);
        trackingEventRepository.save(initialEvent);

        return convertToDTO(savedShipment);
    }

    public ShipmentDTO updateShipment(Long id, ShipmentDTO shipmentDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shipment existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));

        updateShipmentFields(existingShipment, shipmentDTO);
        existingShipment.setUpdatedBy(user);

        Shipment savedShipment = shipmentRepository.save(existingShipment);
        return convertToDTO(savedShipment);
    }

    public Optional<ShipmentDTO> getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(this::convertToDTO);
    }

    public Page<ShipmentDTO> getAllShipments(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return shipmentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public Page<ShipmentDTO> searchShipments(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return shipmentRepository.searchShipments(searchTerm, pageable)
                .map(this::convertToDTO);
    }

    public Page<ShipmentDTO> getShipmentsWithFilters(
            String carrierName, ShipmentStatus status, Long supplierId,
            LocalDateTime fromDate, LocalDateTime toDate,
            int page, int size, String sortBy, String sortDirection) {
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return shipmentRepository.findShipmentsWithFilters(
                carrierName, status, supplierId, fromDate, toDate, pageable)
                .map(this::convertToDTO);
    }

    public List<ShipmentDTO> getDelayedShipments() {
        List<Shipment> delayedShipments = shipmentRepository.findDelayedShipments(
                ShipmentStatus.IN_TRANSIT, LocalDateTime.now());
        return delayedShipments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ShipmentDTO> getHighRiskShipments(Integer riskThreshold) {
        List<Shipment> highRiskShipments = shipmentRepository.findHighRiskShipments(riskThreshold);
        return highRiskShipments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ShipmentDTO updateShipmentStatus(Long id, ShipmentStatus newStatus, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));

        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        shipment.setUpdatedBy(user);

        if (newStatus == ShipmentStatus.DELIVERED && shipment.getActualDeliveryDate() == null) {
            shipment.setActualDeliveryDate(LocalDateTime.now());
        }

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Create tracking event for status change
        ShipmentTrackingEvent statusEvent = new ShipmentTrackingEvent(
                savedShipment,
                newStatus.name(),
                "Status updated from " + oldStatus + " to " + newStatus,
                LocalDateTime.now()
        );
        statusEvent.setEventType(getEventTypeForStatus(newStatus));
        trackingEventRepository.save(statusEvent);

        return convertToDTO(savedShipment);
    }

    public ShipmentTrackingEventDTO addTrackingEvent(Long shipmentId, ShipmentTrackingEventDTO eventDTO) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + shipmentId));

        ShipmentTrackingEvent event = convertTrackingEventToEntity(eventDTO);
        event.setShipment(shipment);

        ShipmentTrackingEvent savedEvent = trackingEventRepository.save(event);
        return convertTrackingEventToDTO(savedEvent);
    }

    public List<ShipmentTrackingEventDTO> getShipmentTrackingEvents(Long shipmentId) {
        List<ShipmentTrackingEvent> events = trackingEventRepository
                .findByShipmentIdOrderByEventTimestampDesc(shipmentId);
        return events.stream()
                .map(this::convertTrackingEventToDTO)
                .collect(Collectors.toList());
    }

    public List<ShipmentTrackingEventDTO> getRecentExceptions() {
        List<ShipmentTrackingEvent> exceptions = trackingEventRepository
                .findByIsExceptionTrueOrderByEventTimestampDesc();
        return exceptions.stream()
                .map(this::convertTrackingEventToDTO)
                .collect(Collectors.toList());
    }

    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new RuntimeException("Shipment not found with id: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    public List<Object[]> getShipmentStatistics() {
        return shipmentRepository.getShipmentCountByCarrier();
    }

    public long getShipmentCountByStatus(ShipmentStatus status) {
        return shipmentRepository.countByStatus(status);
    }

    // Utility methods for conversion
    private Shipment convertToEntity(ShipmentDTO dto) {
        Shipment shipment = new Shipment();
        updateShipmentFields(shipment, dto);
        return shipment;
    }

    private void updateShipmentFields(Shipment shipment, ShipmentDTO dto) {
        shipment.setTrackingNumber(dto.getTrackingNumber());
        shipment.setReferenceNumber(dto.getReferenceNumber());
        shipment.setShipmentType(dto.getShipmentType());
        shipment.setServiceLevel(dto.getServiceLevel());
        shipment.setWeightKg(dto.getWeightKg());
        shipment.setDimensionsLengthCm(dto.getDimensionsLengthCm());
        shipment.setDimensionsWidthCm(dto.getDimensionsWidthCm());
        shipment.setDimensionsHeightCm(dto.getDimensionsHeightCm());
        shipment.setDeclaredValue(dto.getDeclaredValue());
        shipment.setCurrency(dto.getCurrency());

        // Origin fields
        shipment.setOriginName(dto.getOriginName());
        shipment.setOriginAddress(dto.getOriginAddress());
        shipment.setOriginCity(dto.getOriginCity());
        shipment.setOriginState(dto.getOriginState());
        shipment.setOriginCountry(dto.getOriginCountry());
        shipment.setOriginPostalCode(dto.getOriginPostalCode());
        shipment.setOriginLatitude(dto.getOriginLatitude());
        shipment.setOriginLongitude(dto.getOriginLongitude());

        // Destination fields
        shipment.setDestinationName(dto.getDestinationName());
        shipment.setDestinationAddress(dto.getDestinationAddress());
        shipment.setDestinationCity(dto.getDestinationCity());
        shipment.setDestinationState(dto.getDestinationState());
        shipment.setDestinationCountry(dto.getDestinationCountry());
        shipment.setDestinationPostalCode(dto.getDestinationPostalCode());
        shipment.setDestinationLatitude(dto.getDestinationLatitude());
        shipment.setDestinationLongitude(dto.getDestinationLongitude());

        // Carrier fields
        shipment.setCarrierName(dto.getCarrierName());
        shipment.setCarrierServiceCode(dto.getCarrierServiceCode());
        shipment.setCarrierTrackingUrl(dto.getCarrierTrackingUrl());

        // Status and dates
        if (dto.getStatus() != null) {
            shipment.setStatus(dto.getStatus());
        }
        shipment.setSubstatus(dto.getSubstatus());
        shipment.setShipDate(dto.getShipDate());
        shipment.setEstimatedDeliveryDate(dto.getEstimatedDeliveryDate());
        shipment.setActualDeliveryDate(dto.getActualDeliveryDate());
        shipment.setTransitDays(dto.getTransitDays());

        // Risk and cost fields
        shipment.setRiskScore(dto.getRiskScore());
        shipment.setDelayRiskProbability(dto.getDelayRiskProbability());
        shipment.setPredictedDelayHours(dto.getPredictedDelayHours());
        shipment.setOnTimePerformance(dto.getOnTimePerformance());
        shipment.setShippingCost(dto.getShippingCost());
        shipment.setFuelSurcharge(dto.getFuelSurcharge());
        shipment.setTotalCost(dto.getTotalCost());
        shipment.setBilledWeightKg(dto.getBilledWeightKg());
        shipment.setCarbonFootprintKg(dto.getCarbonFootprintKg());

        // Set supplier if provided
        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + dto.getSupplierId()));
            shipment.setSupplier(supplier);
        }
    }

    private ShipmentDTO convertToDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setReferenceNumber(shipment.getReferenceNumber());
        dto.setShipmentType(shipment.getShipmentType());
        dto.setServiceLevel(shipment.getServiceLevel());
        dto.setWeightKg(shipment.getWeightKg());
        dto.setDimensionsLengthCm(shipment.getDimensionsLengthCm());
        dto.setDimensionsWidthCm(shipment.getDimensionsWidthCm());
        dto.setDimensionsHeightCm(shipment.getDimensionsHeightCm());
        dto.setDeclaredValue(shipment.getDeclaredValue());
        dto.setCurrency(shipment.getCurrency());

        // Origin fields
        dto.setOriginName(shipment.getOriginName());
        dto.setOriginAddress(shipment.getOriginAddress());
        dto.setOriginCity(shipment.getOriginCity());
        dto.setOriginState(shipment.getOriginState());
        dto.setOriginCountry(shipment.getOriginCountry());
        dto.setOriginPostalCode(shipment.getOriginPostalCode());
        dto.setOriginLatitude(shipment.getOriginLatitude());
        dto.setOriginLongitude(shipment.getOriginLongitude());

        // Destination fields
        dto.setDestinationName(shipment.getDestinationName());
        dto.setDestinationAddress(shipment.getDestinationAddress());
        dto.setDestinationCity(shipment.getDestinationCity());
        dto.setDestinationState(shipment.getDestinationState());
        dto.setDestinationCountry(shipment.getDestinationCountry());
        dto.setDestinationPostalCode(shipment.getDestinationPostalCode());
        dto.setDestinationLatitude(shipment.getDestinationLatitude());
        dto.setDestinationLongitude(shipment.getDestinationLongitude());

        // Carrier fields
        dto.setCarrierName(shipment.getCarrierName());
        dto.setCarrierServiceCode(shipment.getCarrierServiceCode());
        dto.setCarrierTrackingUrl(shipment.getCarrierTrackingUrl());

        // Status and dates
        dto.setStatus(shipment.getStatus());
        dto.setSubstatus(shipment.getSubstatus());
        dto.setShipDate(shipment.getShipDate());
        dto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        dto.setActualDeliveryDate(shipment.getActualDeliveryDate());
        dto.setTransitDays(shipment.getTransitDays());

        // Risk and cost fields
        dto.setRiskScore(shipment.getRiskScore());
        dto.setDelayRiskProbability(shipment.getDelayRiskProbability());
        dto.setPredictedDelayHours(shipment.getPredictedDelayHours());
        dto.setOnTimePerformance(shipment.getOnTimePerformance());
        dto.setShippingCost(shipment.getShippingCost());
        dto.setFuelSurcharge(shipment.getFuelSurcharge());
        dto.setTotalCost(shipment.getTotalCost());
        dto.setBilledWeightKg(shipment.getBilledWeightKg());
        dto.setCarbonFootprintKg(shipment.getCarbonFootprintKg());

        // Supplier information
        if (shipment.getSupplier() != null) {
            dto.setSupplierId(shipment.getSupplier().getId());
            dto.setSupplierName(shipment.getSupplier().getName());
        }

        // Audit fields
        if (shipment.getCreatedBy() != null) {
            dto.setCreatedById(shipment.getCreatedBy().getId());
            dto.setCreatedByName(shipment.getCreatedBy().getName());
        }
        if (shipment.getUpdatedBy() != null) {
            dto.setUpdatedById(shipment.getUpdatedBy().getId());
            dto.setUpdatedByName(shipment.getUpdatedBy().getName());
        }
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());

        return dto;
    }

    private ShipmentTrackingEvent convertTrackingEventToEntity(ShipmentTrackingEventDTO dto) {
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setEventCode(dto.getEventCode());
        event.setEventDescription(dto.getEventDescription());
        event.setEventTimestamp(dto.getEventTimestamp());
        event.setLocationName(dto.getLocationName());
        event.setLocationCity(dto.getLocationCity());
        event.setLocationState(dto.getLocationState());
        event.setLocationCountry(dto.getLocationCountry());
        event.setLatitude(dto.getLatitude());
        event.setLongitude(dto.getLongitude());
        event.setEventType(dto.getEventType());
        event.setIsException(dto.getIsException());
        event.setExceptionReason(dto.getExceptionReason());
        event.setCarrierEventCode(dto.getCarrierEventCode());
        event.setCarrierRawData(dto.getCarrierRawData());
        return event;
    }

    private ShipmentTrackingEventDTO convertTrackingEventToDTO(ShipmentTrackingEvent event) {
        ShipmentTrackingEventDTO dto = new ShipmentTrackingEventDTO();
        dto.setId(event.getId());
        dto.setShipmentId(event.getShipment().getId());
        dto.setEventCode(event.getEventCode());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventTimestamp(event.getEventTimestamp());
        dto.setLocationName(event.getLocationName());
        dto.setLocationCity(event.getLocationCity());
        dto.setLocationState(event.getLocationState());
        dto.setLocationCountry(event.getLocationCountry());
        dto.setLatitude(event.getLatitude());
        dto.setLongitude(event.getLongitude());
        dto.setEventType(event.getEventType());
        dto.setIsException(event.getIsException());
        dto.setExceptionReason(event.getExceptionReason());
        dto.setCarrierEventCode(event.getCarrierEventCode());
        dto.setCarrierRawData(event.getCarrierRawData());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    private ShipmentTrackingEvent.EventType getEventTypeForStatus(ShipmentStatus status) {
        return switch (status) {
            case CREATED, PICKED_UP -> ShipmentTrackingEvent.EventType.PICKUP;
            case IN_TRANSIT, OUT_FOR_DELIVERY -> ShipmentTrackingEvent.EventType.TRANSIT;
            case DELIVERED -> ShipmentTrackingEvent.EventType.DELIVERY;
            case EXCEPTION -> ShipmentTrackingEvent.EventType.EXCEPTION;
        };
    }
}