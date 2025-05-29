package com.noix.spendtracker.bank.mono;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoJarDTO {

    private String id;
    private String title;
    private String description;
    private String currencyCode;
    private Long balance;
    private Long goal;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MonoJarDTO that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(currencyCode, that.currencyCode) && Objects.equals(balance, that.balance) && Objects.equals(goal, that.goal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, currencyCode, balance, goal);
    }
}
