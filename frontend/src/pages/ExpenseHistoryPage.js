import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { expenseAPI } from '../services/api';
import { ArrowLeft, Trash2, ShoppingBag, Car, ShoppingCart, FileText, Music, MoreHorizontal, Search, Filter, Calendar, X, AlertTriangle } from 'lucide-react';
import { format } from 'date-fns';
import AnimatedBackground from '../components/AnimatedBackground';

const ExpenseHistoryPage = () => {
  const navigate = useNavigate();
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterCategory, setFilterCategory] = useState('');
  const [filterMonth, setFilterMonth] = useState('');
  const [filteredExpenses, setFilteredExpenses] = useState([]);
  const [selectedAnomaly, setSelectedAnomaly] = useState(null);

  useEffect(() => {
    fetchExpenses();
  }, []);

  useEffect(() => {
    let result = expenses;
    
    // Apply search
    if (searchQuery) {
      result = result.filter(e => 
        (e.description && e.description.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (e.category && e.category.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }
    
    // Apply category filter
    if (filterCategory && filterCategory !== 'all') {
      result = result.filter(e => e.category === filterCategory);
    }
    
    // Apply month filter
    if (filterMonth) {
      result = result.filter(e => {
        const expenseMonth = new Date(e.date).getMonth() + 1;
        return expenseMonth === parseInt(filterMonth);
      });
    }
    
    setFilteredExpenses(result);
  }, [expenses, searchQuery, filterCategory, filterMonth]);

  const fetchExpenses = async () => {
    try {
      const response = await expenseAPI.list();
      setExpenses(response.data);
    } catch (error) {
      console.error('Error fetching expenses:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this expense?')) {
      try {
        await expenseAPI.delete(id);
        setExpenses(expenses.filter(exp => exp.id !== id));
      } catch (error) {
        alert('Failed to delete expense');
      }
    }
  };

  const handleMarkAsNormal = async (id) => {
    try {
      await expenseAPI.removeAnomaly(id);
      // Update the expense in state
      setExpenses(expenses.map(exp => 
        exp.id === id ? { ...exp, isAnomaly: false, anomalyMessage: null } : exp
      ));
      setSelectedAnomaly(null);
    } catch (error) {
      alert('Failed to mark as normal');
    }
  };

  const getCategoryIcon = (category) => {
    const icons = {
      Food: ShoppingBag,
      Travel: Car,
      Shopping: ShoppingCart,
      Bills: FileText,
      Entertainment: Music,
      Other: MoreHorizontal
    };
    return icons[category] || MoreHorizontal;
  };

  const getCategoryColor = (category) => {
    const colors = {
      Food: 'from-purple-500 to-purple-600',
      Travel: 'from-blue-500 to-blue-600',
      Shopping: 'from-pink-500 to-pink-600',
      Bills: 'from-yellow-500 to-yellow-600',
      Entertainment: 'from-green-500 to-green-600',
      Other: 'from-gray-500 to-gray-600'
    };
    return colors[category] || 'from-gray-500 to-gray-600';
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
            <h2 className="text-3xl font-bold text-white gradient-text">Expense History</h2>
            <div className="text-gray-400">
              {filteredExpenses.length} of {expenses.length} {expenses.length === 1 ? 'transaction' : 'transactions'}
            </div>
          </div>

          {/* Search and Filters */}
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="mb-6 space-y-4">
            <div className="grid md:grid-cols-3 gap-4">
              {/* Search */}
              <div>
                <label className="text-white block mb-2 font-semibold text-sm">Search</label>
                <div className="flex items-center bg-white/10 rounded-lg p-3 border border-purple-500/30">
                  <Search size={18} className="text-cyan-400 mr-2" />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="bg-transparent text-white outline-none w-full placeholder-gray-400 text-sm"
                    placeholder="Search expenses..."
                  />
                  {searchQuery && (
                    <X 
                      size={16} 
                      className="text-gray-400 cursor-pointer hover:text-white transition" 
                      onClick={() => setSearchQuery('')}
                    />
                  )}
                </div>
              </div>
              
              {/* Category Filter */}
              <div>
                <label className="text-white block mb-2 font-semibold text-sm">Category</label>
                <div className="flex items-center bg-white/10 rounded-lg p-3 border border-purple-500/30">
                  <Filter size={18} className="text-purple-400 mr-2" />
                  <select
                    value={filterCategory}
                    onChange={(e) => setFilterCategory(e.target.value)}
                    className="bg-transparent text-white outline-none w-full text-sm"
                  >
                    <option value="" className="bg-purple-900">All Categories</option>
                    <option value="Food" className="bg-purple-900">Food</option>
                    <option value="Travel" className="bg-purple-900">Travel</option>
                    <option value="Shopping" className="bg-purple-900">Shopping</option>
                    <option value="Bills" className="bg-purple-900">Bills</option>
                    <option value="Entertainment" className="bg-purple-900">Entertainment</option>
                    <option value="Other" className="bg-purple-900">Other</option>
                  </select>
                </div>
              </div>
              
              {/* Month Filter */}
              <div>
                <label className="text-white block mb-2 font-semibold text-sm">Month</label>
                <div className="flex items-center bg-white/10 rounded-lg p-3 border border-purple-500/30">
                  <Calendar size={18} className="text-pink-400 mr-2" />
                  <select
                    value={filterMonth}
                    onChange={(e) => setFilterMonth(e.target.value)}
                    className="bg-transparent text-white outline-none w-full text-sm"
                  >
                    <option value="" className="bg-purple-900">All Months</option>
                    {[1,2,3,4,5,6,7,8,9,10,11,12].map(m => (
                      <option key={m} value={m} className="bg-purple-900">
                        {new Date(2000, m-1).toLocaleString('default', { month: 'long' })}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
            
            {/* Clear Filters Button */}
            {(searchQuery || filterCategory || filterMonth) && (
              <motion.button
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => {
                  setSearchQuery('');
                  setFilterCategory('');
                  setFilterMonth('');
                }}
                className="bg-white/10 text-white px-4 py-2 rounded-lg hover:bg-white/20 transition flex items-center gap-2 text-sm"
              >
                <X size={16} />
                Clear Filters
              </motion.button>
            )}
          </motion.div>

          {filteredExpenses.length === 0 && expenses.length > 0 ? (
            <div className="text-center py-16">
              <p className="text-gray-400 text-lg">No expenses match your filters</p>
            </div>
          ) : filteredExpenses.length === 0 ? (
            <div className="text-center py-16">
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: "spring" }}
              >
                <ShoppingBag className="mx-auto text-gray-500 mb-4" size={64} />
              </motion.div>
              <p className="text-gray-400 text-lg mb-4">No expenses found</p>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => navigate('/add-expense')}
                className="btn-premium"
              >
                Add Your First Expense
              </motion.button>
            </div>
          ) : (
            <div className="space-y-3">
              {filteredExpenses.map((expense, index) => {
                const Icon = getCategoryIcon(expense.category);
                return (
                  <motion.div
                    key={expense.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.05 }}
                    whileHover={{ scale: 1.02, x: 5 }}
                    className="bg-white/5 rounded-xl p-4 flex items-center justify-between hover:bg-white/10 transition border border-white/10 cursor-pointer"
                  >
                    <div className="flex items-center gap-4 flex-1">
                      <div className={`w-14 h-14 rounded-xl bg-gradient-to-br ${getCategoryColor(expense.category)} flex items-center justify-center shadow-lg`}>
                        <Icon className="text-white" size={24} />
                      </div>
                      <div className="flex-1">
                        <h3 className="text-white font-semibold text-lg">{expense.category}</h3>
                        <p className="text-gray-400 text-sm">{expense.description || 'No description'}</p>
                        <p className="text-gray-500 text-xs mt-1">{format(new Date(expense.date), 'MMM dd, yyyy')}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-2xl font-bold text-white">₹{parseFloat(expense.amount).toFixed(2)}</span>
                      {expense.isAnomaly && (
                        <motion.button
                          whileHover={{ scale: 1.1 }}
                          whileTap={{ scale: 0.9 }}
                          onClick={(e) => {
                            e.stopPropagation();
                            setSelectedAnomaly(expense);
                          }}
                          className="text-yellow-400 hover:text-yellow-300 transition p-2 hover:bg-yellow-500/20 rounded-lg"
                          title="Unusual expense detected"
                        >
                          <AlertTriangle size={20} />
                        </motion.button>
                      )}
                      <motion.button
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(expense.id);
                        }}
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

      {/* Anomaly Modal */}
      <AnimatePresence>
        {selectedAnomaly && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4"
            onClick={() => setSelectedAnomaly(null)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              className="glass-card p-6 max-w-md w-full border-2 border-yellow-500/30"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex items-start gap-3 mb-4">
                <AlertTriangle className="text-yellow-400 flex-shrink-0" size={32} />
                <div className="flex-1">
                  <h3 className="text-2xl font-bold text-white mb-2">Unusual Expense Detected</h3>
                  <p className="text-gray-300 text-sm leading-relaxed">
                    {selectedAnomaly.anomalyMessage || 
                     "This expense is significantly higher than your normal spending pattern."}
                  </p>
                </div>
                <button
                  onClick={() => setSelectedAnomaly(null)}
                  className="text-gray-400 hover:text-white transition"
                >
                  <X size={24} />
                </button>
              </div>

              <div className="bg-white/5 rounded-lg p-4 mb-4 border border-white/10">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-gray-400">Category:</span>
                  <span className="text-white font-semibold">{selectedAnomaly.category}</span>
                </div>
                <div className="flex justify-between items-center mb-2">
                  <span className="text-gray-400">Amount:</span>
                  <span className="text-white font-semibold">₹{parseFloat(selectedAnomaly.amount).toFixed(2)}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-400">Date:</span>
                  <span className="text-white font-semibold">
                    {format(new Date(selectedAnomaly.date), 'MMM dd, yyyy')}
                  </span>
                </div>
              </div>

              <div className="flex gap-3">
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => handleMarkAsNormal(selectedAnomaly.id)}
                  className="flex-1 bg-green-500/20 text-green-400 px-4 py-3 rounded-lg hover:bg-green-500/30 transition border border-green-500/30 font-semibold"
                >
                  Mark as Normal
                </motion.button>
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => setSelectedAnomaly(null)}
                  className="flex-1 bg-white/10 text-white px-4 py-3 rounded-lg hover:bg-white/20 transition border border-white/10 font-semibold"
                >
                  Close
                </motion.button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default ExpenseHistoryPage;
