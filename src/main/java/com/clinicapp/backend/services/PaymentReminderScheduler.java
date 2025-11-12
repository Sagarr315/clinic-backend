package com.clinicapp.backend.services;

import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.clinicapp.backend.entity.Bill;

@Component
public class PaymentReminderScheduler {

    private final BillingService billingService;

    public PaymentReminderScheduler(BillingService billingService) {
        this.billingService = billingService;
    }

    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendPaymentReminders() {
        List<Bill> pendingBills = billingService.getPendingBills();

        for (Bill bill : pendingBills) {
            // In real implementation, send email/SMS here
            System.out.println("Payment Reminder: Bill ID " + bill.getId() +
                    " for Patient " + bill.getAppointment().getPatient().getName() +
                    " Amount: " + bill.getTotalAmount());
        }

        System.out.println("Sent payment reminders for " + pendingBills.size() + " pending bills");
    }
}