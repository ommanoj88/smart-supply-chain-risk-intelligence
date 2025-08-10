package com.smartsupplychain.riskintelligence.controller;

import com.smartsupplychain.riskintelligence.dto.ApiResponse;
import com.smartsupplychain.riskintelligence.dto.ShipmentDto;
import com.smartsupplychain.riskintelligence.enums.ShipmentStatus;
import com.smartsupplychain.riskintelligence.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getAllShipments() {
        List<ShipmentDto> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDto>> getShipmentById(@PathVariable Long id) {
        ShipmentDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getShipmentsByStatus(@PathVariable ShipmentStatus status) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getShipmentsBySupplier(@PathVariable Long supplierId) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsBySupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getShipmentsByTrackingNumber(@PathVariable String trackingNumber) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/delayed")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getDelayedShipments() {
        List<ShipmentDto> shipments = shipmentService.getDelayedShipments();
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getShipmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<ShipmentDto>> createShipment(@Valid @RequestBody ShipmentDto shipmentDto) {
        ShipmentDto createdShipment = shipmentService.createShipment(shipmentDto);
        return ResponseEntity.ok(ApiResponse.success("Shipment created successfully", createdShipment));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<ShipmentDto>> updateShipment(
            @PathVariable Long id, 
            @Valid @RequestBody ShipmentDto shipmentDto) {
        ShipmentDto updatedShipment = shipmentService.updateShipment(id, shipmentDto);
        return ResponseEntity.ok(ApiResponse.success("Shipment updated successfully", updatedShipment));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<ShipmentDto>> updateShipmentStatus(
            @PathVariable Long id, 
            @RequestParam ShipmentStatus status) {
        ShipmentDto updatedShipment = shipmentService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Shipment status updated", updatedShipment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success("Shipment deleted successfully"));
    }
}