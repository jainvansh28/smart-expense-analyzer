# Enhanced Features Implementation Plan

## Overview
This document outlines the implementation of 7 new features for the Smart Expense Analyzer while maintaining existing functionality and design.

## Features to Implement

### 1. Monthly Budget Limit ✅ (In Progress)
**Database**: `category_budgets` table
**Backend**: CategoryBudget model, repository, service, controller
**Frontend**: Budget management page, budget progress bars on dashboard
**API Endpoints**:
- POST `/api/budget/set` - Set category budget
- GET `/api/budget/list` - Get all budgets for user
- PUT `/api/budget/update/{id}` - Update budget
- DELETE `/api/budget/delete/{id}` - Delete budget
- GET `/api/budget/status` - Get budget status with spending

### 2. Planned Expenses ✅ (In Progress)
**Database**: `planned_expenses` table
**Backend**: PlannedExpense model, repository, service, controller
**Frontend**: Planned expenses page, upcoming payments widget on dashboard
**API Endpoints**:
- POST `/api/planned/add` - Add planned expense
- GET `/api/planned/list` - Get all planned expenses
- GET `/api/planned/upcoming` - Get unpaid planned expenses
- PUT `/api/planned/update/{id}` - Update planned expense
- POST `/api/planned/mark-paid/{id}` - Mark as paid (creates actual expense)
- DELETE `/api/planned/delete/{id}` - Delete planned expense

### 3. Advanced Expense Filters
**Backend**: Enhance ExpenseService with filtering
**Frontend**: Add filter UI to ExpenseHistoryPage
**API Endpoints**:
- GET `/api/expense/filter` - Filter expenses by month, category, date range

### 4. Expense Search
**Backend**: Add search functionality to ExpenseService
**Frontend**: Add search bar to ExpenseHistoryPage
**API Endpoints**:
- GET `/api/expense/search?q={query}` - Search expenses

### 5. Saving Goal Tracker ✅ (In Progress)
**Database**: `saving_goals` table
**Backend**: SavingGoal model, repository, service, controller
**Frontend**: Goals page, goals widget on dashboard
**API Endpoints**:
- POST `/api/goals/add` - Add saving goal
- GET `/api/goals/list` - Get all goals
- PUT `/api/goals/update/{id}` - Update goal
- POST `/api/goals/contribute/{id}` - Add money to goal
- DELETE `/api/goals/delete/{id}` - Delete goal

### 6. Category Insights
**Backend**: Enhance AnalyticsService with month-over-month comparisons
**Frontend**: Add insights section to dashboard
**API Endpoints**:
- GET `/api/analytics/insights` - Get category insights

### 7. Overspending Alert
**Backend**: Add overspending detection to AnalyticsService
**Frontend**: Display alerts on dashboard
**Logic**: Compare current month spending to 3-month average per category

## File Structure

### Backend Files to Create:
```
backend/src/main/java/com/expenseanalyzer/
├── model/
│   ├── CategoryBudget.java ✅
│   ├── PlannedExpense.java ✅
│   └── SavingGoal.java ✅
├── repository/
│   ├── CategoryBudgetRepository.java ✅
│   ├── PlannedExpenseRepository.java ✅
│   └── SavingGoalRepository.java ✅
├── dto/
│   ├── CategoryBudgetRequest.java
│   ├── CategoryBudgetResponse.java
│   ├── PlannedExpenseRequest.java
│   ├── SavingGoalRequest.java
│   ├── InsightResponse.java
│   └── BudgetStatusResponse.java
├── service/
│   ├── CategoryBudgetService.java
│   ├── PlannedExpenseService.java
│   ├── SavingGoalService.java
│   └── InsightsService.java
└── controller/
    ├── CategoryBudgetController.java
    ├── PlannedExpenseController.java
    ├── SavingGoalController.java
    └── InsightsController.java
```

### Frontend Files to Create/Modify:
```
frontend/src/
├── pages/
│   ├── BudgetManagementPage.js (NEW)
│   ├── PlannedExpensesPage.js (NEW)
│   ├── SavingGoalsPage.js (NEW)
│   ├── ExpenseHistoryPage.js (MODIFY - add filters & search)
│   └── DashboardPage.js (MODIFY - add widgets)
├── components/
│   ├── BudgetProgressCard.js (NEW)
│   ├── UpcomingPaymentsWidget.js (NEW)
│   ├── SavingGoalsWidget.js (NEW)
│   ├── InsightsWidget.js (NEW)
│   └── OverspendingAlert.js (NEW)
└── services/
    └── api.js (MODIFY - add new API calls)
```

