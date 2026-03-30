import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { incomeAPI } from '../services/api';
import { ArrowLeft, Trash2, DollarSign, Briefcase, TrendingUp } from 'lucide-react';
import { format } from 'date-fns';
import AnimatedBackground from '../components/AnimatedBackground';

const IncomeHistoryPage = () => {
  const navigate = useNavigate();
  const [incomes, setIncomes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchIncomes();
  }, []);

  const fetchIncomes = async () => {
    try {
      const response = await incomeAPI.list();
      setIncomes(response.data);
    } catch (error) {
      console.error('Error fetching incomes:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this income entry?')) {
      try {
        await incomeAPI.delete(id);
        setIncomes(incomes.filter(inc => inc.id !== id));
        // Notify user to refresh dashboard
        alert('Income deleted successfully. Dashboard will update automatically.');
      } catch (error) {
        alert('Failed to delete income');
      }
    }
  };

  const getTypeIcon = (type) => {
    return type === 'salary' ? Briefcase : TrendingUp;
  };

  const getTypeColor = (type) => {
    return type === 'salary' 
      ? 'from-cyan-500 to-cyan-600' 
      : 'from-green-500 to-green-600';
  };

  const getTypeLabel = (type) => {
    return type === 'salary' ? 'Salary' : 'Extra Income';
  };

  if (loading) {
    return (
      <div className="min-h-screen dashboard-bg flex items-center justify-center">
        <AnimatedBackground />
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
          className="spinner relative z-10"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen dashboard-bg relative">
      <AnimatedBackground />
      
      <div className="container mx-auto max-w-4xl p-6 relative z-10">
        <motion.button 
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => navigate('/dashboard')} 
          className="text-white mb-6 flex items-center gap-2 hover:text-cyan-300 transition"
        >
          <ArrowLeft size={20} /> Back to Dashboard
        </motion.button>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass-card p-8"
        >
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-3xl font-bold text-white gradient-text">Income History</h2>
            <div className="text-gray-400">
              {incomes.length} {incomes.length === 1 ? 'transaction' : 'transactions'}
            </div>
          </div>

          {incomes.length === 0 ? (
            <div className="text-center py-16">
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: "spring" }}
              >
                <DollarSign className="mx-auto text-gray-500 mb-4" size={64} />
              </motion.div>
              <p className="text-gray-400 text-lg mb-4">No income records found</p>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => navigate('/add-income')}
                className="btn-premium"
              >
                Add Your First Income
              </motion.button>
            </div>
          ) : (
            <div className="space-y-3">
              {incomes.map((income, index) => {
                const Icon = getTypeIcon(income.type);
                return (
                  <motion.div
                    key={income.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.05 }}
                    whileHover={{ scale: 1.02, x: 5 }}
                    className="bg-white/5 rounded-xl p-4 flex items-center justify-between hover:bg-white/10 transition border border-white/10 cursor-pointer"
                  >
                    <div className="flex items-center gap-4 flex-1">
                      <div className={`w-14 h-14 rounded-xl bg-gradient-to-br ${getTypeColor(income.type)} flex items-center justify-center shadow-lg`}>
                        <Icon className="text-white" size={24} />
                      </div>
                      <div className="flex-1">
                        <h3 className="text-white font-semibold text-lg">{getTypeLabel(income.type)}</h3>
                        <p className="text-gray-400 text-sm">{income.description || 'No description'}</p>
                        <p className="text-gray-500 text-xs mt-1">{format(new Date(income.date), 'MMM dd, yyyy')}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-2xl font-bold text-green-400">+₹{parseFloat(income.amount).toFixed(2)}</span>
                      <motion.button
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => handleDelete(income.id)}
                        className="text-red-400 hover:text-red-300 transition p-2 hover:bg-red-500/20 rounded-lg"
                      >
                        <Trash2 size={20} />
                      </motion.button>
                    </div>
                  </motion.div>
                );
              })}
            </div>
          )}
        </motion.div>
      </div>
    </div>
  );
};

export default IncomeHistoryPage;
