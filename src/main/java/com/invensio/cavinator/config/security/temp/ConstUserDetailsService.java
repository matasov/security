package com.invensio.cavinator.config.security.temp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.UUID;

import com.invensio.cavinator.config.security.util.ContactAuthDetails;
import com.invensio.cavinator.db.models.recordowner.abstractmodel.RecordsOwnerDto;
import com.invensio.cavinator.db.models.security.model.ContactAuthDto;

@Component
public class ConstUserDetailsService implements UserDetailsService {
    private final Map<String, UserDetails> users = new HashMap<>();

    public ConstUserDetailsService() {
        UUID recordId = UUID.randomUUID();
        users.put("sudo",
                getContactAuthDetails(recordId,
                        new ContactAuthDto(UUID.fromString("c36e075d-b6e0-489d-a326-f74515110b09"),
                                UUID.fromString("c36e075d-b6e0-489d-a326-f74515110b09"), "user",
                                "$2a$12$F.jq3gsHk1fnj2cxnLWpQOYIo9GfvltBj3NXBzWLPAaXkeqW7k3h6",
                                getOwnerRecords(recordId), null,
                                null)));
    }

    private Map<UUID, RecordsOwnerDto> getOwnerRecords(UUID recordId) {
        return Map.of(recordId, new RecordsOwnerDto(recordId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "roleName"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.get(username);
    }

    private ContactAuthDetails getContactAuthDetails(UUID recordId, ContactAuthDto user) {
        return new ContactAuthDetails(user, recordId, "domainKey", "authLink");
    }

}
