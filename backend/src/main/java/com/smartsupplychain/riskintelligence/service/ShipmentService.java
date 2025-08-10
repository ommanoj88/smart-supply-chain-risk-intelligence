package com.smartsupplychain.riskintelligence.service;

import com.smartsupplychain.riskintelligence.dto.ShipmentDto;
import com.smartsupplychain.riskintelligence.enums.ShipmentStatus;
import com.smartsupplychain.riskintelligence.exception.ResourceNotFoundException;
import com.smartsupplychain.riskintelligence.model.Shipment;
import com.smartsupplychain.riskintelligence.model.Supplier;
import com.smartsupplychain.riskintelligence.repository.ShipmentRepository;
import com.smartsupplychain.riskintelligence.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private final Random random = new Random();

    public List<ShipmentDto> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipmentDto getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
        return convertToDto(shipment);
    }

    public List<ShipmentDto> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ShipmentDto> getShipmentsBySupplier(Long supplierId) {
        return shipmentRepository.findBySupplierIdOrderByLastUpdateDesc(supplierId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ShipmentDto> getShipmentsByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ShipmentDto> getShipmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return shipmentRepository.findByEstimatedArrivalBetween(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ShipmentDto> getDelayedShipments() {
        LocalDateTime now = LocalDateTime.now();
        List<ShipmentStatus> activeStatuses = Arrays.asList(
                ShipmentStatus.PENDING, 
                ShipmentStatus.IN_TRANSIT
        );
        return shipmentRepository.findDelayedShipments(now, activeStatuses).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ShipmentDto createShipment(ShipmentDto shipmentDto) {
        Supplier supplier = supplierRepository.findById(shipmentDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", shipmentDto.getSupplierId()));

        Shipment shipment = new Shipment();
        shipment.setSupplier(supplier);
        shipment.setOrigin(shipmentDto.getOrigin());
        shipment.setDestination(shipmentDto.getDestination());
        shipment.setEstimatedArrival(shipmentDto.getEstimatedArrival());
        shipment.setDescription(shipmentDto.getDescription());
        shipment.setStatus(ShipmentStatus.PENDING);
        
        // Generate tracking number
        shipment.setTrackingNumber(generateTrackingNumber());

        shipment = shipmentRepository.save(shipment);
        return convertToDto(shipment);
    }

    public ShipmentDto updateShipment(Long id, ShipmentDto shipmentDto) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

        if (shipmentDto.getStatus() != null) {
            shipment.setStatus(shipmentDto.getStatus());
            
            // Set actual arrival time if delivered
            if (shipmentDto.getStatus() == ShipmentStatus.DELIVERED && shipment.getActualArrival() == null) {
                shipment.setActualArrival(LocalDateTime.now());
            }
        }
        if (shipmentDto.getOrigin() != null) {
            shipment.setOrigin(shipmentDto.getOrigin());
        }
        if (shipmentDto.getDestination() != null) {
            shipment.setDestination(shipmentDto.getDestination());
        }
        if (shipmentDto.getEstimatedArrival() != null) {
            shipment.setEstimatedArrival(shipmentDto.getEstimatedArrival());
        }
        if (shipmentDto.getDescription() != null) {
            shipment.setDescription(shipmentDto.getDescription());
        }
        if (shipmentDto.getTrackingNumber() != null) {
            shipment.setTrackingNumber(shipmentDto.getTrackingNumber());
        }

        shipment = shipmentRepository.save(shipment);
        return convertToDto(shipment);
    }

    public ShipmentDto updateShipmentStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

        shipment.setStatus(status);
        
        if (status == ShipmentStatus.DELIVERED && shipment.getActualArrival() == null) {
            shipment.setActualArrival(LocalDateTime.now());
        }

        shipment = shipmentRepository.save(shipment);
        return convertToDto(shipment);
    }

    public void deleteShipment(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
        shipmentRepository.delete(shipment);
    }

    private String generateTrackingNumber() {
        // Generate a tracking number in format: SCR-YYYYMMDD-XXXXXX
        LocalDateTime now = LocalDateTime.now();
        String datePart = String.format("%04d%02d%02d", 
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String randomPart = String.format("%06d", random.nextInt(1000000));
        return "SCR-" + datePart + "-" + randomPart;
    }

    private ShipmentDto convertToDto(Shipment shipment) {
        ShipmentDto dto = new ShipmentDto();
        dto.setId(shipment.getId());
        dto.setSupplierId(shipment.getSupplier().getId());
        dto.setSupplierName(shipment.getSupplier().getName());
        dto.setStatus(shipment.getStatus());
        dto.setOrigin(shipment.getOrigin());
        dto.setDestination(shipment.getDestination());
        dto.setEstimatedArrival(shipment.getEstimatedArrival());
        dto.setActualArrival(shipment.getActualArrival());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setDescription(shipment.getDescription());
        dto.setLastUpdate(shipment.getLastUpdate());
        return dto;
    }
}