### Database Files:
```
database/
└── enhanced_features.sql ✅
```

## Implementation Order

### Phase 1: Backend Foundation (Current)
1. ✅ Create database schema
2. ✅ Create models (CategoryBudget, PlannedExpense, SavingGoal)
3. ✅ Create repositories
4. ⏳ Create DTOs
5. ⏳ Create services
6. ⏳ Create controllers

### Phase 2: Frontend Foundation
1. Update api.js with new endpoints
2. Create new pages
3. Create new components
4. Update routing in App.js

### Phase 3: Integration
1. Add widgets to dashboard
2. Add navigation links
3. Test all features
4. Fix bugs

### Phase 4: Polish
1. Add loading states
2. Add error handling
3. Add success messages
4. Optimize performance

## Design Guidelines

### Colors (Maintain Existing Theme):
- Background: `#0a0118` (dark purple)
- Primary: `#8B5CF6` (purple)
- Secondary: `#EC4899` (pink)
- Accent: `#06B6D4` (cyan)
- Success: `#10B981` (green)
- Warning: `#F59E0B` (yellow/orange)
- Danger: `#EF4444` (red)

### Components Style:
- Use `glass-card` class for cards
- Use `btn-premium` class for buttons
- Use `input-animated` class for inputs
- Add `AnimatedBackground` to new pages
- Use `motion` from framer-motion for animations
- Use `AnimatedCounter` for numbers

### Icons (from lucide-react):
- Budget: `Target`, `TrendingUp`
- Planned: `Calendar`, `Clock`, `CheckCircle`
- Goals: `Trophy`, `PiggyBank`, `Target`
- Insights: `TrendingUp`, `TrendingDown`, `AlertCircle`
- Search: `Search`
- Filter: `Filter`

## API Response Examples

### Budget Status Response:
```json
{
  "category": "Food",
  "budgetAmount": 3000.00,
  "spentAmount": 2500.00,
  "remainingAmount": 500.00,
  "percentageUsed": 83.33,
  "isOverBudget": false
}
```

### Planned Expense Response:
```json
{
  "id": 1,
  "title": "Rent",
  "amount": 5000.00,
  "category": "Bills",
  "dueDay": 5,
  "isPaid": false,
  "daysUntilDue": 3
}
```

### Saving Goal Response:
```json
{
  "id": 1,
  "goalName": "Buy Laptop",
  "targetAmount": 60000.00,
  "currentAmount": 15000.00,
  "percentageComplete": 25.00,
  "remainingAmount": 45000.00,
  "deadline": "2026-12-31",
  "status": "active"
}
```

### Insight Response:
```json
{
  "category": "Food",
  "currentMonthSpending": 3660.00,
  "previousMonthSpending": 3000.00,
  "changePercentage": 22.00,
  "trend": "increased",
  "message": "Food expenses increased by 22% compared to last month."
}
```

## Testing Checklist

### Budget Feature:
- [ ] Can set budget for category
- [ ] Budget progress shows correctly
- [ ] Warning appears when over budget
- [ ] Can update budget
- [ ] Can delete budget

### Planned Expenses:
- [ ] Can create planned expense
- [ ] Shows in upcoming payments
- [ ] Can mark as paid
- [ ] Creates actual expense when marked paid
- [ ] Can edit planned expense
- [ ] Can delete planned expense

### Filters & Search:
- [ ] Can filter by month
- [ ] Can filter by category
- [ ] Can filter by date range
- [ ] Can search by description
- [ ] Filters update instantly

### Saving Goals:
- [ ] Can create goal
- [ ] Progress bar shows correctly
- [ ] Can contribute to goal
- [ ] Can update goal
- [ ] Can delete goal
- [ ] Shows on dashboard

### Insights:
- [ ] Shows category comparisons
- [ ] Calculates percentages correctly
- [ ] Shows trend direction
- [ ] Updates with new data

### Overspending Alerts:
- [ ] Detects unusual spending
- [ ] Shows warning message
- [ ] Calculates average correctly

## Performance Considerations

1. **Database Queries**: Use indexes on user_id, month, year
2. **Frontend**: Use React.memo for widgets
3. **API Calls**: Batch related data in single requests
4. **Caching**: Consider caching insights calculations
5. **Pagination**: Add pagination to expense history if > 100 items

## Backward Compatibility

- All existing API endpoints remain unchanged
- Existing database tables not modified
- Existing frontend pages work as before
- New features are additive only

## Next Steps

Continue with creating DTOs, Services, and Controllers...
