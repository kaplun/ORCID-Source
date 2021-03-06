/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OtherNameDaoTest extends DBUnitTest {

    @Resource
    private OtherNameDao otherNameDao;

    @Resource
    private ProfileDao profileDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(otherNameDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindOtherNameByOrcid() {
        List<OtherNameEntity> otherNames = otherNameDao.getOtherName("4444-4444-4444-4443");
        assertNotNull(otherNames);
        assertEquals(2, otherNames.size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testUpdateOtherName() {
        try {
            otherNameDao.updateOtherName(null);
            fail();
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddOtherName() {
        Date profileLastModifiedOrig = profileDao.retrieveLastModifiedDate("4444-4444-4444-4443");
        assertEquals(2, otherNameDao.getOtherName("4444-4444-4444-4443").size());
        boolean result = otherNameDao.addOtherName("4444-4444-4444-4443", "OtherName");
        assertEquals(true, result);
        assertEquals(3, otherNameDao.getOtherName("4444-4444-4444-4443").size());
        assertFalse("Profile last modified date should have been updated", profileLastModifiedOrig.after(profileDao.retrieveLastModifiedDate("4444-4444-4444-4443")));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeleteOtherName() {
        Date now = new Date();
        Date justBeforeStart = new Date(now.getTime() - 1000);
        List<OtherNameEntity> otherNames = otherNameDao.getOtherName("4444-4444-4444-4443");
        assertNotNull(otherNames);
        assertEquals(2, otherNames.size());
        OtherNameEntity otherName = otherNames.get(0);
        assertTrue(otherNameDao.deleteOtherName(otherName));
        List<OtherNameEntity> updatedOtherNames = otherNameDao.getOtherName("4444-4444-4444-4443");
        assertNotNull(updatedOtherNames);
        assertEquals(1, updatedOtherNames.size());
        assertTrue("Profile last modified date should have been updated", justBeforeStart.before(profileDao.retrieveLastModifiedDate("4444-4444-4444-4443")));
    }

}