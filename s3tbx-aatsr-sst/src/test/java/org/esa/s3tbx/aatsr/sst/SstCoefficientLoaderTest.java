/*
 * Copyright (C) 2010 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.s3tbx.aatsr.sst;

import org.esa.snap.core.gpf.OperatorException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class SstCoefficientLoaderTest {

    private static final String expNadirDesc = "test coefficients nadir";
    private static final String expDualDesc = "test coefficients dual";
    private SstCoefficientLoader loader;

    @Before
    public void setUp() {
        loader = new SstCoefficientLoader();
        assertNotNull(loader);
    }

    /**
     * Tests the load method for expected behaviour when fed with different erroneous arguments
     */
    @Test
    public void testLoadWithInvalidArguments() throws IOException {

        // shall not accept null arguments
        try {
            loader.load(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            //ignore
        }

        // shall throw IOException when fed with nonexistent URL
        URL noneExistent = createNonExistentUrl();

        try {
            loader.load(noneExistent);
            fail("IOException expected");
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Tests if a correctly formatted file is read in
     */
    @Test
    public void testLoadCorrectFile() throws IOException, URISyntaxException {
        URL nadirCoeff = createNadirCoeffUrl();
        URL dualCoeff = createDualCoeffUrl();
        SstCoefficientSet coeffs;

        coeffs = loader.load(nadirCoeff);
        // we expect something in return
        assertNotNull(coeffs);

        coeffs = loader.load(dualCoeff);
        // we expect something in return
        assertNotNull(coeffs);
    }

    /**
     * Tests that the content for the nadir coeffcient file is read correctly
     */
    @Test
    public void testNadirStuffCorrectRead() throws IOException, URISyntaxException {
        URL nadirCoeff = createNadirCoeffUrl();

        SstCoefficientSet coeffSet;
        SstCoefficients coeffs;

        coeffSet = loader.load(nadirCoeff);
        // we expect something in return
        assertNotNull(coeffSet);

        // we expect a correct description string
        assertEquals(expNadirDesc, coeffSet.getDescription());

        // we expect two parameter ranges
        // 0: 0 - 124
        // 1: 125 - 511
        assertEquals(2, coeffSet.getNumCoefficients());

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);
        assertEquals(0, coeffs.getStart());
        assertEquals(124, coeffs.getEnd());

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);
        assertEquals(125, coeffs.getStart());
        assertEquals(511, coeffs.getEnd());

        // we expect two a parameter sets of:
        // a0 = 1.23, a1 = 2.34, a2 = 3.45 and
        float[] exp_a_one = new float[]{1.23f, 2.34f, 3.45f};
        // a0 = 0.12, a1 = 1.23, a2 = 2.34
        float[] exp_a_two = new float[]{0.12f, 1.23f, 2.34f};

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);

        float[] a_coeffs = coeffs.get_A_Coeffs();
        assertNotNull(a_coeffs);
        assertEquals(3, a_coeffs.length);

        for (int n = 0; n < a_coeffs.length; n++) {
            assertEquals(exp_a_one[n], a_coeffs[n], 1e-6);
        }

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);

        a_coeffs = coeffs.get_A_Coeffs();
        assertNotNull(a_coeffs);
        assertEquals(3, a_coeffs.length);

        for (int n = 0; n < a_coeffs.length; n++) {
            assertEquals(exp_a_two[n], a_coeffs[n], 1e-6);
        }

        // we expect two parametersets b:
        float[] exp_b_one = new float[]{5.67f, 6.78f, 8.89f, 8.90f};
        float[] exp_b_two = new float[]{3.45f, 4.56f, 5.67f, 6.78f};

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);

        float[] b_coeffs = coeffs.get_B_Coeffs();
        assertNotNull(b_coeffs);
        assertEquals(4, b_coeffs.length);

        for (int n = 0; n < b_coeffs.length; n++) {
            assertEquals(exp_b_one[n], b_coeffs[n], 1e-6);
        }

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);

        b_coeffs = coeffs.get_B_Coeffs();
        assertNotNull(b_coeffs);
        assertEquals(4, b_coeffs.length);

        for (int n = 0; n < b_coeffs.length; n++) {
            assertEquals(exp_b_two[n], b_coeffs[n], 1e-6);
        }

        // we expect NO parameters for c and c
        assertNull(coeffs.get_C_Coeffs());
        assertNull(coeffs.get_D_Coeffs());

    }

    /**
     * Tests that the content for the dual coeffcient file is read correctly
     */
    @Test
    public void testDualStuffCorrectRead() throws IOException, URISyntaxException {
        URL dualCoeff = createDualCoeffUrl();

        SstCoefficientSet coeffSet;
        SstCoefficients coeffs;

        coeffSet = loader.load(dualCoeff);
        // we expect something in return
        assertNotNull(coeffSet);

        // we expect a correct description string
        assertEquals(expDualDesc, coeffSet.getDescription());

        // we expect two parameter ranges
        // 0: 0 - 255
        // 1: 256 - 511
        assertEquals(2, coeffSet.getNumCoefficients());

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);
        assertEquals(0, coeffs.getStart());
        assertEquals(255, coeffs.getEnd());

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);
        assertEquals(256, coeffs.getStart());
        assertEquals(511, coeffs.getEnd());

        // we expect two parameter sets c
        float[] exp_c_one = new float[]{1.23f, 2.34f, 3.45f, 4.56f, 5.67f};
        float[] exp_c_two = new float[]{3.45f, 4.56f, 5.67f, 6.78f, 7.89f};

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);

        float[] c_coeffs = coeffs.get_C_Coeffs();
        assertNotNull(c_coeffs);
        assertEquals(5, c_coeffs.length);

        for (int n = 0; n < c_coeffs.length; n++) {
            assertEquals(exp_c_one[n], c_coeffs[n], 1e-6);
        }

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);

        c_coeffs = coeffs.get_C_Coeffs();
        assertNotNull(c_coeffs);
        assertEquals(5, c_coeffs.length);

        for (int n = 0; n < c_coeffs.length; n++) {
            assertEquals(exp_c_two[n], c_coeffs[n], 1e-6);
        }

        // we expect two parameter sets d
        float[] exp_d_one = new float[]{6.78f, 7.89f, 8.90f, 9.01f, 0.12f, 1.23f, 2.34f};
        float[] exp_d_two = new float[]{8.90f, 9.01f, 0.12f, 1.23f, 2.34f, 3.45f, 4.56f};

        coeffs = coeffSet.getCoefficientsAt(0);
        assertNotNull(coeffs);

        float[] d_coeffs = coeffs.get_D_Coeffs();
        assertNotNull(d_coeffs);
        assertEquals(7, d_coeffs.length);

        for (int n = 0; n < d_coeffs.length; n++) {
            assertEquals(exp_d_one[n], d_coeffs[n], 1e-6);
        }

        coeffs = coeffSet.getCoefficientsAt(1);
        assertNotNull(coeffs);

        d_coeffs = coeffs.get_D_Coeffs();
        assertNotNull(d_coeffs);
        assertEquals(7, d_coeffs.length);

        for (int n = 0; n < d_coeffs.length; n++) {
            assertEquals(exp_d_two[n], d_coeffs[n], 1e-6);
        }

    }

    /**
     * Tests that when the coefficient file contains no description none is read
     */
    @Test
    public void testNoDescriptionCorrectRead() throws IOException, URISyntaxException {
        URL noDescFile = createNoDescriptionCoeffUrl();

        SstCoefficientSet coeffs;

        coeffs = loader.load(noDescFile);
        // we expect something in return
        assertNotNull(coeffs);

        // we expect an empty description string
        assertEquals("", coeffs.getDescription());
    }


    /**
     * Tests that an incorrect set up coefficient file is checked and the errors are found
     */
    @Test
    public void testIncorrectNumberOfMaps() throws IOException, URISyntaxException {
        URL illegalFile = createOneMapTooMuchUrl();

        try {
            loader.load(illegalFile);
            fail("OperatorException expected");
        } catch (OperatorException ignored) {
        }

        illegalFile = createMapOverlapUrl();
        try {
            loader.load(illegalFile);
            fail("OperatorException expected");
        } catch (OperatorException ignored) {
        }

        illegalFile = create_A_and_D_Url();
        try {
            loader.load(illegalFile);
            fail("OperatorException expected");
        } catch (OperatorException ignored) {
        }
    }

    /**
     * Tests that load description reacts as expected when fed with illegal arguments
     */
    @Test
    public void testLoadDescriptionInterface() throws IOException {
        try {
            loader.getDescription(null);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ignored) {
        }

        URL nonExistent = createNonExistentUrl();

        String description = loader.getDescription(nonExistent);
        assertEquals("", description);
    }

    /**
     * Tests that we really load the description strings expected
     */
    @Test
    public void testLoadDescription() throws IOException, URISyntaxException {
        String description;
        URL url;

        url = createDualCoeffUrl();
        description = loader.getDescription(url);
        assertEquals(expDualDesc, description);

        url = createNadirCoeffUrl();
        description = loader.getDescription(url);
        assertEquals(expNadirDesc, description);
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates a (hopefully :-) nonexistent url
     */
    private URL createNonExistentUrl() throws MalformedURLException {
        return new URL("file", "", "n:\nonexistentTestFile");
    }

    /**
     * Creates an url pointing to the nadir coefficient test file
     */
    private URL createNadirCoeffUrl() throws MalformedURLException, URISyntaxException {
        File nadirCoeff = getTestFile("sstTestCoefficientsNadir.coef");

        assertEquals(true, nadirCoeff.exists());

        return nadirCoeff.toURI().toURL();
    }

    /**
     * Creates an url pointing to the dual coefficient test file
     */
    private URL createDualCoeffUrl() throws MalformedURLException, URISyntaxException {
        File dualCoeff = getTestFile("sstTestCoefficientsDual.coef");

        assertEquals(true, dualCoeff.exists());

        return dualCoeff.toURI().toURL();
    }

    /**
     * Creates an url pointing to the coefficient test file with no description string
     */
    private URL createNoDescriptionCoeffUrl() throws MalformedURLException, URISyntaxException {
        File noDescCoeff = getTestFile("sstTestCoefficientsNoDescription.coef");

        assertEquals(true, noDescCoeff.exists());

        return noDescCoeff.toURI().toURL();
    }

    /**
     * Creates an url pointing to the coefficient test file with one map too much
     */
    private URL createOneMapTooMuchUrl() throws MalformedURLException, URISyntaxException {
        File noDescCoeff = getTestFile("sstTestOneMapTooMuch.coef");

        assertEquals(true, noDescCoeff.exists());

        return noDescCoeff.toURI().toURL();
    }

    /**
     * Creates an url pointing to the coefficient test file with overlapping map ranges
     */
    private URL createMapOverlapUrl() throws MalformedURLException, URISyntaxException {
        File noDescCoeff = getTestFile("sstTestMapRangeOverlap.coef");

        assertEquals(true, noDescCoeff.exists());

        return noDescCoeff.toURI().toURL();
    }

    /**
     * Creates an url pointing to the coefficient test file with a and d coefficients
     */
    private URL create_A_and_D_Url() throws MalformedURLException, URISyntaxException {
        File noDescCoeff = getTestFile("sstTest_a_and_d.coef");

        assertEquals(true, noDescCoeff.exists());

        return noDescCoeff.toURI().toURL();
    }

    private File getTestFile(final String s) throws URISyntaxException, MalformedURLException {
        URL resource = getClass().getResource(s);
        // fixme - The tests will fail in Maven2 because blanks are note encoded in
        // fixme - the URL retrieved by getResource(). So we have to encode it by ourself.
        // fixme - see: http://jira.codehaus.org/browse/MNG-2050?page=comments#action_59460
        String encodedUrl = resource.toString().replace(" ", "%20");
        resource = new URL(encodedUrl);
        return new File(resource.toURI());
    }

}
