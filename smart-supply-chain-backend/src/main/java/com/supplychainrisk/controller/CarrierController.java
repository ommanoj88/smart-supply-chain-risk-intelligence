package com.supplychainrisk.controller;

import com.supplychainrisk.dto.CarrierDTO;
import com.supplychainrisk.service.CarrierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carriers")
@CrossOrigin(origins = "*")
public class CarrierController {

    private final CarrierService carrierService;

    @Autowired
    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping
    public ResponseEntity<CarrierDTO> createCarrier(@Valid @RequestBody CarrierDTO carrierDTO) {
        try {
            CarrierDTO createdCarrier = carrierService.createCarrier(carrierDTO);
            return new ResponseEntity<>(createdCarrier, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarrierDTO> updateCarrier(
            @PathVariable Long id,
            @Valid @RequestBody CarrierDTO carrierDTO) {
        try {
            CarrierDTO updatedCarrier = carrierService.updateCarrier(id, carrierDTO);
            return new ResponseEntity<>(updatedCarrier, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierDTO> getCarrierById(@PathVariable Long id) {
        Optional<CarrierDTO> carrier = carrierService.getCarrierById(id);
        return carrier.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CarrierDTO> getCarrierByCode(@PathVariable String code) {
        Optional<CarrierDTO> carrier = carrierService.getCarrierByCode(code);
        return carrier.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CarrierDTO> getCarrierByName(@PathVariable String name) {
        Optional<CarrierDTO> carrier = carrierService.getCarrierByName(name);
        return carrier.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<Page<CarrierDTO>> getAllCarriers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Page<CarrierDTO> carriers = carrierService.getAllCarriers(page, size, sortBy, sortDirection);
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<CarrierDTO>> getActiveCarriers() {
        try {
            List<CarrierDTO> activeCarriers = carrierService.getActiveCarriers();
            return new ResponseEntity<>(activeCarriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<CarrierDTO>> getCarriersByService(@PathVariable String service) {
        try {
            List<CarrierDTO> carriers = carrierService.getCarriersByService(service);
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<CarrierDTO>> getCarriersByCountry(@PathVariable String country) {
        try {
            List<CarrierDTO> carriers = carrierService.getCarriersByCountry(country);
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/{service}/country/{country}")
    public ResponseEntity<List<CarrierDTO>> getCarriersByServiceAndCountry(
            @PathVariable String service,
            @PathVariable String country) {
        try {
            List<CarrierDTO> carriers = carrierService.getCarriersByServiceAndCountry(service, country);
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reliability/{minScore}")
    public ResponseEntity<List<CarrierDTO>> getCarriersByMinReliabilityScore(@PathVariable Integer minScore) {
        try {
            List<CarrierDTO> carriers = carrierService.getCarriersByMinReliabilityScore(minScore);
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-performance")
    public ResponseEntity<List<CarrierDTO>> getCarriersOrderedByPerformance() {
        try {
            List<CarrierDTO> carriers = carrierService.getCarriersOrderedByPerformance();
            return new ResponseEntity<>(carriers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CarrierDTO> updateCarrierStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        try {
            CarrierDTO updatedCarrier = carrierService.updateCarrierStatus(id, isActive);
            return new ResponseEntity<>(updatedCarrier, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/sync")
    public ResponseEntity<CarrierDTO> updateCarrierSyncTime(@PathVariable Long id) {
        try {
            CarrierDTO updatedCarrier = carrierService.updateCarrierSyncTime(id);
            return new ResponseEntity<>(updatedCarrier, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrier(@PathVariable Long id) {
        try {
            carrierService.deleteCarrier(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}