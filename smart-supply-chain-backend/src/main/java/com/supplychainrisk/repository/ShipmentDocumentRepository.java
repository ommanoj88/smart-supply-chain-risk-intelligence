package com.supplychainrisk.repository;

import com.supplychainrisk.entity.ShipmentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentDocumentRepository extends JpaRepository<ShipmentDocument, Long> {

    List<ShipmentDocument> findByShipmentId(Long shipmentId);

    List<ShipmentDocument> findByDocumentType(ShipmentDocument.DocumentType documentType);

    List<ShipmentDocument> findByShipmentIdAndDocumentType(Long shipmentId, ShipmentDocument.DocumentType documentType);

    List<ShipmentDocument> findByUploadedById(Long uploadedById);
}