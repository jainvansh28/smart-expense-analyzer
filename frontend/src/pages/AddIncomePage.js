import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { incomeAPI } from '../services/api';
import { ArrowLeft, DollarSign, Tag, Calendar, FileText } from 'lucide-react';
import AnimatedBackground from '../components/AnimatedBackground';

const AddIncomePage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    amount: '',
    type: 'salary',
    description: '',
    date: new Date().toISOString().split('T')[0]
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await incomeAPI.add(formData);
      setSuccess(true);
      // Show success message briefly then navigate
      setTimeout(() => {
        navigate('/dashboard', { state: { refresh: true } });
      }, 1000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add income');
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
          <h2 className="text-3xl font-bold text-white mb-6 gradient-text">Add New Income</h2>

          {error && (
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="bg-red-500/20 border border-red-500 text-white p-3 rounded-lg mb-4"
            >
              {error}
            </motion.div>
          )}

          {success && (
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-green-500/20 border border-green-500 text-white p-3 rounded-lg mb-4"
            >
              ✅ Income added successfully!
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="text-white block mb-2 font-semibold">Amount</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <DollarSign size={20} className="text-green-400 mr-2" />
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
              <label className="text-white block mb-2 font-semibold">Type</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 border border-purple-500/30">
                <Tag size={20} className="text-cyan-400 mr-2" />
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  className="bg-transparent text-white outline-none w-full"
                  required
                >
                  <option value="salary" className="bg-purple-900">Monthly Salary</option>
                  <option value="extra" className="bg-purple-900">Extra Income</option>
                </select>
              </div>
            </div>

            <div>
              <label className="text-white block mb-2 font-semibold">Date</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <Calendar size={20} className="text-purple-400 mr-2" />
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
                <FileText size={20} className="text-pink-400 mr-2 mt-1" />
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
              {loading ? 'Adding...' : success ? '✓ Added!' : 'Add Income'}
            </motion.button>
          </form>
        </motion.div>
      </div>
    </div>
  );
};

export default AddIncomePage;
