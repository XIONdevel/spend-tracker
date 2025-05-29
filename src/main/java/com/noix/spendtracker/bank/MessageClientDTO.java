package com.noix.spendtracker.bank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class MessageClientDTO implements ClientDTO {

    private String message;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MessageClientDTO that)) return false;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }
}
