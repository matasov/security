package com.invensio.cavinator.db;

import java.util.UUID;

public interface DBConstants {

    public String OAUTH_CLIENT_DETAILS = "oauth_client_details";

    public String OAUTH_CLIENT_AVAILABLE = "oauth_client_available";

    public String TBL_STRUCTURE_FIELDS = "class_structure_fields";
    public String TBL_COLLECTIONS_STRUCTURE = "class_collections_fields_use";
    public String TBL_REGISTRY_CLASSES = "registry_classes";
    public String TBL_REGISTRY_DYNAMIC_ROLE_COLLECTION = "registry_dynamic_role_collection";
    public String TBL_CLASS_STRUCTURE_USE_FAST = "class_structure_cache";
    public String TBL_MANAGEMENT_RULES = "class_management_rules";
    public String TBL_MANAGEMENT_SETTINGS = "class_management_settings";

    public String TBL_TOKEN_IMPLEMENTED_RECORDS = "cc_%1$s_implemented_records";

    public String TBL_LITERAL = "class_literal";

    public String TBL_OWN_RECORD_DYNAMIC = "class_own_dynamic";
    public String TBL_RECORD_OWNER = "class_record_owner";

    public String TBL_FILTER_RECORD = "class_filter_record";
    public String TBL_FILTER_GROUP = "class_filter_group";
    public String TBL_STAFF_DESCRIPTIONS = "class_stuff_descriptions";
    public String TBL_CLASS_LOG = "class_stuff_log";
    public String TBL_CLASS_LOG_SETTINGS = "class_stuff_log_settings";
    public String TBL_PRODUCTION_LOG = "production_log";
    public String TBL_REGISTRY_COMPANIES_LINKS = "registry_companies_links";
    public String TBL_LOCAL_SETTINGS = "registry_local_settings";
    public String TBL_STRUCTURE_CACHE = "registry_structure_cache";

    public String TBL_PRODUCTION_DB_ITEM = "class_production_db_item";
    public String TBL_PRODUCTION_DB_COMPARE_ITEM = "class_production_db_compare_item";
    // site tables
    public String TBL_SITE_DOMAINS = "site_domains";
    // relations table
    public String TBL_CLASS_COLLECTION_REQUEST_RELATIONS = "class_collection_request_relations";
    public String TBL_COMPANY_CONTACT_CLASS_RELATION = "registry_company_contact_class";
    public String TBL_SIGNUP_FIELDS = "registry_signup_fields";
    public String TBL_DICTIONARY_SYSTEM = "registry_dictionary_system";
    public String TBL_COLLECTION_CASE = "registry_collection_case";
    public String TBL_OAUTH_AVAILABLE_CLASSES = "oauth_client_available_classes";
    public final String TBL_OAUTH_CONFIRMATION_CODE = "oauth_confirmation_code";
    public String TBL_CLIENT_WEBSOCKET = "registry_client_network_settings";

    // recovery
    public String REGISTRY_RECOVERY = "registry_recovery";
    public String REGISTRY_CLASSES = "registry_classes";

    // class static tables
    public String TBL_RECAPTCHA_BY_DOMAIN = "recaptcha_by_domain";

    // const classes for permissions:
    public String ROLE_ID = "roleId";
    public String COMPANY_ID = "companyId";
    public String SERVICE_ID = "serviceId";
    public String CONTACT_ID = "contactId";
    public String LOCAL_CONTACT_ID = "localContactId";
    public String LOCAL_CLASS_ID = "localContactClassId";
    public String CURRENT_OWNER_ID = "ownerId";
    public String DYNAMIC_ROLE_ID = "dynamicRoleId";
    public String DOMAIN_ID = "domainKey";
    // work params
    public String SYSTEM_UUID = "3a1ee914-ec32-4e83-8f04-df0897daf8e9";

    // public static List<UUID> superAdminsPryorities = new ArrayList() {
    // {
    // add(UUID.fromString(SystemRole.SUPERADMIN_ROLE.getId()));
    // add(UUID.fromString(SystemRole.GLOBAL_SUPERVISOR_ROLE.getId()));
    // }
    // };

    public static String getFirstPartOfUUID(UUID value) {
        if (value == null) {
            return "null";
        }
        return new StringBuilder(8).append(value.toString()).substring(0, 8);
    }

    public static String getFirstPartOfUUID(String value) {
        if (value == null) {
            return "null";
        }
        UUID parseValue = null;
        parseValue = UUID.fromString(value);
        return getFirstPartOfUUID(parseValue);
    }

    public static final String CACHE_ENVIRONMENT_VALUE = "RESOURCE";
    public static final String CACHE_ENVIRONMENT_PREFIX = CACHE_ENVIRONMENT_VALUE + "_";

}
