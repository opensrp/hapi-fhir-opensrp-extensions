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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeResourceDefinition;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.api.model.*;
import ca.uhn.fhir.jpa.model.entity.*;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.storage.ResourcePersistentId;
import ca.uhn.fhir.rest.api.server.storage.TransactionDetails;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseParameters;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestLocationIFhirResourceDao implements IFhirResourceDao {

    private final Map<String, List<IBaseResource>> database;

    public TestLocationIFhirResourceDao() {
        super();
        database = TestUtils.getTestLocationsMap();
    }

    @Override
    public DaoMethodOutcome create(IBaseResource iBaseResource) {
        return null;
    }

    @Override
    public DaoMethodOutcome create(IBaseResource iBaseResource, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome create(IBaseResource iBaseResource, String s) {
        return null;
    }

    @Override
    public DaoMethodOutcome create(
            IBaseResource iBaseResource,
            String s,
            boolean b,
            @NotNull TransactionDetails transactionDetails,
            RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome create(
            IBaseResource iBaseResource, String s, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome delete(IIdType iIdType) {
        return null;
    }

    @Override
    public DaoMethodOutcome delete(
            IIdType iIdType,
            DeleteConflictList deleteConflictList,
            RequestDetails requestDetails,
            @NotNull TransactionDetails transactionDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome delete(IIdType iIdType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DeleteMethodOutcome deleteByUrl(
            String s, DeleteConflictList deleteConflictList, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DeleteMethodOutcome deleteByUrl(String s, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public ExpungeOutcome expunge(ExpungeOptions expungeOptions, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public ExpungeOutcome expunge(
            IIdType iIdType, ExpungeOptions expungeOptions, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public ExpungeOutcome forceExpungeInExistingTransaction(
            IIdType iIdType, ExpungeOptions expungeOptions, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public Class getResourceType() {
        return null;
    }

    @Override
    public IBundleProvider history(
            Date date, Date date1, Integer integer, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBundleProvider history(
            IIdType iIdType,
            Date date,
            Date date1,
            Integer integer,
            RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome patch(
            IIdType iIdType,
            String s,
            PatchTypeEnum patchTypeEnum,
            String s1,
            IBaseParameters iBaseParameters,
            RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBaseResource read(IIdType iIdType) {
        Location root = new Location();
        root.setId(iIdType);
        root.setName("Root Location 1");
        return root;
    }

    @Override
    public IBaseResource readByPid(ResourcePersistentId resourcePersistentId) {
        return null;
    }

    @Override
    public IBaseResource readByPid(ResourcePersistentId thePid, boolean theDeletedOk) {
        return IFhirResourceDao.super.readByPid(thePid, theDeletedOk);
    }

    @Override
    public IBaseResource read(IIdType iIdType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBaseResource read(IIdType iIdType, RequestDetails requestDetails, boolean b) {
        return null;
    }

    @Override
    public BaseHasResource readEntity(IIdType iIdType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public BaseHasResource readEntity(IIdType iIdType, boolean b, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public void reindex(IBaseResource iBaseResource, ResourceTable resourceTable) {}

    @Override
    public void removeTag(
            IIdType iIdType,
            TagTypeEnum tagTypeEnum,
            String s,
            String s1,
            RequestDetails requestDetails) {}

    @Override
    public void removeTag(IIdType iIdType, TagTypeEnum tagTypeEnum, String s, String s1) {}

    @Override
    public IBundleProvider search(SearchParameterMap searchParameterMap) {

        TestBundleProvider testBundleProvider = new TestBundleProvider();
        String parentLocationId = getSearchParameterValue(searchParameterMap);

        List<IBaseResource> attributedLocations = database.get(parentLocationId);

        testBundleProvider.setResources(attributedLocations);
        testBundleProvider.size();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return testBundleProvider;
    }

    private String getSearchParameterValue(SearchParameterMap searchParameterMap) {
        String referenceParamString =
                searchParameterMap.values().stream()
                        .map(it -> it.get(0))
                        .findFirst()
                        .get()
                        .get(0)
                        .toString();
        return referenceParamString.substring(
                referenceParamString.indexOf('=') + 1, referenceParamString.indexOf(']'));
    }

    @Override
    public IBundleProvider search(
            SearchParameterMap searchParameterMap, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBundleProvider search(
            SearchParameterMap searchParameterMap,
            RequestDetails requestDetails,
            HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public Set<ResourcePersistentId> searchForIds(
            SearchParameterMap theParams, RequestDetails theRequest) {
        return IFhirResourceDao.super.searchForIds(theParams, theRequest);
    }

    @Override
    public Set<ResourcePersistentId> searchForIds(
            SearchParameterMap theParams,
            RequestDetails theRequest,
            @Nullable IBaseResource theConditionalOperationTargetOrNull) {
        return IFhirResourceDao.super.searchForIds(
                theParams, theRequest, theConditionalOperationTargetOrNull);
    }

    @Override
    public DaoMethodOutcome update(IBaseResource iBaseResource) {
        return null;
    }

    @Override
    public DaoMethodOutcome update(IBaseResource iBaseResource, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome update(IBaseResource iBaseResource, String s) {
        return null;
    }

    @Override
    public DaoMethodOutcome update(
            IBaseResource iBaseResource, String s, boolean b, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome update(
            IBaseResource iBaseResource, String s, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public DaoMethodOutcome update(
            IBaseResource iBaseResource,
            String s,
            boolean b,
            boolean b1,
            RequestDetails requestDetails,
            @NotNull TransactionDetails transactionDetails) {
        return null;
    }

    @Override
    public MethodOutcome validate(
            IBaseResource iBaseResource,
            IIdType iIdType,
            String s,
            EncodingEnum encodingEnum,
            ValidationModeEnum validationModeEnum,
            String s1,
            RequestDetails requestDetails) {
        return null;
    }

    @Override
    public RuntimeResourceDefinition validateCriteriaAndReturnResourceDefinition(String s) {
        return null;
    }

    @Override
    public String getCurrentVersionId(IIdType theReferenceElement) {
        return IFhirResourceDao.super.getCurrentVersionId(theReferenceElement);
    }

    @Override
    public DeleteMethodOutcome deletePidList(
            String s,
            Collection collection,
            DeleteConflictList deleteConflictList,
            RequestDetails requestDetails) {
        return null;
    }

    @Override
    public void translateRawParameters(Map map, SearchParameterMap searchParameterMap) {}

    @Override
    public IBaseMetaType metaGetOperation(Class aClass, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBaseMetaType metaGetOperation(
            Class aClass, IIdType iIdType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBaseMetaType metaDeleteOperation(
            IIdType iIdType, IBaseMetaType iBaseMetaType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public IBaseMetaType metaAddOperation(
            IIdType iIdType, IBaseMetaType iBaseMetaType, RequestDetails requestDetails) {
        return null;
    }

    @Override
    public FhirContext getContext() {
        return null;
    }

    @Override
    public IBaseResource toResource(BaseHasResource baseHasResource, boolean b) {
        return null;
    }

    @Override
    public <R extends IBaseResource> R toResource(
            Class<R> theResourceType,
            IBaseResourceEntity theEntity,
            Collection<ResourceTag> theTagList,
            boolean theForHistoryOperation) {
        return null;
    }
}
