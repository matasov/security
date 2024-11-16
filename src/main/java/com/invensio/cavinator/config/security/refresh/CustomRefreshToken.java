package com.invensio.cavinator.config.security.refresh;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.invensio.cavinator.config.security.login.AuthRequest;
import java.util.UUID;

public class CustomRefreshToken extends UsernamePasswordAuthenticationToken {

    private String currentOwnerRecordId;
    private transient AuthRequest authRequest;

    public CustomRefreshToken(String principal, Object credentials, AuthRequest authRequest,
            String currentOwnerRecordId) {
        super(principal, credentials);
        this.currentOwnerRecordId = currentOwnerRecordId;
        this.authRequest = authRequest;
    }

    public AuthRequest getAuthRequest() {
        return authRequest;
    }

    public String getRoleId() {
        return currentOwnerRecordId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        CustomRefreshToken that = (CustomRefreshToken) o;

        if (currentOwnerRecordId != null ? !currentOwnerRecordId.equals(that.currentOwnerRecordId)
                : that.currentOwnerRecordId != null)
            return false;
        return authRequest != null ? authRequest.equals(that.authRequest) : that.authRequest == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (currentOwnerRecordId != null ? currentOwnerRecordId.hashCode() : 0);
        result = 31 * result + (authRequest != null ? authRequest.hashCode() : 0);
        return result;
    }

}
