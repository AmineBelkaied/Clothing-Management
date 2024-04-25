package com.clothing.management.tenant;

import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import static com.clothing.management.auth.constant.AppConstants.MASTER_DB;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = DBContextHolder.getCurrentDb();
        return StringUtils.isNotBlank(tenant) ? tenant : MASTER_DB;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
