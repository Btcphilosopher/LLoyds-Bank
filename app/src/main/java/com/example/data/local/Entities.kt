package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "lloyds_user_001",
    val name: String,
    val email: String,
    val isBiometricsEnabled: Boolean = false,
    val isMfaSetup: Boolean = false,
    val pinHash: String = "1234", // Simple default PIN for demonstration
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "CURRENT", "SAVINGS", "JOINT", "BUSINESS"
    val balance: Double,
    val availableFunds: Double,
    val accountNumber: String,
    val sortCode: String,
    val interestRate: Double = 0.0, // Only for SAVINGS
    val isFrozen: Boolean = false
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: String,
    val title: String,
    val description: String,
    val category: String, // "Groceries", "Bills", "Salary", "Investment", "Shopping", "Transfer", "FraudAlert"
    val amount: Double,
    val timestamp: Long,
    val type: String, // "DEBIT", "CREDIT"
    val status: String, // "COMPLETED", "PENDING"
    val riskScore: Double = 0.0, // 0.0 to 100.0 scale
    val isSuspicious: Boolean = false
)

@Entity(tableName = "beneficiaries")
data class BeneficiaryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val accountNumber: String,
    val sortCode: String,
    val bankName: String
)

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val isRoundUpEnabled: Boolean = false
)

@Entity(tableName = "fraud_logs")
data class FraudLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleName: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: String, // "LOW", "MEDIUM", "HIGH"
    val details: String,
    val isActionTaken: Boolean = false
)
