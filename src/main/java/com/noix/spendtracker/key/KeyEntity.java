package com.noix.spendtracker.key;

import com.noix.spendtracker.user.User;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeyEntity {

    private String key;
    private User user;
    private Bank bank;

}
