package org.kumoricon.service.validate;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.kumoricon.model.order.Payment;

import java.math.BigDecimal;


public class PaymentValidator extends Validator {

    public static boolean validate (Payment payment) {
        if (payment.getPaymentType() == Payment.PaymentType.CREDIT) {
            if (isNullOrEmpty(payment.getAuthNumber())) {
                throw new ValueException("Credit cards must have authorization number in note field");
            }
        } else if (payment.getPaymentType() == Payment.PaymentType.CHECK) {
            if (isNullOrEmpty(payment.getAuthNumber())) {
               throw new ValueException("Checks must have a check number in note field");
            }
        }

        if (payment.getAmount() == null || BigDecimal.ZERO.compareTo(payment.getAmount()) > 0) {
            throw new ValueException("Amount must not be negative or empty");
        }

        return true;
    }

}
