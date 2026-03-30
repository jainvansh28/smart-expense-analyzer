import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ArrowLeft, User, Mail, Calendar, Download, Key, Shield, X, Eye, EyeOff } from 'lucide-react';
import AnimatedBackground from '../components/AnimatedBackground';
import { userAPI } from '../services/api';

const ProfilePage = () => {
  const navigate = useNavigate();
  const { user, setUser } = useAuth();
  const [memberSince, setMemberSince] = useState('2024');
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [showPrivacyModal, setShowPrivacyModal] = useState(false);
  const [loading, setLoading] = useState(false);
  
  // Password form state
  const [passwordForm, setPasswordForm] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false
  });
  
  // Privacy settings state
  const [privacySettings, setPrivacySettings] = useState({
    emailNotifications: true,
    showSensitiveInfo: true
  });

  useEffect(() => {
    // Fetch user profile to get createdAt
    const fetchProfile = async () => {
      try {
        const res = await userAPI.getProfile();
        if (res.data.createdAt) {
          const year = new Date(res.data.createdAt).getFullYear();
          setMemberSince(year.toString());
        }
        setPrivacySettings({
          emailNotifications: res.data.emailNotifications ?? true,
          showSensitiveInfo: res.data.showSensitiveInfo ?? true
        });
      } catch (error) {
        console.error('Error fetching profile:', error);
      }
    };
    
    if (user?.createdAt) {
      const year = new Date(user.createdAt).getFullYear();
      setMemberSince(year.toString());
    }
    
    if (user?.emailNotifications !== undefined) {
      setPrivacySettings({
        emailNotifications: user.emailNotifications,
        showSensitiveInfo: user.showSensitiveInfo ?? true
      });
    } else {
      fetchProfile();
    }
  }, [user]);

  const handleExportCsv = async () => {
    try {
      setLoading(true);
      const response = await userAPI.exportCsv();
      
      // Create blob and download
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `expense_data_${new Date().toISOString().split('T')[0]}.csv`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      alert('Data exported successfully!');
    } catch (error) {
      console.error('Export error:', error);
      alert('Failed to export data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      alert('New passwords do not match!');
      return;
    }
    
    if (passwordForm.newPassword.length < 6) {
      alert('New password must be at least 6 characters long!');
      return;
    }
    
    try {
      setLoading(true);
      await userAPI.changePassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword
      });
      
      alert('Password changed successfully!');
      setShowPasswordModal(false);
      setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (error) {
      console.error('Password change error:', error);
      alert(error.response?.data?.error || 'Failed to change password. Please check your current password.');
    } finally {
      setLoading(false);
    }
  };

  const handleSavePrivacySettings = async () => {
    try {
      setLoading(true);
      const res = await userAPI.updatePrivacySettings(privacySettings);
      
      // Update user context
      if (setUser) {
        setUser(prev => ({
          ...prev,
          emailNotifications: privacySettings.emailNotifications,
          showSensitiveInfo: privacySettings.showSensitiveInfo
        }));
      }
      
      alert('Privacy settings updated successfully!');
      setShowPrivacyModal(false);
    } catch (error) {
      console.error('Privacy settings error:', error);
      alert('Failed to update privacy settings. Please try again.');
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
          <div className="text-center mb-8">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: "spring", stiffness: 200 }}
              className="w-24 h-24 bg-gradient-to-br from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg"
            >
              <User size={48} className="text-white" />
            </motion.div>
            <h2 className="text-3xl font-bold text-white gradient-text">{user?.name}</h2>
            <p className="text-gray-400 mt-2">Account Information</p>
          </div>

          <div className="space-y-4 mb-8">
            <motion.div 
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-white/5 rounded-xl p-4 flex items-center gap-4 border border-white/10 hover:bg-white/10 transition"
            >
              <div className="w-12 h-12 bg-gradient-to-br from-cyan-500 to-blue-500 rounded-lg flex items-center justify-center">
                <Mail size={24} className="text-white" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Email</p>
                <p className="text-white font-semibold">{user?.email}</p>
              </div>
            </motion.div>

            <motion.div 
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white/5 rounded-xl p-4 flex items-center gap-4 border border-white/10 hover:bg-white/10 transition"
            >
              <div className="w-12 h-12 bg-gradient-to-br from-purple-500 to-pink-500 rounded-lg flex items-center justify-center">
                <User size={24} className="text-white" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">User ID</p>
                <p className="text-white font-semibold">{user?.id}</p>
              </div>
            </motion.div>

            <motion.div 
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.3 }}
              className="bg-white/5 rounded-xl p-4 flex items-center gap-4 border border-white/10 hover:bg-white/10 transition"
            >
              <div className="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-500 rounded-lg flex items-center justify-center">
                <Calendar size={24} className="text-white" />
              </div>
              <div>
                <p className="text-gray-400 text-sm">Member Since</p>
                <p className="text-white font-semibold">{memberSince}</p>
              </div>
            </motion.div>
          </div>

          <div className="space-y-3">
            <motion.button 
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 }}
              onClick={handleExportCsv}
              disabled={loading}
              className="w-full bg-white/5 text-white py-3 rounded-lg hover:bg-white/10 transition flex items-center justify-center gap-2 border border-white/10 disabled:opacity-50"
            >
              <Download size={20} />
              {loading ? 'Exporting...' : 'Export Data to CSV'}
            </motion.button>
            
            <motion.button 
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              onClick={() => setShowPasswordModal(true)}
              className="w-full bg-white/5 text-white py-3 rounded-lg hover:bg-white/10 transition flex items-center justify-center gap-2 border border-white/10"
            >
              <Key size={20} />
              Change Password
            </motion.button>

            <motion.button 
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.6 }}
              onClick={() => setShowPrivacyModal(true)}
              className="w-full bg-white/5 text-white py-3 rounded-lg hover:bg-white/10 transition flex items-center justify-center gap-2 border border-white/10"
            >
              <Shield size={20} />
              Privacy Settings
            </motion.button>
          </div>
        </motion.div>
      </div>

      {/* Change Password Modal */}
      <AnimatePresence>
        {showPasswordModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4"
            onClick={() => setShowPasswordModal(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="glass-card p-6 max-w-md w-full"
            >
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-2xl font-bold text-white flex items-center gap-2">
                  <Key className="text-purple-400" size={24} />
                  Change Password
                </h3>
                <button onClick={() => setShowPasswordModal(false)} className="text-gray-400 hover:text-white">
                  <X size={24} />
                </button>
              </div>

              <form onSubmit={handleChangePassword} className="space-y-4">
                <div>
                  <label className="text-gray-400 text-sm mb-2 block">Current Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.current ? "text" : "password"}
                      value={passwordForm.currentPassword}
                      onChange={(e) => setPasswordForm({...passwordForm, currentPassword: e.target.value})}
                      className="input-animated w-full pr-10"
                      required
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({...showPasswords, current: !showPasswords.current})}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showPasswords.current ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="text-gray-400 text-sm mb-2 block">New Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.new ? "text" : "password"}
                      value={passwordForm.newPassword}
                      onChange={(e) => setPasswordForm({...passwordForm, newPassword: e.target.value})}
                      className="input-animated w-full pr-10"
                      required
                      minLength={6}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({...showPasswords, new: !showPasswords.new})}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showPasswords.new ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="text-gray-400 text-sm mb-2 block">Confirm New Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.confirm ? "text" : "password"}
                      value={passwordForm.confirmPassword}
                      onChange={(e) => setPasswordForm({...passwordForm, confirmPassword: e.target.value})}
                      className="input-animated w-full pr-10"
                      required
                      minLength={6}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({...showPasswords, confirm: !showPasswords.confirm})}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white"
                    >
                      {showPasswords.confirm ? <EyeOff size={20} /> : <Eye size={20} />}
                    </button>
                  </div>
                </div>

                <div className="flex gap-3 mt-6">
                  <button
                    type="button"
                    onClick={() => setShowPasswordModal(false)}
                    className="flex-1 bg-white/5 text-white py-2 rounded-lg hover:bg-white/10 transition"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={loading}
                    className="flex-1 btn-premium disabled:opacity-50"
                  >
                    {loading ? 'Changing...' : 'Change Password'}
                  </button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Privacy Settings Modal */}
      <AnimatePresence>
        {showPrivacyModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4"
            onClick={() => setShowPrivacyModal(false)}
          >
            <motion.div
              initial={{ scale: 0.9, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.9, opacity: 0 }}
              onClick={(e) => e.stopPropagation()}
              className="glass-card p-6 max-w-md w-full"
            >
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-2xl font-bold text-white flex items-center gap-2">
                  <Shield className="text-purple-400" size={24} />
                  Privacy Settings
                </h3>
                <button onClick={() => setShowPrivacyModal(false)} className="text-gray-400 hover:text-white">
                  <X size={24} />
                </button>
              </div>

              <div className="space-y-4">
                <div className="bg-white/5 rounded-lg p-4 border border-white/10">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-white font-semibold">Email Notifications</p>
                      <p className="text-gray-400 text-sm">Receive email alerts for important updates</p>
                    </div>
                    <button
                      onClick={() => setPrivacySettings({...privacySettings, emailNotifications: !privacySettings.emailNotifications})}
                      className={`relative w-14 h-7 rounded-full transition-colors ${
                        privacySettings.emailNotifications ? 'bg-purple-500' : 'bg-gray-600'
                      }`}
                    >
                      <span
                        className={`absolute top-1 left-1 w-5 h-5 bg-white rounded-full transition-transform ${
                          privacySettings.emailNotifications ? 'translate-x-7' : 'translate-x-0'
                        }`}
                      />
                    </button>
                  </div>
                </div>

                <div className="bg-white/5 rounded-lg p-4 border border-white/10">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-white font-semibold">Show Sensitive Info</p>
                      <p className="text-gray-400 text-sm">Display financial amounts by default</p>
                    </div>
                    <button
                      onClick={() => setPrivacySettings({...privacySettings, showSensitiveInfo: !privacySettings.showSensitiveInfo})}
                      className={`relative w-14 h-7 rounded-full transition-colors ${
                        privacySettings.showSensitiveInfo ? 'bg-purple-500' : 'bg-gray-600'
                      }`}
                    >
                      <span
                        className={`absolute top-1 left-1 w-5 h-5 bg-white rounded-full transition-transform ${
                          privacySettings.showSensitiveInfo ? 'translate-x-7' : 'translate-x-0'
                        }`}
                      />
                    </button>
                  </div>
                </div>
              </div>

              <div className="flex gap-3 mt-6">
                <button
                  onClick={() => setShowPrivacyModal(false)}
                  className="flex-1 bg-white/5 text-white py-2 rounded-lg hover:bg-white/10 transition"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSavePrivacySettings}
                  disabled={loading}
                  className="flex-1 btn-premium disabled:opacity-50"
                >
                  {loading ? 'Saving...' : 'Save Settings'}
                </button>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default ProfilePage;
