package org.kumoricon.service.validate;

import org.kumoricon.model.order.Payment;
import java.math.BigDecimal;

public class PaymentValidator extends Validator {
    /**
     * Validate fields in the given payment. Throws ValidationException on any failure
     * @param payment Payment
     * @throws ValidationException Error message
     */
    public static void validate (Payment payment) throws ValidationException {
        if (payment.getPaymentType() == Payment.PaymentType.CREDIT) {
            if (isNullOrEmpty(payment.getAuthNumber())) {
                throw new ValidationException("Credit cards must have authorization number in note field");
            }
        } else if (payment.getPaymentType() == Payment.PaymentType.CHECK) {
            if (isNullOrEmpty(payment.getAuthNumber())) {
               throw new ValidationException("Checks must have a check number in note field");
            }
        }

        if (payment.getAmount() == null || BigDecimal.ZERO.compareTo(payment.getAmount()) > 0) {
            throw new ValidationException("Amount must not be negative or empty");
        }
    }

}
