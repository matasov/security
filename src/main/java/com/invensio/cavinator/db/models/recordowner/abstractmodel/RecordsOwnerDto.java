package com.invensio.cavinator.db.models.recordowner.abstractmodel;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.invensio.cavinator.db.DBConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RecordsOwnerDto implements Serializable {

	private static final long serialVersionUID = -1816296648557553306L;

	private UUID id;
	private UUID contactId;
	private UUID companyId;
	private UUID serviceId;
	private UUID roleId;
	private UUID dynamicRoleId;
	private String roleName;

	@Transient
	public Map<String, String> getValues() {
		return Map.of("id", id.toString(), DBConstants.CONTACT_ID, contactId.toString(), DBConstants.COMPANY_ID,
				companyId.toString(), DBConstants.SERVICE_ID, serviceId.toString(), DBConstants.ROLE_ID,
				roleId.toString(), DBConstants.DYNAMIC_ROLE_ID, dynamicRoleId.toString());
	}

}
