import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LandingPage from './pages/LandingPage';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import AddExpensePage from './pages/AddExpensePage';
import AddIncomePage from './pages/AddIncomePage';
import ExpenseHistoryPage from './pages/ExpenseHistoryPage';
import IncomeHistoryPage from './pages/IncomeHistoryPage';
import ProfilePage from './pages/ProfilePage';
import { AuthProvider, useAuth } from './context/AuthContext';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="min-h-screen dashboard-bg flex items-center justify-center">
        <div className="spinner" />
      </div>
    );
  }
  
  return user ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
          <Route path="/add-expense" element={<PrivateRoute><AddExpensePage /></PrivateRoute>} />
          <Route path="/add-income" element={<PrivateRoute><AddIncomePage /></PrivateRoute>} />
          <Route path="/expenses" element={<PrivateRoute><ExpenseHistoryPage /></PrivateRoute>} />
          <Route path="/incomes" element={<PrivateRoute><IncomeHistoryPage /></PrivateRoute>} />
          <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
