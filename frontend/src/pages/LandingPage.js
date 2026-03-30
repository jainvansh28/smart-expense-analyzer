import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, PieChart, Brain, Shield, ArrowRight, Sparkles } from 'lucide-react';
import AnimatedBackground from '../components/AnimatedBackground';

const LandingPage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen premium-bg relative overflow-hidden">
      <AnimatedBackground />
      
      <nav className="p-6 flex justify-between items-center relative z-10">
        <motion.h1 
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="text-3xl font-bold neon-text"
        >
          💰 ExpenseAI
        </motion.h1>
        <div className="space-x-4">
          <motion.button 
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate('/login')} 
            className="text-white hover:text-gray-200 px-4 py-2 transition"
          >
            Login
          </motion.button>
          <motion.button 
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate('/signup')} 
            className="btn-premium ripple"
          >
            Sign Up
          </motion.button>
        </div>
      </nav>

      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="container mx-auto px-6 py-20 text-center relative z-10"
      >
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="mb-6"
        >
          <Sparkles className="inline-block text-yellow-400 mb-4" size={48} />
        </motion.div>
        <h2 className="text-7xl font-bold text-white mb-6">
          Smart Expense Tracking
          <br />
          <span className="gradient-text">Powered by AI</span>
        </h2>
        <p className="text-xl text-gray-300 mb-12 max-w-2xl mx-auto">
          Track your expenses, get intelligent insights, and predict your future spending with machine learning
        </p>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => navigate('/signup')}
          className="btn-premium text-lg px-10 py-4 inline-flex items-center gap-2"
        >
          Get Started <ArrowRight size={24} />
        </motion.button>
      </motion.div>

      <div className="container mx-auto px-6 py-20 relative z-10">
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
          {[
            { icon: TrendingUp, title: 'Real-time Analytics', desc: 'Track spending patterns instantly', color: 'cyan' },
            { icon: PieChart, title: 'Visual Insights', desc: 'Beautiful charts and graphs', color: 'purple' },
            { icon: Brain, title: 'AI Predictions', desc: 'ML-powered spending forecasts', color: 'pink' },
            { icon: Shield, title: 'Secure & Private', desc: 'Your data is encrypted', color: 'green' }
          ].map((feature, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="glass-card p-6 text-white hover:scale-105 transition card-shine"
            >
              <feature.icon size={40} className={`mb-4 text-${feature.color}-400`} />
              <h3 className="text-xl font-bold mb-2">{feature.title}</h3>
              <p className="text-gray-300">{feature.desc}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default LandingPage;
