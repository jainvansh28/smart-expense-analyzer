import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import { AlertCircle, List, User, LogOut, Wallet, PiggyBank, ArrowUpCircle, ArrowDownCircle, Target, Calendar, CheckCircle, Trophy, Plus, X, History, Trash2 } from 'lucide-react';
import { analyticsAPI, predictionAPI, incomeAPI, budgetAPI, plannedExpenseAPI, goalsAPI, aiAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import AnimatedBackground from '../components/AnimatedBackground';
import AnimatedCounter from '../components/AnimatedCounter';

const DashboardPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const [analytics, setAnalytics] = useState(null);
  const [prediction, setPrediction] = useState(null);
  const [balance, setBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [budgets, setBudgets] = useState([]);
  const [plannedExpenses, setPlannedExpenses] = useState([]);
  const [savingGoals, setSavingGoals] = useState([]);
  const [budgetWarning, setBudgetWarning] = useState(null);
  const [showBudgetWarning, setShowBudgetWarning] = useState(true);
  
  // Modal states
  const [showBudgetModal, setShowBudgetModal] = useState(false);
  const [showPlannedModal, setShowPlannedModal] = useState(false);
  const [showGoalModal, setShowGoalModal] = useState(false);
  const [showAddMoneyModal, setShowAddMoneyModal] = useState(false);
  const [selectedGoal, setSelectedGoal] = useState(null);
  
  // Form states
  const [budgetForm, setBudgetForm] = useState({
    category: 'Food',
    budgetAmount: '',
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear()
  });
  
  const [plannedForm, setPlannedForm] = useState({
    title: '',
    amount: '',
    category: 'Bills',
    dueDay: 1,
    description: ''
  });
  
  const [goalForm, setGoalForm] = useState({
    goalName: '',
    targetAmount: '',
    currentAmount: 0,
    deadline: ''
  });
  
  const [addMoneyAmount, setAddMoneyAmount] = useState('');

  const fetchData = useCallback(async () => {
    try {
      const [analyticsRes, balanceRes, budgetsRes, plannedRes, goalsRes] = await Promise.all([
        analyticsAPI.getMonthly(),
        incomeAPI.getBalance(),
        budgetAPI.getCurrentMonth().catch(() => ({ data: [] })),
        plannedExpenseAPI.getUpcoming().catch(() => ({ data: [] })),
        goalsAPI.getActive().catch(() => ({ data: [] }))
      ]);
      setAnalytics(analyticsRes.data);
      setBalance(balanceRes.data);
      setBudgets(budgetsRes.data || []);
      setPlannedExpenses(plannedRes.data || []);
      setSavingGoals(goalsRes.data || []);
      
      // Fetch budget warning
      try {
        const warningRes = await aiAPI.getBudgetWarning();
        console.log('Budget warning response:', warningRes.data);
        setBudgetWarning(warningRes.data);
      } catch (warningError) {
        console.error('Budget warning error:', warningError);
      }
      
      // Fetch prediction separately
      try {
        const predictionRes = await predictionAPI.getNextMonth();
        console.log('Prediction response:', predictionRes.data);
        setPrediction(predictionRes.data);
      } catch (predError) {
        console.error('Prediction error:', predError);
        // Try to get latest prediction as fallback
        try {
          const latestPrediction = await predictionAPI.getLatest();
          if (latestPrediction && latestPrediction.data) {
            setPrediction(latestPrediction.data);
          }
        } catch (err) {
          console.error('Failed to fetch latest prediction:', err);
        }
      }
      
      // Debug logging
      console.log('Analytics data:', analyticsRes.data);
      console.log('Balance data:', balanceRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
    
    // Check if budget warning was dismissed today
    const dismissed = localStorage.getItem('budgetWarningDismissed');
    const dismissedDate = localStorage.getItem('budgetWarningDismissedDate');
    const today = new Date().toDateString();
    
    if (dismissed === 'true' && dismissedDate === today) {
      setShowBudgetWarning(false);
    } else {
      // Clear old dismissal if it's a new day
      localStorage.removeItem('budgetWarningDismissed');
      localStorage.removeItem('budgetWarningDismissedDate');
      setShowBudgetWarning(true);
    }
  }, [fetchData]);

  // Refresh when returning from add pages
  useEffect(() => {
    if (location.state?.refresh) {
      fetchData();
      // Clear the state to prevent unnecessary refreshes
      window.history.replaceState({}, document.title);
    }
  }, [location, fetchData]);

  const handleDismissBudgetWarning = useCallback(() => {
    setShowBudgetWarning(false);
    localStorage.setItem('budgetWarningDismissed', 'true');
    localStorage.setItem('budgetWarningDismissedDate', new Date().toDateString());
  }, []);

  const generatePrediction = useCallback(async () => {
    try {
      const res = await predictionAPI.getNextMonth();
      console.log('Prediction response:', res.data);
      setPrediction(res.data);
    } catch (error) {
      console.error('Prediction error:', error);
      const errorMessage = error.response?.data?.error || error.message || 'Failed to generate prediction';
      alert(errorMessage);
    }
  }, []);

  const handleMarkPaid = useCallback(async (id) => {
    try {
      await plannedExpenseAPI.markPaid(id);
      fetchData();
    } catch (error) {
      alert('Failed to mark as paid');
    }
  }, [fetchData]);

  const handleBudgetSubmit = useCallback(async (e) => {
    e.preventDefault();
    try {
      await budgetAPI.set(budgetForm);
      setShowBudgetModal(false);
      setBudgetForm({
        category: 'Food',
        budgetAmount: '',
        month: new Date().getMonth() + 1,
        year: new Date().getFullYear()
      });
      fetchData();
    } catch (error) {
      alert('Failed to set budget');
    }
  }, [budgetForm, fetchData]);

  const handlePlannedSubmit = useCallback(async (e) => {
    e.preventDefault();
    try {
      await plannedExpenseAPI.add(plannedForm);
      setShowPlannedModal(false);
      setPlannedForm({
        title: '',
        amount: '',
        category: 'Bills',
        dueDay: 1,
        description: ''
      });
      fetchData();
    } catch (error) {
      alert('Failed to add planned expense');
    }
  }, [plannedForm, fetchData]);

  const handleGoalSubmit = useCallback(async (e) => {
    e.preventDefault();
    try {
      await goalsAPI.add(goalForm);
      setShowGoalModal(false);
      setGoalForm({
        goalName: '',
        targetAmount: '',
        currentAmount: 0,
        deadline: ''
      });
      fetchData();
    } catch (error) {
      alert('Failed to create goal');
    }
  }, [goalForm, fetchData]);

  const handleAddMoney = useCallback(async (e) => {
    e.preventDefault();
    try {
      await goalsAPI.addProgress(selectedGoal.id, parseFloat(addMoneyAmount));
      setShowAddMoneyModal(false);
      setAddMoneyAmount('');
      setSelectedGoal(null);
      fetchData();
    } catch (error) {
      alert('Failed to add money');
    }
  }, [selectedGoal, addMoneyAmount, fetchData]);

  const handleDeleteBudget = useCallback(async (id) => {
    if (window.confirm('Are you sure you want to delete this budget?')) {
      try {
        await budgetAPI.delete(id);
        fetchData();
      } catch (error) {
        alert('Failed to delete budget');
      }
    }
  }, [fetchData]);

  const COLORS = useMemo(() => ['#8B5CF6', '#EC4899', '#F59E0B', '#10B981', '#06B6D4', '#EF4444'], []);
  
  const pieData = useMemo(() => {
    return analytics?.categoryWiseSpending 
      ? Object.entries(analytics.categoryWiseSpending).map(([name, value]) => ({
          name,
          value: parseFloat(value)
        }))
      : [];
  }, [analytics?.categoryWiseSpending]);

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
      
      <nav className="p-6 glass-nav sticky top-0 z-50">
        <div className="container mx-auto flex justify-between items-center">
          <motion.h1 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="text-2xl font-bold neon-text"
          >
            💰 ExpenseAI
          </motion.h1>
          <div className="flex gap-4">
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => navigate('/add-income')} className="btn-premium px-4 py-2 rounded-lg flex items-center gap-2">
              <ArrowUpCircle size={18} /> Income
            </motion.button>
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => navigate('/add-expense')} className="btn-premium px-4 py-2 rounded-lg flex items-center gap-2">
              <ArrowDownCircle size={18} /> Expense
            </motion.button>
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => navigate('/expenses')} className="text-white hover:text-purple-300 flex items-center gap-2 transition" title="Expense History">
              <List size={18} />
            </motion.button>
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => navigate('/incomes')} className="text-white hover:text-cyan-300 flex items-center gap-2 transition" title="Income History">
              <History size={18} />
            </motion.button>
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => navigate('/profile')} className="text-white hover:text-blue-300 flex items-center gap-2 transition">
              <User size={18} />
            </motion.button>
            <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => { logout(); navigate('/'); }} className="text-white hover:text-red-300 flex items-center gap-2 transition">
              <LogOut size={18} />
            </motion.button>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-6 py-8 relative z-10">
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="mb-8">
          <h2 className="text-5xl font-bold text-white mb-2">Welcome back, <span className="gradient-text">{user?.name}</span>! 👋</h2>
          <p className="text-gray-400 text-lg">Here's your financial overview for this month</p>
        </motion.div>

        {/* AI Budget Warning Card */}
        {budgetWarning && showBudgetWarning && (
          <motion.div 
            initial={{ opacity: 0, y: -20 }} 
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ delay: 0.2 }}
            className={`glass-card p-6 mb-8 border-l-4 relative ${
              budgetWarning.warningLevel === 'SAFE' 
                ? 'border-green-500 bg-green-500/5' 
                : budgetWarning.warningLevel === 'MEDIUM' 
                ? 'border-yellow-500 bg-yellow-500/5' 
                : 'border-red-500 bg-red-500/5'
            }`}
          >
            {/* Close Button */}
            <motion.button
              whileHover={{ scale: 1.1, rotate: 90 }}
              whileTap={{ scale: 0.9 }}
              onClick={handleDismissBudgetWarning}
              className="absolute top-4 right-4 p-2 rounded-full bg-white/10 hover:bg-white/20 transition-colors"
              title="Dismiss warning"
            >
              <X className="text-gray-300 hover:text-white" size={20} />
            </motion.button>

            <div className="flex items-start gap-4 pr-10">
              <div className={`p-3 rounded-full ${
                budgetWarning.warningLevel === 'SAFE' 
                  ? 'bg-green-500/20' 
                  : budgetWarning.warningLevel === 'MEDIUM' 
                  ? 'bg-yellow-500/20' 
                  : 'bg-red-500/20'
              }`}>
                <AlertCircle className={`${
                  budgetWarning.warningLevel === 'SAFE' 
                    ? 'text-green-400' 
                    : budgetWarning.warningLevel === 'MEDIUM' 
                    ? 'text-yellow-400' 
                    : 'text-red-400'
                }`} size={28} />
              </div>
              
              <div className="flex-1">
                <h3 className={`text-2xl font-bold mb-2 ${
                  budgetWarning.warningLevel === 'SAFE' 
                    ? 'text-green-400' 
                    : budgetWarning.warningLevel === 'MEDIUM' 
                    ? 'text-yellow-400' 
                    : 'text-red-400'
                }`}>
                  {budgetWarning.warningLevel === 'SAFE' ? '✅ Budget Status: Safe' : 
                   budgetWarning.warningLevel === 'MEDIUM' ? '⚡ Budget Alert' : 
                   '⚠️ High Budget Alert'}
                </h3>
                
                <p className="text-gray-300 text-lg mb-4">{budgetWarning.message}</p>
                
                <div className="grid md:grid-cols-3 gap-4 mb-4">
                  <div className="bg-white/5 rounded-lg p-3">
                    <p className="text-gray-400 text-sm mb-1">Monthly Income</p>
                    <p className="text-white text-xl font-bold">₹{budgetWarning.monthlyIncome?.toFixed(2) || '0.00'}</p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-3">
                    <p className="text-gray-400 text-sm mb-1">Monthly Expenses</p>
                    <p className="text-white text-xl font-bold">₹{budgetWarning.monthlyExpenses?.toFixed(2) || '0.00'}</p>
                  </div>
                  <div className="bg-white/5 rounded-lg p-3">
                    <p className="text-gray-400 text-sm mb-1">Remaining</p>
                    <p className={`text-xl font-bold ${budgetWarning.remainingBudget >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                      ₹{budgetWarning.remainingBudget?.toFixed(2) || '0.00'}
                    </p>
                  </div>
                </div>
                
                <div className="mb-2">
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-gray-400 text-sm">Budget Used</span>
                    <span className={`text-lg font-bold ${
                      budgetWarning.warningLevel === 'SAFE' 
                        ? 'text-green-400' 
                        : budgetWarning.warningLevel === 'MEDIUM' 
                        ? 'text-yellow-400' 
                        : 'text-red-400'
                    }`}>
                      {budgetWarning.spendingPercentage}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-700/50 rounded-full h-3 overflow-hidden">
                    <motion.div 
                      className={`h-full rounded-full ${
                        budgetWarning.warningLevel === 'SAFE' 
                          ? 'bg-gradient-to-r from-green-500 to-green-400' 
                          : budgetWarning.warningLevel === 'MEDIUM' 
                          ? 'bg-gradient-to-r from-yellow-500 to-yellow-400' 
                          : 'bg-gradient-to-r from-red-500 to-red-400'
                      }`}
                      initial={{ width: 0 }}
                      animate={{ width: `${Math.min(budgetWarning.spendingPercentage, 100)}%` }}
                      transition={{ duration: 1, ease: "easeOut" }}
                    />
                  </div>
                </div>
                
                {budgetWarning.topCategory && budgetWarning.topCategory !== 'Unknown' && (
                  <div className="mt-3 text-gray-400 text-sm">
                    💡 Top spending category: <span className="text-white font-semibold">{budgetWarning.topCategory}</span>
                  </div>
                )}
              </div>
            </div>
          </motion.div>
        )}

        {/* Balance Cards */}
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 0.1 }}
            className="glass-card p-6 card-shine">
            <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-cyan-500/20 to-transparent rounded-full -mr-16 -mt-16" />
            <div className="flex items-center justify-between mb-4 relative z-10">
              <ArrowUpCircle className="text-cyan-400" size={36} />
              <span className="text-white text-xs bg-cyan-500/20 px-3 py-1 rounded-full">Income</span>
            </div>
            <h3 className="text-4xl font-bold text-white mb-2">
              <AnimatedCounter value={balance?.totalIncome || 0} />
            </h3>
            <p className="text-gray-400 text-sm">Total Income</p>
          </motion.div>

          <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 0.2 }}
            className="glass-card p-6 card-shine">
            <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-pink-500/20 to-transparent rounded-full -mr-16 -mt-16" />
            <div className="flex items-center justify-between mb-4 relative z-10">
              <ArrowDownCircle className="text-pink-400" size={36} />
              <span className="text-white text-xs bg-pink-500/20 px-3 py-1 rounded-full">Expenses</span>
            </div>
            <h3 className="text-4xl font-bold text-white mb-2">
              <AnimatedCounter value={balance?.totalExpenses || analytics?.monthlyTotal || 0} />
            </h3>
            <p className="text-gray-400 text-sm">Total Expenses</p>
          </motion.div>

          <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 0.3 }}
            className="glass-card p-6 card-shine pulse-glow">
            <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-green-500/20 to-transparent rounded-full -mr-16 -mt-16" />
            <div className="flex items-center justify-between mb-4 relative z-10">
              <Wallet className="text-green-400" size={36} />
              <span className="text-white text-xs bg-green-500/20 px-3 py-1 rounded-full">Balance</span>
            </div>
            <h3 className="text-4xl font-bold text-white mb-2">
              <AnimatedCounter value={balance?.currentBalance || 0} />
            </h3>
            <p className="text-gray-400 text-sm">Current Balance</p>
          </motion.div>

          <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} transition={{ delay: 0.4 }}
            className="glass-card p-6 card-shine">
            <div className="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-purple-500/20 to-transparent rounded-full -mr-16 -mt-16" />
            <div className="flex items-center justify-between mb-4 relative z-10">
              <PiggyBank className="text-purple-400" size={36} />
              <span className="text-white text-xs bg-purple-500/20 px-3 py-1 rounded-full">Budget</span>
            </div>
            <h3 className="text-4xl font-bold text-white mb-2">
              <AnimatedCounter value={balance?.remainingBudget || 0} />
            </h3>
            <p className="text-gray-400 text-sm">Remaining Budget</p>
          </motion.div>
        </div>

        {/* Budget Progress Bar */}
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}
          className="glass-card p-6 mb-8">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-xl font-bold text-white">Budget Usage</h3>
            <span className="text-3xl font-bold gradient-text">{balance?.budgetUsedPercentage?.toFixed(1) || 0}%</span>
          </div>
          <div className="progress-bar">
            <motion.div 
              className="progress-fill"
              initial={{ width: 0 }}
              animate={{ width: `${Math.min(balance?.budgetUsedPercentage || 0, 100)}%` }}
              transition={{ duration: 1.5, delay: 0.5 }}
            />
          </div>
          <p className="text-gray-400 text-sm mt-3">
            {balance?.budgetUsedPercentage > 90 ? '⚠️ You\'re close to your budget limit!' : 
             balance?.budgetUsedPercentage > 75 ? '⚡ Watch your spending!' : 
             '✅ You\'re doing great!'}
          </p>
        </motion.div>



        <div className="grid lg:grid-cols-2 gap-6 mb-8">
          <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.6 }}
            className="glass-card p-6">
            <h3 className="text-2xl font-bold text-white mb-6">Category Distribution</h3>
            {pieData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie 
                    data={pieData} 
                    cx="50%" 
                    cy="50%" 
                    labelLine={false} 
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={100} 
                    fill="#8884d8" 
                    dataKey="value"
                  >
                    {pieData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip contentStyle={{ background: 'rgba(10, 1, 24, 0.9)', border: '1px solid rgba(139, 92, 246, 0.5)', borderRadius: '12px' }} />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p className="text-gray-400 text-center py-8">No data available</p>
            )}
          </motion.div>

          <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.7 }}
            className="glass-card p-6">
            <h3 className="text-2xl font-bold text-white mb-6">🤖 AI Prediction for Next Month</h3>
            {prediction ? (
              <div className="space-y-4">
                <motion.div 
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.8 }}
                  className="bg-white/5 rounded-lg p-4 hover:bg-white/10 transition border border-purple-500/20"
                >
                  <p className="text-gray-400 text-sm mb-1">Total Estimated Spending</p>
                  <p className="text-3xl font-bold text-white">
                    <AnimatedCounter value={prediction.totalPredictedExpense || prediction.predictedExpense || 0} duration={1500} />
                  </p>
                  {prediction.predictionMonth && (
                    <p className="text-gray-500 text-xs mt-1">
                      For {prediction.predictionMonth} {prediction.predictionYear}
                    </p>
                  )}
                </motion.div>

                {/* Category Predictions */}
                {prediction.categoryPredictions && prediction.categoryPredictions.length > 0 && (
                  <motion.div 
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.9 }}
                    className="bg-white/5 rounded-lg p-4 border border-cyan-500/20"
                  >
                    <p className="text-gray-400 text-sm mb-3">Category-wise Predictions</p>
                    <div className="space-y-3">
                      {prediction.categoryPredictions.map((cat, idx) => (
                        <motion.div 
                          key={idx}
                          initial={{ opacity: 0, x: -10 }}
                          animate={{ opacity: 1, x: 0 }}
                          transition={{ delay: 1 + idx * 0.1 }}
                          className="flex items-center justify-between"
                        >
                          <div className="flex items-center gap-2 flex-1">
                            <span className="text-white font-medium">{cat.category}</span>
                            <div className="flex-1 bg-white/10 rounded-full h-2 mx-2">
                              <motion.div 
                                className="bg-gradient-to-r from-cyan-400 to-purple-500 h-2 rounded-full"
                                initial={{ width: 0 }}
                                animate={{ 
                                  width: `${(cat.predictedAmount / prediction.totalPredictedExpense * 100)}%` 
                                }}
                                transition={{ duration: 1, delay: 1.1 + idx * 0.1 }}
                              />
                            </div>
                          </div>
                          <span className="text-cyan-400 font-semibold">
                            ₹{cat.predictedAmount.toFixed(0)}
                          </span>
                        </motion.div>
                      ))}
                    </div>
                  </motion.div>
                )}

                <motion.div 
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 1.2 }}
                  className="bg-white/5 rounded-lg p-4 hover:bg-white/10 transition border border-pink-500/20"
                >
                  <p className="text-gray-400 text-sm mb-2">Overspending Risk</p>
                  <div className="flex items-center gap-3">
                    <div className="flex-1 bg-white/10 rounded-full h-3">
                      <motion.div 
                        className="bg-gradient-to-r from-yellow-400 via-orange-500 to-red-500 h-3 rounded-full" 
                        initial={{ width: 0 }}
                        animate={{ width: `${prediction.overspendingRiskPercentage}%` }}
                        transition={{ duration: 1.5, delay: 1.3 }}
                      />
                    </div>
                    <span className="text-white font-bold text-lg">{prediction.overspendingRiskPercentage}%</span>
                  </div>
                </motion.div>

                <motion.div 
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 1.4 }}
                  className="bg-white/5 rounded-lg p-4 hover:bg-white/10 transition border border-green-500/20"
                >
                  <p className="text-gray-400 text-sm mb-1">Predicted Savings</p>
                  <p className="text-2xl font-bold text-green-400">
                    <AnimatedCounter value={prediction.savingsPrediction || 0} duration={1500} />
                  </p>
                </motion.div>

                {/* AI Insight */}
                {prediction.insight && (
                  <motion.div 
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 1.5 }}
                    className="bg-gradient-to-r from-purple-500/10 to-pink-500/10 rounded-lg p-4 border border-purple-500/30"
                  >
                    <p className="text-gray-400 text-xs mb-2">💡 AI Insight</p>
                    <p className="text-white text-sm leading-relaxed">{prediction.insight}</p>
                  </motion.div>
                )}
              </div>
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-400 mb-4">No prediction available</p>
                <motion.button 
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={generatePrediction} 
                  className="btn-premium ripple"
                >
                  Generate Prediction
                </motion.button>
              </div>
            )}
          </motion.div>
        </div>

        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.8 }}
          className="glass-card p-6 mb-8">
          <h3 className="text-2xl font-bold text-white mb-6">💡 Smart Suggestions</h3>
          <div className="space-y-3">
            {analytics?.suggestions?.map((suggestion, index) => (
              <motion.div 
                key={index} 
                initial={{ opacity: 0, x: -20 }} 
                animate={{ opacity: 1, x: 0 }} 
                transition={{ delay: 0.9 + index * 0.1 }}
                className="bg-white/5 rounded-lg p-4 flex items-start gap-3 hover:bg-white/10 transition cursor-pointer border border-yellow-500/20"
              >
                <AlertCircle className="text-yellow-400 flex-shrink-0 mt-1" size={20} />
                <p className="text-gray-300">{suggestion}</p>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Category Budgets */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }} 
          animate={{ opacity: 1, y: 0 }} 
          transition={{ delay: 0.9 }}
          className="glass-card p-6 mt-8">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-2xl font-bold text-white flex items-center gap-2">
              <Target className="text-purple-400" size={28} />
              Category Budgets
            </h3>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowBudgetModal(true)}
              className="bg-purple-500/20 text-purple-400 px-4 py-2 rounded-lg hover:bg-purple-500/30 transition flex items-center gap-2 border border-purple-500/30"
            >
              <Plus size={18} />
              Set Budget
            </motion.button>
          </div>
          {budgets && budgets.length > 0 ? (
            <div className="space-y-4">
              {budgets.map((budget, index) => (
                <motion.div 
                  key={budget.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 1 + index * 0.1 }}
                  className="bg-white/5 rounded-lg p-4 border border-white/10">
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-white font-semibold">{budget.category}</span>
                    <div className="flex items-center gap-3">
                      <span className={`text-sm ${budget.isOverBudget ? 'text-red-400' : 'text-green-400'}`}>
                        ₹{budget.spentAmount} / ₹{budget.budgetAmount}
                      </span>
                      <motion.button
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => handleDeleteBudget(budget.id)}
                        className="text-red-400 hover:text-red-300 transition p-1 hover:bg-red-500/20 rounded"
                        title="Delete budget"
                      >
                        <Trash2 size={16} />
                      </motion.button>
                    </div>
                  </div>
                  <div className="progress-bar mb-2">
                    <motion.div 
                      className={`h-full rounded-full ${budget.isOverBudget ? 'bg-gradient-to-r from-red-500 to-pink-500' : 'bg-gradient-to-r from-cyan-500 to-purple-500'}`}
                      initial={{ width: 0 }}
                      animate={{ width: `${Math.min(budget.percentageUsed, 100)}%` }}
                      transition={{ duration: 1, delay: 1.1 + index * 0.1 }}
                    />
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-400">{budget.percentageUsed.toFixed(1)}% used</span>
                    {budget.isOverBudget && (
                      <span className="text-red-400">⚠️ Over by ₹{Math.abs(budget.remainingAmount).toFixed(2)}</span>
                    )}
                    {!budget.isOverBudget && (
                      <span className="text-green-400">₹{budget.remainingAmount.toFixed(2)} remaining</span>
                    )}
                  </div>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-400">No budgets set for this month</p>
              <p className="text-gray-500 text-sm mt-2">Set category budgets to track your spending</p>
            </div>
          )}
        </motion.div>

        {/* Overspending Alerts */}
        {budgets && budgets.some(b => b.isOverBudget) && (
          <motion.div 
            initial={{ opacity: 0, y: 20 }} 
            animate={{ opacity: 1, y: 0 }} 
            transition={{ delay: 1 }}
            className="glass-card p-6 mt-8 border-2 border-red-500/30">
            <h3 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
              <AlertCircle className="text-red-400" size={28} />
              Overspending Alerts
            </h3>
            <div className="space-y-3">
              {budgets.filter(b => b.isOverBudget).map((budget, index) => (
                <motion.div 
                  key={budget.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 1.1 + index * 0.1 }}
                  className="bg-red-500/10 rounded-lg p-4 flex items-start gap-3 border border-red-500/30">
                  <AlertCircle className="text-red-400 flex-shrink-0 mt-1" size={20} />
                  <div>
                    <p className="text-white font-semibold">
                      ⚠️ Warning: You have exceeded your {budget.category} budget by ₹{Math.abs(budget.remainingAmount).toFixed(2)} this month.
                    </p>
                    <p className="text-gray-400 text-sm mt-1">
                      Budget: ₹{budget.budgetAmount} | Spent: ₹{budget.spentAmount}
                    </p>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Planned Expenses */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }} 
          animate={{ opacity: 1, y: 0 }} 
          transition={{ delay: 1.1 }}
          className="glass-card p-6 mt-8">
          <h3 className="text-2xl font-bold text-white mb-6 flex items-center gap-2">
            <Calendar className="text-cyan-400" size={28} />
            Upcoming Payments
          </h3>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setShowPlannedModal(true)}
            className="bg-cyan-500/20 text-cyan-400 px-4 py-2 rounded-lg hover:bg-cyan-500/30 transition flex items-center gap-2 border border-cyan-500/30 mb-4"
          >
            <Plus size={18} />
            Add Planned Expense
          </motion.button>
          {plannedExpenses && plannedExpenses.length > 0 ? (
            <div className="space-y-3">
              {plannedExpenses.map((planned, index) => (
                <motion.div 
                  key={planned.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 1.2 + index * 0.1 }}
                  className="bg-white/5 rounded-lg p-4 flex items-center justify-between border border-white/10 hover:bg-white/10 transition">
                  <div className="flex-1">
                    <h4 className="text-white font-semibold">{planned.title}</h4>
                    <p className="text-gray-400 text-sm">
                      Due on {planned.dueDay}{planned.dueDay === 1 ? 'st' : planned.dueDay === 2 ? 'nd' : planned.dueDay === 3 ? 'rd' : 'th'} of month
                    </p>
                    <p className="text-cyan-400 font-semibold mt-1">₹{planned.amount}</p>
                  </div>
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => handleMarkPaid(planned.id)}
                    className="bg-green-500/20 text-green-400 px-4 py-2 rounded-lg hover:bg-green-500/30 transition flex items-center gap-2 border border-green-500/30"
                  >
                    <CheckCircle size={18} />
                    Mark Paid
                  </motion.button>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-400">No upcoming payments</p>
              <p className="text-gray-500 text-sm mt-2">Add planned expenses like rent or EMI</p>
            </div>
          )}
        </motion.div>

        {/* Saving Goals */}
        <motion.div 
          initial={{ opacity: 0, y: 20 }} 
          animate={{ opacity: 1, y: 0 }} 
          transition={{ delay: 1.2 }}
          className="glass-card p-6 mt-8">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-2xl font-bold text-white flex items-center gap-2">
              <Trophy className="text-yellow-400" size={28} />
              Saving Goals
            </h3>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowGoalModal(true)}
              className="bg-yellow-500/20 text-yellow-400 px-4 py-2 rounded-lg hover:bg-yellow-500/30 transition flex items-center gap-2 border border-yellow-500/30"
            >
              <Plus size={18} />
              Create Goal
            </motion.button>
          </div>
          {savingGoals && savingGoals.length > 0 ? (
            <div className="grid md:grid-cols-2 gap-4">
              {savingGoals.map((goal, index) => {
                const percentage = (goal.currentAmount / goal.targetAmount * 100).toFixed(1);
                return (
                  <motion.div 
                    key={goal.id}
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 1.3 + index * 0.1 }}
                    className="bg-white/5 rounded-lg p-4 border border-white/10">
                    <h4 className="text-white font-semibold mb-2">{goal.goalName}</h4>
                    <p className="text-gray-400 text-sm mb-3">
                      ₹{goal.currentAmount} / ₹{goal.targetAmount}
                    </p>
                    <div className="progress-bar mb-3">
                      <motion.div 
                        className="bg-gradient-to-r from-yellow-400 to-orange-500 h-full rounded-full"
                        initial={{ width: 0 }}
                        animate={{ width: `${Math.min(percentage, 100)}%` }}
                        transition={{ duration: 1, delay: 1.4 + index * 0.1 }}
                      />
                    </div>
                    <div className="flex justify-between items-center">
                      <p className="text-sm text-gray-400">{percentage}% complete</p>
                      <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                        onClick={() => {
                          setSelectedGoal(goal);
                          setShowAddMoneyModal(true);
                        }}
                        className="bg-green-500/20 text-green-400 px-3 py-1 rounded-lg hover:bg-green-500/30 transition text-sm border border-green-500/30"
                      >
                        Add Money
                      </motion.button>
                    </div>
                  </motion.div>
                );
              })}
            </div>
          ) : (
            <div className="text-center py-8">
              <p className="text-gray-400">No active saving goals</p>
              <p className="text-gray-500 text-sm mt-2">Create goals to track your savings progress</p>
            </div>
          )}
        </motion.div>
      </div>

      {/* Budget Modal */}
      {showBudgetModal && (
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4"
          onClick={() => setShowBudgetModal(false)}
        >
          <motion.div 
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="glass-card p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-white">Set Category Budget</h3>
              <button onClick={() => setShowBudgetModal(false)} className="text-gray-400 hover:text-white">
                <X size={24} />
              </button>
            </div>
            <form onSubmit={handleBudgetSubmit} className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-2">Category</label>
                <select 
                  value={budgetForm.category}
                  onChange={(e) => setBudgetForm({...budgetForm, category: e.target.value})}
                  className="input-animated w-full"
                  required
                >
                  <option value="Food">Food</option>
                  <option value="Travel">Travel</option>
                  <option value="Shopping">Shopping</option>
                  <option value="Bills">Bills</option>
                  <option value="Entertainment">Entertainment</option>
                  <option value="Health">Health</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Budget Amount (₹)</label>
                <input 
                  type="number"
                  value={budgetForm.budgetAmount}
                  onChange={(e) => setBudgetForm({...budgetForm, budgetAmount: e.target.value})}
                  className="input-animated w-full"
                  placeholder="5000"
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                className="btn-premium w-full ripple"
              >
                Set Budget
              </motion.button>
            </form>
          </motion.div>
        </motion.div>
      )}

      {/* Planned Expense Modal */}
      {showPlannedModal && (
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4"
          onClick={() => setShowPlannedModal(false)}
        >
          <motion.div 
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="glass-card p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-white">Add Planned Expense</h3>
              <button onClick={() => setShowPlannedModal(false)} className="text-gray-400 hover:text-white">
                <X size={24} />
              </button>
            </div>
            <form onSubmit={handlePlannedSubmit} className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-2">Title</label>
                <input 
                  type="text"
                  value={plannedForm.title}
                  onChange={(e) => setPlannedForm({...plannedForm, title: e.target.value})}
                  className="input-animated w-full"
                  placeholder="Rent / EMI / Subscription"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Amount (₹)</label>
                <input 
                  type="number"
                  value={plannedForm.amount}
                  onChange={(e) => setPlannedForm({...plannedForm, amount: e.target.value})}
                  className="input-animated w-full"
                  placeholder="8000"
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Category</label>
                <select 
                  value={plannedForm.category}
                  onChange={(e) => setPlannedForm({...plannedForm, category: e.target.value})}
                  className="input-animated w-full"
                  required
                >
                  <option value="Bills">Bills</option>
                  <option value="Food">Food</option>
                  <option value="Travel">Travel</option>
                  <option value="Shopping">Shopping</option>
                  <option value="Entertainment">Entertainment</option>
                  <option value="Health">Health</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Due Day (1-31)</label>
                <input 
                  type="number"
                  value={plannedForm.dueDay}
                  onChange={(e) => setPlannedForm({...plannedForm, dueDay: parseInt(e.target.value)})}
                  className="input-animated w-full"
                  placeholder="5"
                  required
                  min="1"
                  max="31"
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Description (Optional)</label>
                <textarea 
                  value={plannedForm.description}
                  onChange={(e) => setPlannedForm({...plannedForm, description: e.target.value})}
                  className="input-animated w-full"
                  placeholder="Monthly rent payment"
                  rows="3"
                />
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                className="btn-premium w-full ripple"
              >
                Add Planned Expense
              </motion.button>
            </form>
          </motion.div>
        </motion.div>
      )}

      {/* Goal Modal */}
      {showGoalModal && (
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4"
          onClick={() => setShowGoalModal(false)}
        >
          <motion.div 
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="glass-card p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-white">Create Saving Goal</h3>
              <button onClick={() => setShowGoalModal(false)} className="text-gray-400 hover:text-white">
                <X size={24} />
              </button>
            </div>
            <form onSubmit={handleGoalSubmit} className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-2">Goal Name</label>
                <input 
                  type="text"
                  value={goalForm.goalName}
                  onChange={(e) => setGoalForm({...goalForm, goalName: e.target.value})}
                  className="input-animated w-full"
                  placeholder="Laptop / Vacation / Emergency Fund"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Target Amount (₹)</label>
                <input 
                  type="number"
                  value={goalForm.targetAmount}
                  onChange={(e) => setGoalForm({...goalForm, targetAmount: e.target.value})}
                  className="input-animated w-full"
                  placeholder="60000"
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <div>
                <label className="block text-gray-400 mb-2">Deadline (Optional)</label>
                <input 
                  type="date"
                  value={goalForm.deadline}
                  onChange={(e) => setGoalForm({...goalForm, deadline: e.target.value})}
                  className="input-animated w-full"
                />
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                className="btn-premium w-full ripple"
              >
                Create Goal
              </motion.button>
            </form>
          </motion.div>
        </motion.div>
      )}

      {/* Add Money Modal */}
      {showAddMoneyModal && selectedGoal && (
        <motion.div 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center z-50 p-4"
          onClick={() => {
            setShowAddMoneyModal(false);
            setSelectedGoal(null);
          }}
        >
          <motion.div 
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="glass-card p-6 max-w-md w-full"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-2xl font-bold text-white">Add Money to Goal</h3>
              <button onClick={() => {
                setShowAddMoneyModal(false);
                setSelectedGoal(null);
              }} className="text-gray-400 hover:text-white">
                <X size={24} />
              </button>
            </div>
            <div className="mb-4 bg-white/5 rounded-lg p-4 border border-white/10">
              <p className="text-gray-400 text-sm">Goal</p>
              <p className="text-white font-semibold text-lg">{selectedGoal.goalName}</p>
              <p className="text-gray-400 text-sm mt-2">
                Current: ₹{selectedGoal.currentAmount} / ₹{selectedGoal.targetAmount}
              </p>
            </div>
            <form onSubmit={handleAddMoney} className="space-y-4">
              <div>
                <label className="block text-gray-400 mb-2">Amount to Add (₹)</label>
                <input 
                  type="number"
                  value={addMoneyAmount}
                  onChange={(e) => setAddMoneyAmount(e.target.value)}
                  className="input-animated w-full"
                  placeholder="1000"
                  required
                  min="0"
                  step="0.01"
                />
              </div>
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="submit"
                className="btn-premium w-full ripple"
              >
                Add Money
              </motion.button>
            </form>
          </motion.div>
        </motion.div>
      )}
    </div>
  );
};

export default DashboardPage;
