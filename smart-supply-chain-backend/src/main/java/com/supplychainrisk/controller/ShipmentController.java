package com.supplychainrisk.controller;

import com.supplychainrisk.dto.ShipmentDTO;
import com.supplychainrisk.dto.ShipmentTrackingEventDTO;
import com.supplychainrisk.entity.Shipment.ShipmentStatus;
import com.supplychainrisk.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ShipmentDTO> createShipment(
            @Valid @RequestBody ShipmentDTO shipmentDTO,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ShipmentDTO createdShipment = shipmentService.createShipment(shipmentDTO, userEmail);
            return new ResponseEntity<>(createdShipment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentDTO> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentDTO shipmentDTO,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ShipmentDTO updatedShipment = shipmentService.updateShipment(id, shipmentDTO, userEmail);
            return new ResponseEntity<>(updatedShipment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        Optional<ShipmentDTO> shipment = shipmentService.getShipmentById(id);
        return shipment.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ShipmentDTO> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        Optional<ShipmentDTO> shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return shipment.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentDTO>> getAllShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<ShipmentDTO> shipments = shipmentService.getAllShipments(page, size, sortBy, sortDirection);
            return new ResponseEntity<>(shipments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ShipmentDTO>> searchShipments(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<ShipmentDTO> shipments = shipmentService.searchShipments(searchTerm, page, size);
            return new ResponseEntity<>(shipments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ShipmentDTO>> getShipmentsWithFilters(
            @RequestParam(required = false) String carrierName,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<ShipmentDTO> shipments = shipmentService.getShipmentsWithFilters(
                    carrierName, status, supplierId, fromDate, toDate, 
                    page, size, sortBy, sortDirection);
            return new ResponseEntity<>(shipments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/delayed")
    public ResponseEntity<List<ShipmentDTO>> getDelayedShipments() {
        try {
            List<ShipmentDTO> delayedShipments = shipmentService.getDelayedShipments();
            return new ResponseEntity<>(delayedShipments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/high-risk")
    public ResponseEntity<List<ShipmentDTO>> getHighRiskShipments(
            @RequestParam(defaultValue = "70") Integer riskThreshold) {
        try {
            List<ShipmentDTO> highRiskShipments = shipmentService.getHighRiskShipments(riskThreshold);
            return new ResponseEntity<>(highRiskShipments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            ShipmentDTO updatedShipment = shipmentService.updateShipmentStatus(id, status, userEmail);
            return new ResponseEntity<>(updatedShipment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/tracking-events")
    public ResponseEntity<ShipmentTrackingEventDTO> addTrackingEvent(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentTrackingEventDTO eventDTO) {
        try {
            ShipmentTrackingEventDTO createdEvent = shipmentService.addTrackingEvent(id, eventDTO);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/tracking-events")
    public ResponseEntity<List<ShipmentTrackingEventDTO>> getShipmentTrackingEvents(@PathVariable Long id) {
        try {
            List<ShipmentTrackingEventDTO> events = shipmentService.getShipmentTrackingEvents(id);
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tracking-events/exceptions")
    public ResponseEntity<List<ShipmentTrackingEventDTO>> getRecentExceptions() {
        try {
            List<ShipmentTrackingEventDTO> exceptions = shipmentService.getRecentExceptions();
            return new ResponseEntity<>(exceptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        try {
            shipmentService.deleteShipment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<Object[]>> getShipmentStatistics() {
        try {
            List<Object[]> statistics = shipmentService.getShipmentStatistics();
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> getShipmentCountByStatus(@PathVariable ShipmentStatus status) {
        try {
            long count = shipmentService.getShipmentCountByStatus(status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}