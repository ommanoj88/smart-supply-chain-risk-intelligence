"""
Smart Supply Chain ML Service
AI/ML microservice for predictive analytics and risk intelligence
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import pandas as pd
from datetime import datetime, timedelta
import logging
import os
from typing import Dict, List, Optional
import joblib
from sklearn.ensemble import RandomForestRegressor, IsolationForest
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_absolute_error
import warnings
warnings.filterwarnings('ignore')

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

class SupplyChainMLPredictor:
    """Main ML prediction engine for supply chain risk intelligence"""
    
    def __init__(self):
        self.delay_model = None
        self.anomaly_detector = None
        self.scaler = StandardScaler()
        self.is_trained = False
        self._initialize_models()
    
    def _initialize_models(self):
        """Initialize ML models with default configurations"""
        try:
            # Delay prediction model
            self.delay_model = RandomForestRegressor(
                n_estimators=100,
                max_depth=10,
                random_state=42,
                n_jobs=-1
            )
            
            # Anomaly detection model
            self.anomaly_detector = IsolationForest(
                contamination=0.1,
                random_state=42,
                n_jobs=-1
            )
            
            logger.info("ML models initialized successfully")
        except Exception as e:
            logger.error(f"Error initializing models: {str(e)}")
            raise
    
    def train_delay_prediction_model(self, training_data: List[Dict]) -> Dict:
        """Train the delay prediction model with historical shipment data"""
        try:
            if not training_data:
                return {"error": "No training data provided", "success": False}
            
            # Convert to DataFrame
            df = pd.DataFrame(training_data)
            
            # Feature engineering
            features = self._extract_features(df)
            
            if 'actual_delay_hours' not in df.columns:
                return {"error": "Missing target variable 'actual_delay_hours'", "success": False}
            
            # Prepare training data
            X = features
            y = df['actual_delay_hours'].values
            
            # Scale features
            X_scaled = self.scaler.fit_transform(X)
            
            # Train model
            self.delay_model.fit(X_scaled, y)
            self.is_trained = True
            
            # Calculate training metrics
            y_pred = self.delay_model.predict(X_scaled)
            mae = mean_absolute_error(y, y_pred)
            
            logger.info(f"Delay prediction model trained successfully. MAE: {mae:.2f} hours")
            
            return {
                "success": True,
                "mae": float(mae),
                "features_count": X.shape[1],
                "samples_count": X.shape[0],
                "model_type": "RandomForestRegressor"
            }
            
        except Exception as e:
            logger.error(f"Error training delay prediction model: {str(e)}")
            return {"error": str(e), "success": False}
    
    def predict_shipment_delay(self, shipment_data: Dict) -> Dict:
        """Predict delay for a specific shipment"""
        try:
            if not self.is_trained:
                # Use heuristic model if not trained
                return self._heuristic_delay_prediction(shipment_data)
            
            # Extract features
            features = self._extract_features(pd.DataFrame([shipment_data]))
            
            # Scale features
            features_scaled = self.scaler.transform(features)
            
            # Predict delay
            predicted_delay = self.delay_model.predict(features_scaled)[0]
            confidence = self._calculate_prediction_confidence(features_scaled)
            
            # Risk categorization
            risk_level = self._categorize_risk(predicted_delay)
            
            return {
                "predicted_delay_hours": float(predicted_delay),
                "confidence_score": float(confidence),
                "risk_level": risk_level,
                "recommendations": self._get_delay_recommendations(predicted_delay, shipment_data)
            }
            
        except Exception as e:
            logger.error(f"Error predicting shipment delay: {str(e)}")
            return {"error": str(e)}
    
    def detect_supplier_anomalies(self, supplier_performance_data: List[Dict]) -> Dict:
        """Detect anomalies in supplier performance"""
        try:
            if not supplier_performance_data:
                return {"anomalies": [], "total_suppliers": 0}
            
            df = pd.DataFrame(supplier_performance_data)
            
            # Extract performance features
            performance_features = self._extract_performance_features(df)
            
            # Detect anomalies
            anomaly_scores = self.anomaly_detector.fit_predict(performance_features)
            
            # Identify anomalous suppliers
            anomalies = []
            for idx, score in enumerate(anomaly_scores):
                if score == -1:  # Anomaly detected
                    supplier_data = supplier_performance_data[idx]
                    anomaly_details = self._analyze_anomaly(supplier_data, performance_features.iloc[idx])
                    anomalies.append({
                        "supplier_id": supplier_data.get("supplier_id"),
                        "supplier_name": supplier_data.get("supplier_name"),
                        "anomaly_type": anomaly_details["type"],
                        "severity": anomaly_details["severity"],
                        "description": anomaly_details["description"],
                        "recommendations": anomaly_details["recommendations"]
                    })
            
            return {
                "anomalies": anomalies,
                "total_suppliers": len(supplier_performance_data),
                "anomaly_rate": len(anomalies) / len(supplier_performance_data),
                "timestamp": datetime.now().isoformat()
            }
            
        except Exception as e:
            logger.error(f"Error detecting supplier anomalies: {str(e)}")
            return {"error": str(e)}
    
    def _extract_features(self, df: pd.DataFrame) -> pd.DataFrame:
        """Extract features for ML models"""
        features = pd.DataFrame()
        
        # Distance-based features
        features['distance_km'] = df.get('distance_km', 1000)  # Default distance
        
        # Carrier features (one-hot encoded)
        carriers = ['DHL', 'FedEx', 'UPS', 'Maersk', 'MSC']
        for carrier in carriers:
            features[f'carrier_{carrier}'] = (df.get('carrier', 'Unknown') == carrier).astype(int)
        
        # Route complexity
        features['route_complexity'] = df.get('route_complexity', 3)  # 1-5 scale
        
        # Weather risk
        features['weather_risk'] = df.get('weather_risk', 2)  # 1-5 scale
        
        # Priority level
        features['priority_level'] = df.get('priority_level', 2)  # 1-3 scale
        
        # Supplier risk score
        features['supplier_risk_score'] = df.get('supplier_risk_score', 50)  # 0-100 scale
        
        # Season/time features
        if 'created_date' in df.columns:
            df['created_date'] = pd.to_datetime(df['created_date'])
            features['month'] = df['created_date'].dt.month
            features['day_of_week'] = df['created_date'].dt.dayofweek
        else:
            features['month'] = 6  # Default to June
            features['day_of_week'] = 2  # Default to Tuesday
        
        return features
    
    def _extract_performance_features(self, df: pd.DataFrame) -> pd.DataFrame:
        """Extract performance features for anomaly detection"""
        features = pd.DataFrame()
        
        # Performance metrics
        features['on_time_delivery_rate'] = df.get('on_time_delivery_rate', 85.0)
        features['quality_score'] = df.get('quality_score', 8.5)
        features['cost_variance'] = df.get('cost_variance', 5.0)
        features['communication_score'] = df.get('communication_score', 8.0)
        features['compliance_score'] = df.get('compliance_score', 9.0)
        
        # Volume metrics
        features['order_volume'] = df.get('order_volume', 100)
        features['avg_order_value'] = df.get('avg_order_value', 10000)
        
        # Relationship metrics
        features['partnership_duration_months'] = df.get('partnership_duration_months', 24)
        features['dispute_count'] = df.get('dispute_count', 0)
        
        return features
    
    def _heuristic_delay_prediction(self, shipment_data: Dict) -> Dict:
        """Fallback heuristic prediction when ML model is not trained"""
        base_delay = 0
        
        # Distance factor
        distance = shipment_data.get('distance_km', 1000)
        base_delay += distance * 0.01  # 0.01 hours per km
        
        # Carrier factor
        carrier_delays = {
            'DHL': 2, 'FedEx': 1.5, 'UPS': 1.8,
            'Maersk': 24, 'MSC': 36  # Ocean freight has higher delays
        }
        carrier = shipment_data.get('carrier', 'UPS')
        base_delay += carrier_delays.get(carrier, 2)
        
        # Risk factors
        weather_risk = shipment_data.get('weather_risk', 2)
        route_complexity = shipment_data.get('route_complexity', 3)
        base_delay += (weather_risk + route_complexity) * 2
        
        # Add some randomness for realism
        predicted_delay = base_delay * (0.8 + np.random.random() * 0.4)
        
        return {
            "predicted_delay_hours": float(predicted_delay),
            "confidence_score": 0.7,  # Lower confidence for heuristic
            "risk_level": self._categorize_risk(predicted_delay),
            "model_type": "heuristic",
            "recommendations": self._get_delay_recommendations(predicted_delay, shipment_data)
        }
    
    def _calculate_prediction_confidence(self, features_scaled: np.ndarray) -> float:
        """Calculate prediction confidence based on feature similarity to training data"""
        # Simplified confidence calculation
        # In production, this could use prediction intervals or ensemble variance
        return min(0.95, max(0.5, 0.9 - np.random.random() * 0.2))
    
    def _categorize_risk(self, predicted_delay: float) -> str:
        """Categorize risk level based on predicted delay"""
        if predicted_delay < 4:
            return "LOW"
        elif predicted_delay < 12:
            return "MEDIUM"
        elif predicted_delay < 24:
            return "HIGH"
        else:
            return "CRITICAL"
    
    def _get_delay_recommendations(self, predicted_delay: float, shipment_data: Dict) -> List[str]:
        """Generate recommendations based on predicted delay"""
        recommendations = []
        
        if predicted_delay > 8:
            recommendations.append("Consider expedited shipping option")
            recommendations.append("Notify customer about potential delay")
        
        if predicted_delay > 24:
            recommendations.append("Investigate alternative carriers")
            recommendations.append("Implement contingency plan")
        
        weather_risk = shipment_data.get('weather_risk', 2)
        if weather_risk > 3:
            recommendations.append("Monitor weather conditions closely")
        
        if not recommendations:
            recommendations.append("Shipment on track - monitor regularly")
        
        return recommendations
    
    def _analyze_anomaly(self, supplier_data: Dict, features: pd.Series) -> Dict:
        """Analyze detected anomaly to determine type and severity"""
        anomaly_details = {
            "type": "performance",
            "severity": "medium",
            "description": "Performance deviation detected",
            "recommendations": []
        }
        
        # Analyze specific performance issues
        on_time_rate = supplier_data.get('on_time_delivery_rate', 85.0)
        quality_score = supplier_data.get('quality_score', 8.5)
        
        if on_time_rate < 70:
            anomaly_details["type"] = "delivery_reliability"
            anomaly_details["severity"] = "high"
            anomaly_details["description"] = f"On-time delivery rate critically low: {on_time_rate}%"
            anomaly_details["recommendations"].append("Immediate supplier review required")
            
        elif quality_score < 6:
            anomaly_details["type"] = "quality_issue"
            anomaly_details["severity"] = "high"
            anomaly_details["description"] = f"Quality score below threshold: {quality_score}/10"
            anomaly_details["recommendations"].append("Quality audit recommended")
        
        if not anomaly_details["recommendations"]:
            anomaly_details["recommendations"].append("Monitor supplier performance closely")
        
        return anomaly_details

# Initialize ML predictor
ml_predictor = SupplyChainMLPredictor()

# API Endpoints
@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        "status": "healthy",
        "service": "Smart Supply Chain ML Service",
        "version": "1.0.0",
        "timestamp": datetime.now().isoformat(),
        "ml_model_status": "trained" if ml_predictor.is_trained else "not_trained"
    })

@app.route('/predict/delay', methods=['POST'])
def predict_delay():
    """Predict shipment delay"""
    try:
        shipment_data = request.get_json()
        
        if not shipment_data:
            return jsonify({"error": "No shipment data provided"}), 400
        
        prediction = ml_predictor.predict_shipment_delay(shipment_data)
        return jsonify(prediction)
        
    except Exception as e:
        logger.error(f"Error in predict_delay endpoint: {str(e)}")
        return jsonify({"error": "Internal server error"}), 500

@app.route('/analyze/anomalies', methods=['POST'])
def analyze_anomalies():
    """Detect supplier performance anomalies"""
    try:
        supplier_data = request.get_json()
        
        if not supplier_data or not isinstance(supplier_data, list):
            return jsonify({"error": "Invalid supplier data format"}), 400
        
        analysis = ml_predictor.detect_supplier_anomalies(supplier_data)
        return jsonify(analysis)
        
    except Exception as e:
        logger.error(f"Error in analyze_anomalies endpoint: {str(e)}")
        return jsonify({"error": "Internal server error"}), 500

@app.route('/train/delay-model', methods=['POST'])
def train_delay_model():
    """Train the delay prediction model with historical data"""
    try:
        training_data = request.get_json()
        
        if not training_data or not isinstance(training_data, list):
            return jsonify({"error": "Invalid training data format"}), 400
        
        result = ml_predictor.train_delay_prediction_model(training_data)
        return jsonify(result)
        
    except Exception as e:
        logger.error(f"Error in train_delay_model endpoint: {str(e)}")
        return jsonify({"error": "Internal server error"}), 500

@app.route('/predict/risk-score', methods=['POST'])
def predict_risk_score():
    """Calculate comprehensive risk score for a shipment or supplier"""
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({"error": "No data provided"}), 400
        
        # Calculate comprehensive risk score
        risk_factors = {
            "delay_risk": 0.3,
            "supplier_risk": 0.25,
            "route_risk": 0.2,
            "weather_risk": 0.15,
            "geopolitical_risk": 0.1
        }
        
        # Get individual risk scores
        delay_pred = ml_predictor.predict_shipment_delay(data)
        delay_risk = {"LOW": 10, "MEDIUM": 40, "HIGH": 70, "CRITICAL": 95}.get(
            delay_pred.get("risk_level", "MEDIUM"), 40
        )
        
        supplier_risk = data.get('supplier_risk_score', 50)
        route_risk = data.get('route_complexity', 3) * 20
        weather_risk = data.get('weather_risk', 2) * 20
        geopolitical_risk = data.get('geopolitical_risk', 2) * 20
        
        # Calculate weighted risk score
        total_risk = (
            delay_risk * risk_factors["delay_risk"] +
            supplier_risk * risk_factors["supplier_risk"] +
            route_risk * risk_factors["route_risk"] +
            weather_risk * risk_factors["weather_risk"] +
            geopolitical_risk * risk_factors["geopolitical_risk"]
        )
        
        risk_level = "LOW" if total_risk < 30 else "MEDIUM" if total_risk < 60 else "HIGH" if total_risk < 80 else "CRITICAL"
        
        return jsonify({
            "total_risk_score": round(total_risk, 2),
            "risk_level": risk_level,
            "risk_breakdown": {
                "delay_risk": delay_risk,
                "supplier_risk": supplier_risk,
                "route_risk": route_risk,
                "weather_risk": weather_risk,
                "geopolitical_risk": geopolitical_risk
            },
            "predicted_delay": delay_pred,
            "recommendations": _get_risk_recommendations(total_risk, risk_level)
        })
        
    except Exception as e:
        logger.error(f"Error in predict_risk_score endpoint: {str(e)}")
        return jsonify({"error": "Internal server error"}), 500

def _get_risk_recommendations(risk_score: float, risk_level: str) -> List[str]:
    """Generate risk mitigation recommendations"""
    recommendations = []
    
    if risk_level == "CRITICAL":
        recommendations.extend([
            "Immediate escalation to senior management required",
            "Activate contingency plans and backup suppliers",
            "Consider emergency transportation alternatives"
        ])
    elif risk_level == "HIGH":
        recommendations.extend([
            "Close monitoring and proactive communication needed",
            "Prepare contingency plans",
            "Notify stakeholders of potential risks"
        ])
    elif risk_level == "MEDIUM":
        recommendations.extend([
            "Regular monitoring recommended",
            "Review supplier performance metrics"
        ])
    else:
        recommendations.append("Continue standard monitoring procedures")
    
    return recommendations

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    app.run(host='0.0.0.0', port=port, debug=False)