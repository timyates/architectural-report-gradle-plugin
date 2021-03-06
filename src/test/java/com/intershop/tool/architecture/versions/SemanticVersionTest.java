package com.intershop.tool.architecture.versions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class SemanticVersionTest
{
    @Test
    public void testMajorMinorPatch()
    {
        SemanticVersion version_1_2_3 = SemanticVersion.valueOf("1.2.3");
        assertTrue("is semantic version", version_1_2_3.isIncrementable());
        assertEquals("major", 1, version_1_2_3.getMajor());
        assertEquals("minor", 2, version_1_2_3.getMinor());
        assertEquals("patch", 3, version_1_2_3.getPatch());
    }

    @Test
    public void testMajorMinor()
    {
        SemanticVersion version_16_3 = SemanticVersion.valueOf("16.3");
        assertTrue("is semantic version", version_16_3.isIncrementable());
        assertEquals("major", 16, version_16_3.getMajor());
        assertEquals("minor", 3, version_16_3.getMinor());
        assertEquals("patch", 0, version_16_3.getPatch());
    }

    @Test
    public void testIncrementVersions()
    {
        assertTrue("release candidates are not semantic", SemanticVersion.valueOf("2.7.6-rc1").isIncrementable());
        assertTrue("development release are not semantic", SemanticVersion.valueOf("2.7.6-dev1").isIncrementable());
        assertEquals("development release are not semantic", 1, SemanticVersion.valueOf("2.7.6-dev1").getIncrement());
        assertEquals("development release are not semantic", ReleaseType.DEV, SemanticVersion.valueOf("2.7.6-dev1").getIncrementState());
    }

    @Test
    public void testNonSemanticVersions()
    {
        assertFalse("one number greater than 1000 is not semantic", SemanticVersion.valueOf("23423842").isIncrementable());
        assertFalse("one number greater than 1000 is not semantic", SemanticVersion.valueOf("23423842.3242").isIncrementable());
    }

    @Test
    @Ignore
    public void testFeatureBranchVersions()
    {
        assertTrue("development release are not semantic", SemanticVersion.valueOf("FB-12.0.0-ANYISUSE-12233-dev1").isIncrementable());
        assertEquals("development release are not semantic", 1, SemanticVersion.valueOf("FB-12.0.0-ANYISUSE-12233-dev1").getIncrement());
    }

    @Test
    public void testShortSemanticVersions()
    {
        assertTrue("one number less 1000 is semantic", SemanticVersion.valueOf("2").isIncrementable());
        assertTrue("two digits are valid", SemanticVersion.valueOf("2.7").isIncrementable());
    }

    @Test
    public void testExtendedSemanticVersions()
    {
        assertTrue("GA is valid with dot", SemanticVersion.valueOf("2.7.6.GA").isIncrementable());
        assertTrue("FINAL is valid with dot", SemanticVersion.valueOf("2.7.6.FINAL").isIncrementable());
    }

    @Test
    public void testExtendedSemanticDashVersions()
    {
        assertTrue("GA is valid with dash", SemanticVersion.valueOf("2.7.6-GA").isIncrementable());
        assertEquals("patch version of GA is correct", 6, SemanticVersion.valueOf("2.7.6-GA").getPatch());
        assertTrue("FINAL is valid with dash", SemanticVersion.valueOf("2.7.6-FINAL").isIncrementable());
    }

    @Test
    public void testFourDigits()
    {
        assertTrue("four digits are valid", SemanticVersion.valueOf("2.7.6.1").isIncrementable());
    }

    @Test
    public void testFiveAndMoreDigits()
    {
        assertFalse("five digits are invalid", SemanticVersion.valueOf("5.4.3.2.1").isIncrementable());
        assertFalse("six digits are invalid", SemanticVersion.valueOf("6.5.4.3.2.1").isIncrementable());
    }

    @Test
    public void testJettyVersion()
    {
        assertEquals("jetty version 14", 0, SemanticVersion.valueOf("9.3.14.v20161028").getIncrement());
    }
}
