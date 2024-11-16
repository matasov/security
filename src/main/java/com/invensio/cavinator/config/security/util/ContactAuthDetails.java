package com.invensio.cavinator.config.security.util;

import java.beans.Transient;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.invensio.cavinator.config.security.util.user.UserGrantedAuthority;
import com.invensio.cavinator.db.models.recordowner.abstractmodel.RecordsOwnerDto;
import com.invensio.cavinator.db.models.security.model.ContactAuthDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactAuthDetails implements UserDetails {

    private static final long serialVersionUID = -2778488367626025013L;

    private Collection<? extends GrantedAuthority> authorities;
    private UUID currentOwnerRecordId;
    private String domain;
    private String authLink;
    private ContactAuthDto user;

    public ContactAuthDetails(ContactAuthDto user, UUID currentOwnerRecordId, String domain, String authLink) {
        this.user = user;
        this.domain = domain;
        this.authorities = translate(user.getOwnerRecords());
        this.currentOwnerRecordId = currentOwnerRecordId;
        this.authLink = authLink;
    }

    public ContactAuthDetails(ContactAuthDetails other) {
        this.user = other.user;
        this.currentOwnerRecordId = other.currentOwnerRecordId;
        this.domain = other.domain;
        this.authLink = other.authLink;
        this.authorities = other.authorities;
    }

    private Collection<? extends GrantedAuthority> translate(Map<UUID, RecordsOwnerDto> roles) {
        return roles.entrySet().stream().map(entry -> getAccessRole(entry.getValue())).collect(Collectors.toList());
    }

    private UserGrantedAuthority getAccessRole(RecordsOwnerDto recordsOwnerDto) {
        return new UserGrantedAuthority(recordsOwnerDto.getDynamicRoleId(), recordsOwnerDto.getRoleName());
    }

    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Transient
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Transient
    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "{\"id\":\"%1$s\",\"login\":\"%2$s\",\"password\":\"%3$s\",\"domain\":\"%4$s\",\"authorities\":\"%5$s\",\"currentOwnerRecord\":\"%6$s\",\"authLink\":\"%7$s\"}",
                user.getLocalId(), user.getLogin(), user.getPassword(), domain, authorities, currentOwnerRecordId,
                authLink);
    }

    @Transient
    public RecordsOwnerDto getCurrentOwnerRecord() {
        return user.getOwnerRecords().get(currentOwnerRecordId);
    }

    @Transient
    public synchronized void setCurrentOwnerRecord(RecordsOwnerDto currentOwnerRecord) {
        currentOwnerRecordId = currentOwnerRecord.getId();
        user.getOwnerRecords().put(currentOwnerRecordId, currentOwnerRecord);
    }

}
