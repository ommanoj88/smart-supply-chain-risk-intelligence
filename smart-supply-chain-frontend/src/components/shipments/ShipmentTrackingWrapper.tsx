import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ShipmentTracking from './ShipmentTracking';

/**
 * Wrapper component for ShipmentTracking that handles route parameters
 */
export const ShipmentTrackingWrapper: React.FC = () => {
  const { trackingNumber } = useParams<{ trackingNumber: string }>();
  const navigate = useNavigate();

  const handleClose = () => {
    navigate('/shipments');
  };

  if (!trackingNumber) {
    return <div>Invalid tracking number</div>;
  }

  return (
    <ShipmentTracking 
      trackingNumber={trackingNumber} 
      onClose={handleClose} 
    />
  );
};

export default ShipmentTrackingWrapper;