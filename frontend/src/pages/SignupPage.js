import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Mail, Lock, User, ArrowLeft, UserPlus, Shield } from 'lucide-react';
import AnimatedBackground from '../components/AnimatedBackground';

const SignupPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({ name: '', email: '', password: '', otp: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSendOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await authAPI.sendOtp(formData.email);
      setStep(2);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await authAPI.verifyOtp(formData.email, formData.otp);
      setStep(3);
    } catch (err) {
      setError(err.response?.data?.error || 'Invalid OTP');
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const response = await authAPI.signup({
        name: formData.name,
        email: formData.email,
        password: formData.password
      });
      login({ id: response.data.id, name: response.data.name, email: response.data.email }, response.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Signup failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen premium-bg relative flex items-center justify-center p-6">
      <AnimatedBackground />
      
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="glass-card p-8 w-full max-w-md relative z-10"
      >
        <motion.button 
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => navigate('/')} 
          className="text-white mb-6 flex items-center gap-2 hover:text-cyan-300 transition"
        >
          <ArrowLeft size={20} /> Back
        </motion.button>
        
        <div className="text-center mb-8">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ type: "spring", stiffness: 200 }}
            className="inline-block mb-4"
          >
            <UserPlus className="text-purple-400" size={48} />
          </motion.div>
          <h2 className="text-4xl font-bold text-white mb-2">Create Account</h2>
          <p className="text-gray-400">Join us to start tracking your expenses</p>
        </div>

        {/* Progress Indicator */}
        <div className="flex justify-center gap-2 mb-6">
          {[1, 2, 3].map((s) => (
            <div
              key={s}
              className={`h-2 rounded-full transition-all duration-300 ${
                s === step ? 'w-12 bg-gradient-to-r from-cyan-500 to-purple-500' : 
                s < step ? 'w-8 bg-green-500' : 'w-8 bg-white/20'
              }`}
            />
          ))}
        </div>
        
        {error && (
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className="bg-red-500/20 border border-red-500 text-white p-3 rounded-lg mb-4"
          >
            {error}
          </motion.div>
        )}

        {step === 1 && (
          <form onSubmit={handleSendOtp} className="space-y-6">
            <div>
              <label className="text-white block mb-2 font-semibold">Email</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <Mail size={20} className="text-cyan-400 mr-2" />
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  className="bg-transparent text-white outline-none w-full placeholder-gray-400"
                  placeholder="your@email.com"
                  required
                />
              </div>
            </div>
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={loading}
              className="w-full btn-premium py-4 text-lg ripple disabled:opacity-50"
            >
              {loading ? 'Sending...' : 'Send OTP'}
            </motion.button>
          </form>
        )}

        {step === 2 && (
          <form onSubmit={handleVerifyOtp} className="space-y-6">
            <div>
              <label className="text-white block mb-2 font-semibold">Enter OTP</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <Shield size={20} className="text-green-400 mr-2" />
                <input
                  type="text"
                  value={formData.otp}
                  onChange={(e) => setFormData({ ...formData, otp: e.target.value })}
                  className="bg-transparent text-white outline-none w-full text-center text-2xl tracking-widest placeholder-gray-400"
                  placeholder="000000"
                  maxLength="6"
                  required
                />
              </div>
              <p className="text-gray-400 text-sm mt-2 text-center">Check your email or backend console for OTP</p>
            </div>
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={loading}
              className="w-full btn-premium py-4 text-lg ripple disabled:opacity-50"
            >
              {loading ? 'Verifying...' : 'Verify OTP'}
            </motion.button>
          </form>
        )}

        {step === 3 && (
          <form onSubmit={handleSignup} className="space-y-6">
            <div>
              <label className="text-white block mb-2 font-semibold">Name</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <User size={20} className="text-purple-400 mr-2" />
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="bg-transparent text-white outline-none w-full placeholder-gray-400"
                  placeholder="John Doe"
                  required
                />
              </div>
            </div>
            <div>
              <label className="text-white block mb-2 font-semibold">Password</label>
              <div className="flex items-center bg-white/10 rounded-lg p-3 input-animated border border-purple-500/30">
                <Lock size={20} className="text-pink-400 mr-2" />
                <input
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="bg-transparent text-white outline-none w-full placeholder-gray-400"
                  placeholder="••••••••"
                  required
                  minLength="6"
                />
              </div>
            </div>
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={loading}
              className="w-full btn-premium py-4 text-lg ripple disabled:opacity-50"
            >
              {loading ? 'Creating Account...' : 'Sign Up'}
            </motion.button>
          </form>
        )}

        <p className="text-gray-400 text-center mt-6">
          Already have an account?{' '}
          <button onClick={() => navigate('/login')} className="text-cyan-400 hover:text-cyan-300 font-semibold transition">
            Login
          </button>
        </p>
      </motion.div>
    </div>
  );
};

export default SignupPage;
