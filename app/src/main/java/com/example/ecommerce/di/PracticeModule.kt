package com.example.ecommerce.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

interface PaymentService {
    fun processPayment(amount: Double): String
}

interface NtfService {
    fun sendNotification(message: String): String
}

interface Logger {
    fun log(event: String)
}

class CCPaymentService @Inject constructor() : PaymentService {
    override fun processPayment(amount: Double): String {
        return "Processed credit card payment of $amount"
    }
}

class PayPalPaymentService @Inject constructor() : PaymentService {
    override fun processPayment(amount: Double): String {
        return "Processed PayPal payment of $amount"
    }
}

class EmailNotificationService @Inject constructor() : NtfService {
    override fun sendNotification(message: String): String {
        return "Email sent: $message"
    }
}

class SMSNotificationService @Inject constructor() : NtfService {
    override fun sendNotification(message: String): String {
        return "SMS sent: $message"
    }
}

class ConsoleLogger @Inject constructor() : Logger {
    override fun log(event: String) {
        println("Log: $event")
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CreditCard

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PayPal

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Email

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SMS

@Module
@InstallIn(SingletonComponent::class)
abstract class PracticeModule {

    @Binds
    @Singleton
    abstract fun bindLogger(consoleLogger: ConsoleLogger): Logger

    @Binds
    @CreditCard
    @Singleton
    abstract fun bindCreditCardPayment(CCPaymentService: CCPaymentService): PaymentService

    @Binds
    @PayPal
    @Singleton
    abstract fun bindPayPalPayment(payPalPaymentService: PayPalPaymentService): PaymentService

    @Binds
    @Email
    @Singleton
    abstract fun bindEmailNotification(emailNotificationService: EmailNotificationService): NtfService

    @Binds
    @SMS
    @Singleton
    abstract fun bindSMSNotification(smsNotificationService: SMSNotificationService): NtfService
}

class OrderProcessor @Inject constructor(
    @CreditCard private val creditCardPaymentService: PaymentService,
    @PayPal private val payPalPaymentService: PaymentService,
    @Email private val emailNotificationService: NtfService,
    @SMS private val smsNotificationService: NtfService,
    private val logger: Logger
) {
    fun processOrder(amount: Double, useCreditCard: Boolean) {
        val paymentResult = if (useCreditCard) {
            creditCardPaymentService.processPayment(amount)
        } else {
            payPalPaymentService.processPayment(amount)
        }
        logger.log(paymentResult)

        val emailResult = emailNotificationService.sendNotification("Order processed for $$amount")
        logger.log(emailResult)

        val smsResult = smsNotificationService.sendNotification("Your order of $$amount is complete")
        logger.log(smsResult)
    }
}
