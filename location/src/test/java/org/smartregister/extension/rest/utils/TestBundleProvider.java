/*
 * Copyright 2023 Ona Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.extension.rest.utils;

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestBundleProvider implements IBundleProvider {
    private List<IBaseResource> resources = new ArrayList<>();

    @Override
    public IPrimitiveType<Date> getPublished() {
        return null;
    }

    public void setResources(List<IBaseResource> resources) {
        this.resources = resources;
    }

    @NotNull
    @Override
    public List<IBaseResource> getResources(int startIndex, int endIndex) {
        return resources != null ? resources.subList(startIndex, endIndex) : new ArrayList<>();
    }

    @Nullable
    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public Integer preferredPageSize() {
        return null;
    }

    @Nullable
    @Override
    public Integer size() {
        return resources != null ? resources.size() : 0;
    }

    @Override
    public String getCurrentPageId() {
        return IBundleProvider.super.getCurrentPageId();
    }

    @Override
    public String getNextPageId() {
        return IBundleProvider.super.getNextPageId();
    }

    @Override
    public String getPreviousPageId() {
        return IBundleProvider.super.getPreviousPageId();
    }

    @Override
    public Integer getCurrentPageOffset() {
        return IBundleProvider.super.getCurrentPageOffset();
    }

    @Override
    public Integer getCurrentPageSize() {
        return IBundleProvider.super.getCurrentPageSize();
    }

    @NotNull
    @Override
    public List<IBaseResource> getAllResources() {
        return IBundleProvider.super.getAllResources();
    }

    @Override
    public boolean isEmpty() {
        return IBundleProvider.super.isEmpty();
    }

    @Override
    public int sizeOrThrowNpe() {
        return IBundleProvider.super.sizeOrThrowNpe();
    }

    @Override
    public List<String> getAllResourceIds() {
        return IBundleProvider.super.getAllResourceIds();
    }
}
