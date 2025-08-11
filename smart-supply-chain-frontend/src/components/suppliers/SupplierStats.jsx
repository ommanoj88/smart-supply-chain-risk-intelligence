import React from 'react';
import { motion } from 'framer-motion';
import { 
  Users, 
  AlertTriangle, 
  CheckCircle,
  Shield
} from 'lucide-react';

const SupplierStats = ({ stats, auditRequired, isLoading }) => {
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="card p-6 animate-pulse">
            <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
            <div className="h-8 bg-gray-200 rounded w-1/2 mb-2"></div>
            <div className="h-3 bg-gray-200 rounded w-full"></div>
          </div>
        ))}
      </div>
    );
  }

  const statusStats = stats.countByStatus || {};
  const riskStats = stats.averageRiskScores || {};

  const statCards = [
    {
      title: 'Total Suppliers',
      value: stats.totalSuppliers || 0,
      change: '+5.2%',
      changeType: 'positive',
      icon: Users,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
      description: 'Active supplier relationships'
    },
    {
      title: 'Active Suppliers',
      value: statusStats.ACTIVE || 0,
      change: '+2.1%',
      changeType: 'positive',
      icon: CheckCircle,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
      description: 'Currently operational suppliers'
    },
    {
      title: 'Audit Required',
      value: auditRequired,
      change: auditRequired > 0 ? 'Action needed' : 'Up to date',
      changeType: auditRequired > 0 ? 'negative' : 'neutral',
      icon: AlertTriangle,
      color: auditRequired > 0 ? 'text-red-600' : 'text-gray-600',
      bgColor: auditRequired > 0 ? 'bg-red-100' : 'bg-gray-100',
      description: 'Suppliers requiring audit review'
    },
    {
      title: 'Avg Risk Score',
      value: riskStats.overall ? Math.round(riskStats.overall) : 0,
      change: riskStats.overall <= 40 ? 'Low risk' : riskStats.overall <= 70 ? 'Medium risk' : 'High risk',
      changeType: riskStats.overall <= 40 ? 'positive' : riskStats.overall <= 70 ? 'neutral' : 'negative',
      icon: Shield,
      color: riskStats.overall <= 40 ? 'text-green-600' : riskStats.overall <= 70 ? 'text-yellow-600' : 'text-red-600',
      bgColor: riskStats.overall <= 40 ? 'bg-green-100' : riskStats.overall <= 70 ? 'bg-yellow-100' : 'bg-red-100',
      description: 'Overall supply chain risk'
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {statCards.map((stat, index) => (
        <motion.div
          key={stat.title}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.1 }}
          className="card card-hover p-6"
        >
          <div className="flex items-center justify-between">
            <div className="flex-1">
              <div className="flex items-center space-x-3">
                <div className={`p-2 rounded-fluent ${stat.bgColor}`}>
                  <stat.icon className={`w-5 h-5 ${stat.color}`} />
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                  <p className="text-2xl font-bold text-gray-900">{stat.value.toLocaleString()}</p>
                </div>
              </div>
              
              <div className="mt-4 flex items-center justify-between">
                <span className="text-xs text-gray-500">{stat.description}</span>
                <span className={`text-xs font-medium px-2 py-1 rounded-full ${
                  stat.changeType === 'positive' 
                    ? 'text-green-700 bg-green-100' 
                    : stat.changeType === 'negative'
                    ? 'text-red-700 bg-red-100'
                    : 'text-gray-700 bg-gray-100'
                }`}>
                  {stat.change}
                </span>
              </div>
            </div>
          </div>
        </motion.div>
      ))}
    </div>
  );
};

export default SupplierStats;