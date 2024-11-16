package com.invensio.cavinator.db.models.security.model;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.invensio.cavinator.db.models.recordowner.abstractmodel.RecordsOwnerDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactAuthDto implements Serializable {
    private static final long serialVersionUID = -2007853460519522340L;
    private UUID id;
    private UUID localId;
    private String login;
    private transient String password;
    private Map<UUID, RecordsOwnerDto> ownerRecords;
    private Map<String, String> contactExtraData;
    private Map<String, String> companyContactExtraData;

    @Transient
    public String getPassword() {
        return password;
    }
}
