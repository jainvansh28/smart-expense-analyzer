from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
import pandas as pd
import numpy as np
from sklearn.linear_model import LinearRegression
from datetime import datetime, timedelta
from dateutil.relativedelta import relativedelta

app = FastAPI(title="Smart Expense Analyzer ML Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ExpenseData(BaseModel):
    amount: float
    category: str
    date: str

class PredictionRequest(BaseModel):
    expenses: List[ExpenseData]

class PredictionResponse(BaseModel):
    predicted_expense: float
    overspending_risk_percentage: int
    savings_prediction: float

@app.get("/")
def read_root():
    return {"message": "Smart Expense Analyzer ML Service", "status": "running"}

@app.post("/predict", response_model=PredictionResponse)
def predict_expenses(request: PredictionRequest):
    try:
        if not request.expenses:
            raise HTTPException(status_code=400, detail="No expense data provided")
        
        df = pd.DataFrame([{
            'amount': exp.amount,
            'category': exp.category,
            'date': datetime.strptime(exp.date, '%Y-%m-%d')
        } for exp in request.expenses])
        
        df['year_month'] = df['date'].dt.to_period('M')
        monthly_totals = df.groupby('year_month')['amount'].sum().reset_index()
        monthly_totals['month_num'] = range(len(monthly_totals))
        
        if len(monthly_totals) < 2:
            avg_expense = df['amount'].sum()
            return PredictionResponse(
                predicted_expense=round(avg_expense, 2),
                overspending_risk_percentage=50,
                savings_prediction=round(max(0, 50000 - avg_expense), 2)
            )
        
        X = monthly_totals['month_num'].values.reshape(-1, 1)
        y = monthly_totals['amount'].values
        
        model = LinearRegression()
        model.fit(X, y)
        
        next_month_num = len(monthly_totals)
        predicted_expense = model.predict([[next_month_num]])[0]
        predicted_expense = max(0, predicted_expense)
        
        recent_avg = monthly_totals.tail(3)['amount'].mean()
        overspending_risk = 0
        
        if predicted_expense > recent_avg * 1.3:
            overspending_risk = 80
        elif predicted_expense > recent_avg * 1.2:
            overspending_risk = 65
        elif predicted_expense > recent_avg * 1.1:
            overspending_risk = 50
        elif predicted_expense > recent_avg:
            overspending_risk = 35
        else:
            overspending_risk = 20
        
        estimated_income = 50000
        savings_prediction = max(0, estimated_income - predicted_expense)
        
        return PredictionResponse(
            predicted_expense=round(predicted_expense, 2),
            overspending_risk_percentage=overspending_risk,
            savings_prediction=round(savings_prediction, 2)
        )
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")

@app.get("/health")
def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
