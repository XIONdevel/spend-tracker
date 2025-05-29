package com.noix.spendtracker.bank.mono;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoAccountDTO {

    private String id;
    private Long balance;
    private Long creditLimit;
    private String type;
    private int currencyCode;
    private List<String> maskedPan;
    private String iban;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MonoAccountDTO that)) return false;
        return currencyCode == that.currencyCode && Objects.equals(id, that.id) && Objects.equals(balance, that.balance) && Objects.equals(creditLimit, that.creditLimit) && Objects.equals(type, that.type) && Objects.equals(maskedPan, that.maskedPan) && Objects.equals(iban, that.iban);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, creditLimit, type, currencyCode, maskedPan, iban);
    }
}
