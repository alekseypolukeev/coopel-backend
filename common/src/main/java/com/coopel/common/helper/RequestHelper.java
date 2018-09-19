package com.coopel.common.helper;

import com.coopel.common.rest.PathConstants;

import javax.servlet.http.HttpServletRequest;

public final class RequestHelper {

    private RequestHelper() {
    }

    public static String getFullPath(HttpServletRequest request) {
        return request.getContextPath() + request.getServletPath();
    }

    public static boolean isSecuredResource(HttpServletRequest request) {
        String path = getFullPath(request);
        return !isAuthPath(path) &&
                !isHealthPath(path) &&
                !isSwaggerApiDoc(path) &&
                !isPublicApi(path);
    }

    public static boolean isPublicApi(String path) {
        return path.startsWith(PathConstants.PUBLIC);
    }

    public static boolean isSwaggerApiDoc(String path) {
        return path.startsWith("/v2/api-docs");
    }

    public static boolean isAuthPath(String path) {
        return path.startsWith("/oauth");
    }

    public static boolean isAuthPath(HttpServletRequest request) {
        return isAuthPath(getFullPath(request));
    }

    public static boolean isHealthPath(String path) {
        return path.startsWith("/health");
    }

    public static boolean isHealthPath(HttpServletRequest request) {
        return isHealthPath(getFullPath(request));
    }
}
