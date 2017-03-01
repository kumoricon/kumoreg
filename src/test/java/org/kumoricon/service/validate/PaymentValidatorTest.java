package org.kumoricon.service.validate;

import org.junit.Test;
import org.kumoricon.model.order.Payment;

import java.math.BigDecimal;

import static org.junit.Assert.*;


public class PaymentValidatorTest {
    @Test(expected = ValidationException.class)
    public void validateCreditFails() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CREDIT);
        PaymentValidator.validate(p);
    }

    @Test
    public void validateCreditPasses() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CREDIT);
        p.setAuthNumber("12345");
        PaymentValidator.validate(p);
    }

    @Test(expected = ValidationException.class)
    public void validateCheckFails() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CHECK);
        PaymentValidator.validate(p);
    }

    @Test
    public void validateCheckPasses() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CHECK);
        p.setAuthNumber("12345");
        PaymentValidator.validate(p);
    }

    @Test(expected = ValidationException.class)
    public void validateAmountFailsNull() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CHECK);
        p.setAmount(null);
        PaymentValidator.validate(p);
    }

    @Test(expected = ValidationException.class)
    public void validateAmountFailsNegative() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CHECK);
        p.setAmount(BigDecimal.valueOf(-1));
        PaymentValidator.validate(p);
    }

    @Test
    public void validateAmountPasses() throws Exception {
        Payment p = testPayment();
        p.setPaymentType(Payment.PaymentType.CHECK);
        p.setAuthNumber("12345");
        PaymentValidator.validate(p);
    }


    private Payment testPayment() {
        Payment p = new Payment();
        p.setPaymentType(Payment.PaymentType.CASH);
        p.setAmount(BigDecimal.TEN);
        return p;
    }
}