import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { expenseAPI } from '../services/api';
import { ArrowLeft, DollarSign, Tag, Calendar, FileText, AlertTriangle } from 'lucide-react';
import AnimatedBackground from '../components/AnimatedBackground';

const AddExpensePage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    amount: '',
    category: 'Food',
    description: '',
    date: new Date().toISOString().split('T')[0]
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [anomalyAlert, setAnomalyAlert] = useState(null);

  const categories = ['Food', 'Travel', 'Shopping', 'Bills', 'Entertainment', 'Other'];

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setAnomalyAlert(null);
    try {
      const response = await expenseAPI.add(formData);
      console.log('Expense response:', response.data);
      
      // Check for anomaly
      if (response.data.isAnomaly) {
        setAnomalyAlert({
          message: response.data.anomalyMessage,
          averageAmount: response.data.averageAmount,
          currentAmount: response.data.currentAmount,
          multiplier: response.data.multiplier
        });
      }
      
      setSuccess(true);
      // Show success message briefly then navigate
      setTimeout(() => {
        navigate('/dashboard', { state: { refresh: true } });
      }, anomalyAlert ? 3000 : 1000); // Wait longer if anomaly detected
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add expense');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen dashboard-bg relative">
      <AnimatedBackground />
      
      <div className="container mx-auto max-w-2xl p-6 relative z-10">
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
          <h2 className="text-3xl font-bold text-white mb-6 gradient-text">Add New Expense</h2>

          {error && (
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-red-500/20 border border-red-500 text-white p-3 rounded-lg mb-4"
            >
              {error}
            </motion.div>
          )}

          {anomalyAlert && (
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-yellow-500/20 border-2 border-yellow-500 text-white p-4 rounded-lg mb-4"
            >
              <div className="flex items-start gap-3">
                <span className="text-3xl">⚠️</span>
                <div className="flex-1">
                  <h3 className="font-bold text-yellow-300 text-lg mb-2">Unusual Expense Detected!</h3>
                  <p className="text-white mb-2">{anomalyAlert.message}</p>
                  <div className="text-sm text-gray-300 mt-2">
                    <p>• Your usual {formData.category} spending: ₹{anomalyAlert.averageAmount?.toFixed(2)}</p>
                    <p>• This expense: ₹{anomalyAlert.currentAmount?.toFixed(2)}</p>
                    <p>• {anomalyAlert.multiplier?.toFixed(1)}x higher than normal</p>
                  </div>
                </div>
              </div>
            </motion.div>
          )}

          {success && (
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-green-500/20 border border-green-500 text-white p-3 rounded-lg mb-4"
            >
              ✅ Expense added successfully!
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="text-white block mb-2 font-semibold">Amount</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <DollarSign size={20} className="text-pink-400 mr-2" />
                <input
                  type="number"
                  step="0.01"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  className="bg-transparent text-white outline-none w-full placeholder-gray-400"
                  placeholder="0.00"
                  required
                />
              </div>
            </div>

            <div>
              <label className="text-white block mb-2 font-semibold">Category</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 border border-purple-500/30">
                <Tag size={20} className="text-purple-400 mr-2" />
                <select
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  className="bg-transparent text-white outline-none w-full"
                  required
                >
                  {categories.map(cat => (
                    <option key={cat} value={cat} className="bg-purple-900">{cat}</option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="text-white block mb-2 font-semibold">Date</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <Calendar size={20} className="text-cyan-400 mr-2" />
                <input
                  type="date"
                  value={formData.date}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  className="bg-transparent text-white outline-none w-full"
                  required
                />
              </div>
            </div>

            <div>
              <label className="text-white block mb-2 font-semibold">Description (Optional)</label>
              <div className="flex items-start bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <FileText size={20} className="text-green-400 mr-2 mt-1" />
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="bg-transparent text-white outline-none w-full resize-none placeholder-gray-400"
                  placeholder="Add notes..."
                  rows="3"
                />
              </div>
            </div>

            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={loading || success}
              className="w-full btn-premium py-4 text-lg ripple disabled:opacity-50"
            >
              {loading ? 'Adding...' : success ? '✓ Added!' : 'Add Expense'}
            </motion.button>
          </form>
        </motion.div>
      </div>
    </div>
  );
};

export default AddExpensePage;
