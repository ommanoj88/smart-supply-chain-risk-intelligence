package com.supplychainrisk.service;

import com.supplychainrisk.dto.CarrierDTO;
import com.supplychainrisk.entity.Carrier;
import com.supplychainrisk.repository.CarrierRepository;
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
public class CarrierService {

    private final CarrierRepository carrierRepository;

    @Autowired
    public CarrierService(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public CarrierDTO createCarrier(CarrierDTO carrierDTO) {
        Carrier carrier = convertToEntity(carrierDTO);
        Carrier savedCarrier = carrierRepository.save(carrier);
        return convertToDTO(savedCarrier);
    }

    public CarrierDTO updateCarrier(Long id, CarrierDTO carrierDTO) {
        Carrier existingCarrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrier not found with id: " + id));

        updateCarrierFields(existingCarrier, carrierDTO);
        Carrier savedCarrier = carrierRepository.save(existingCarrier);
        return convertToDTO(savedCarrier);
    }

    public Optional<CarrierDTO> getCarrierById(Long id) {
        return carrierRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<CarrierDTO> getCarrierByCode(String code) {
        return carrierRepository.findByCode(code)
                .map(this::convertToDTO);
    }

    public Optional<CarrierDTO> getCarrierByName(String name) {
        return carrierRepository.findByName(name)
                .map(this::convertToDTO);
    }

    public Page<CarrierDTO> getAllCarriers(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return carrierRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public List<CarrierDTO> getActiveCarriers() {
        List<Carrier> activeCarriers = carrierRepository.findByIsActiveTrue();
        return activeCarriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CarrierDTO> getCarriersByService(String service) {
        List<Carrier> carriers = carrierRepository.findByServiceOffered(service);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CarrierDTO> getCarriersByCountry(String country) {
        List<Carrier> carriers = carrierRepository.findByCountrySupported(country);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CarrierDTO> getCarriersByServiceAndCountry(String service, String country) {
        List<Carrier> carriers = carrierRepository.findByServiceAndCountry(service, country);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CarrierDTO> getCarriersByMinReliabilityScore(Integer minScore) {
        List<Carrier> carriers = carrierRepository.findByMinReliabilityScore(minScore);
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CarrierDTO> getCarriersOrderedByPerformance() {
        List<Carrier> carriers = carrierRepository.findAllOrderByOnTimePerformance();
        return carriers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CarrierDTO updateCarrierStatus(Long id, Boolean isActive) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrier not found with id: " + id));

        carrier.setIsActive(isActive);
        Carrier savedCarrier = carrierRepository.save(carrier);
        return convertToDTO(savedCarrier);
    }

    public CarrierDTO updateCarrierSyncTime(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrier not found with id: " + id));

        carrier.setLastSyncAt(LocalDateTime.now());
        Carrier savedCarrier = carrierRepository.save(carrier);
        return convertToDTO(savedCarrier);
    }

    public void deleteCarrier(Long id) {
        if (!carrierRepository.existsById(id)) {
            throw new RuntimeException("Carrier not found with id: " + id);
        }
        carrierRepository.deleteById(id);
    }

    // Utility methods for conversion
    private Carrier convertToEntity(CarrierDTO dto) {
        Carrier carrier = new Carrier();
        updateCarrierFields(carrier, dto);
        return carrier;
    }

    private void updateCarrierFields(Carrier carrier, CarrierDTO dto) {
        carrier.setName(dto.getName());
        carrier.setCode(dto.getCode());
        carrier.setApiEndpoint(dto.getApiEndpoint());
        carrier.setWebhookUrl(dto.getWebhookUrl());
        carrier.setServicesOffered(dto.getServicesOffered());
        carrier.setCountriesSupported(dto.getCountriesSupported());
        carrier.setTrackingUrlTemplate(dto.getTrackingUrlTemplate());
        carrier.setAvgDeliveryTimeDays(dto.getAvgDeliveryTimeDays());
        carrier.setOnTimePercentage(dto.getOnTimePercentage());
        carrier.setReliabilityScore(dto.getReliabilityScore());
        if (dto.getIsActive() != null) {
            carrier.setIsActive(dto.getIsActive());
        }
        carrier.setLastSyncAt(dto.getLastSyncAt());
    }

    private CarrierDTO convertToDTO(Carrier carrier) {
        CarrierDTO dto = new CarrierDTO();
        dto.setId(carrier.getId());
        dto.setName(carrier.getName());
        dto.setCode(carrier.getCode());
        dto.setApiEndpoint(carrier.getApiEndpoint());
        dto.setWebhookUrl(carrier.getWebhookUrl());
        dto.setServicesOffered(carrier.getServicesOffered());
        dto.setCountriesSupported(carrier.getCountriesSupported());
        dto.setTrackingUrlTemplate(carrier.getTrackingUrlTemplate());
        dto.setAvgDeliveryTimeDays(carrier.getAvgDeliveryTimeDays());
        dto.setOnTimePercentage(carrier.getOnTimePercentage());
        dto.setReliabilityScore(carrier.getReliabilityScore());
        dto.setIsActive(carrier.getIsActive());
        dto.setLastSyncAt(carrier.getLastSyncAt());
        dto.setCreatedAt(carrier.getCreatedAt());
        dto.setUpdatedAt(carrier.getUpdatedAt());
        return dto;
    }
}