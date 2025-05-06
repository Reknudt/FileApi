package org.pavlov.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

    public static final String ERROR_NOT_FOUND = "error.not.found";

    public static final String ERROR_FORBIDDEN = "error.forbidden";

    public static final String CLIENTID = "${keycloak.resource}";

    public static final String REALM = "${keycloak.realm}";
}
