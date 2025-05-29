package com.noix.spendtracker.bank.mono;

import com.noix.spendtracker.bank.ClientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoClientDTO implements ClientDTO {

    private String clientId;
    private String name;
    private List<MonoAccountDTO> accounts;
    private List<MonoJarDTO> jars;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MonoClientDTO that)) return false;
        return Objects.equals(clientId, that.clientId) && Objects.equals(name, that.name) && Objects.equals(accounts, that.accounts) && Objects.equals(jars, that.jars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, name, accounts, jars);
    }
}
