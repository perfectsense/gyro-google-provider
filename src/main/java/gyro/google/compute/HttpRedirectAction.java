/*
 * Copyright 2021, Brightspot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.compute;

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class HttpRedirectAction extends Diffable implements Copyable<com.google.cloud.compute.v1.HttpRedirectAction> {

    private String hostRedirect;
    private String pathRedirect;
    private String prefixRedirect;
    private String redirectResponseCode;
    private Boolean httpsRedirect;
    private Boolean stripQuery;

    /**
     * The host that will be used in the redirect response instead of the one that was supplied in the request.
     */
    public String getHostRedirect() {
        return hostRedirect;
    }

    public void setHostRedirect(String hostRedirect) {
        this.hostRedirect = hostRedirect;
    }

    /**
     * The path that will be used in the redirect response instead of the one that was supplied in the request.
     */
    public String getPathRedirect() {
        return pathRedirect;
    }

    public void setPathRedirect(String pathRedirect) {
        this.pathRedirect = pathRedirect;
    }

    /**
     * The prefix that replaces the prefixMatch specified in the HttpRouteRuleMatch, retaining the remaining portion of the URL before redirecting the request.
     */
    public String getPrefixRedirect() {
        return prefixRedirect;
    }

    public void setPrefixRedirect(String prefixRedirect) {
        this.prefixRedirect = prefixRedirect;
    }

    /**
     * The HTTP Status code to use for this RedirectAction.
     */
    public String getRedirectResponseCode() {
        return redirectResponseCode;
    }

    public void setRedirectResponseCode(String redirectResponseCode) {
        this.redirectResponseCode = redirectResponseCode;
    }

    /**
     * If set to ``true``, the URL scheme in the redirected request is set to https.
     */
    public Boolean getHttpsRedirect() {
        return httpsRedirect;
    }

    public void setHttpsRedirect(Boolean httpsRedirect) {
        this.httpsRedirect = httpsRedirect;
    }

    /**
     * If set to ``true``, any accompanying query portion of the original URL is removed prior to redirecting the request.
     */
    public Boolean getStripQuery() {
        return stripQuery;
    }

    public void setStripQuery(Boolean stripQuery) {
        this.stripQuery = stripQuery;
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.HttpRedirectAction model) {
        setHostRedirect(model.getHostRedirect());
        setPathRedirect(model.getPathRedirect());
        setPrefixRedirect(model.getPrefixRedirect());
        setRedirectResponseCode(model.getRedirectResponseCode().toString());
        setStripQuery(model.getStripQuery());
        setHttpsRedirect(model.getHttpsRedirect());

    }

    @Override
    public String primaryKey() {
        return "";
    }

    protected com.google.cloud.compute.v1.HttpRedirectAction toHttpRedirectAction() {
        com.google.cloud.compute.v1.HttpRedirectAction.Builder httpRedirectAction =
            com.google.cloud.compute.v1.HttpRedirectAction.newBuilder();

        if (getHostRedirect() != null) {
            httpRedirectAction.setHostRedirect(getHostRedirect());
        }

        if (getPathRedirect() != null) {
            httpRedirectAction.setPathRedirect(getPathRedirect());
        }

        if (getPrefixRedirect() != null) {
            httpRedirectAction.setPrefixRedirect(getPrefixRedirect());
        }

        if (getRedirectResponseCode() != null) {
            httpRedirectAction.setRedirectResponseCode(getRedirectResponseCode());
        }

        httpRedirectAction.setStripQuery(getStripQuery());
        httpRedirectAction.setHttpsRedirect(getHttpsRedirect());

        return httpRedirectAction.build();
    }
}
