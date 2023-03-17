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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class SstCoefficientsTest {

    /**
     * Check the correct functionality of the default constructor
     */
    @org.junit.Test
    public void testDefaultConstructor() {
        SstCoefficients coeffs = new SstCoefficients();

        // start and end must be 0
        assertEquals(0, coeffs.getStart());
        assertEquals(0, coeffs.getEnd());

        // all coefficients must be null
        assertNull(coeffs.get_A_Coeffs());
        assertNull(coeffs.get_B_Coeffs());
        assertNull(coeffs.get_C_Coeffs());
        assertNull(coeffs.get_D_Coeffs());
    }

    /**
     * Check the correct functionality of the parametrized constructor
     */
    @org.junit.Test
    public void testParameterConstructor() {
        int start = 12;
        int end = 34;

        SstCoefficients range = new SstCoefficients(start, end);

        // start and end must be as we set them
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());

        // when end < start - illegal argument exception
        start = 34;
        end = 8;

        try {
            new SstCoefficients(start, end);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Checks that we get back what we set and that error checking works
     */
    @org.junit.Test
    public void testSetGetRange() {
        SstCoefficients range = new SstCoefficients();

        // start and end must be 0
        assertEquals(0, range.getStart());
        assertEquals(0, range.getEnd());

        int start = 12;
        int end = 13;

        range.setRange(start, end);

        // start and end must be as we set
        assertEquals(start, range.getStart());
        assertEquals(end, range.getEnd());

        // when end < start - illegal argument exception
        start = 34;
        end = 8;

        try {
            range.setRange(start, end);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Tests the correct functionality of setter and getter for the a coefficients set
     */
    @org.junit.Test
    public void testSetGetACoefficients() {
        SstCoefficients coeffs = new SstCoefficients();

        // initially must return null
        assertEquals(null, coeffs.get_A_Coeffs());

        // must not accept null as argument
        try {
            coeffs.set_A_Coeffs(null);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }

        // when we set something we expect to get the same thing back
        float[] one = new float[]{1.f, 2.f, 3.f};
        float[] two = new float[]{4.f, 5.f, 6.f};

        coeffs.set_A_Coeffs(one);
        assertEquals(one, coeffs.get_A_Coeffs());

        coeffs.set_A_Coeffs(two);
        assertEquals(two, coeffs.get_A_Coeffs());
    }

    /**
     * Tests the correct functionality of setter and getter for the b coefficients set
     */
    @org.junit.Test
    public void testSetGetBCoefficients() {
        SstCoefficients coeffs = new SstCoefficients();

        // initially must return null
        assertEquals(null, coeffs.get_B_Coeffs());

        // must not accept null as argument
        try {
            coeffs.set_B_Coeffs(null);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }

        // when we set something we expect to get the same thing back
        float[] one = new float[]{1.f, 2.f, 3.f, 4.f};
        float[] two = new float[]{5.f, 6.f, 7.f, 8.f};

        coeffs.set_B_Coeffs(one);
        assertEquals(one, coeffs.get_B_Coeffs());

        coeffs.set_B_Coeffs(two);
        assertEquals(two, coeffs.get_B_Coeffs());
    }

    /**
     * Tests the correct functionality of setter and getter for the c coefficients set
     */
    @org.junit.Test
    public void testSetGetCCoefficients() {
        SstCoefficients coeffs = new SstCoefficients();

        // initially must return null
        assertEquals(null, coeffs.get_C_Coeffs());

        // must not accept null as argument
        try {
            coeffs.set_C_Coeffs(null);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }

        // when we set something we expect to get the same thing back
        float[] one = new float[]{1.f, 2.f, 3.f, 4.f, 5.f};
        float[] two = new float[]{6.f, 7.f, 8.f, 9.f, 10.f};

        coeffs.set_C_Coeffs(one);
        assertEquals(one, coeffs.get_C_Coeffs());

        coeffs.set_C_Coeffs(two);
        assertEquals(two, coeffs.get_C_Coeffs());
    }

    /**
     * Tests the correct functionality of setter and getter for the d coefficients set
     */
    @org.junit.Test
    public void testSetGetDCoefficients() {
        SstCoefficients coeffs = new SstCoefficients();

        // initially must return null
        assertEquals(null, coeffs.get_D_Coeffs());

        // must not accept null as argument
        try {
            coeffs.set_D_Coeffs(null);
            fail("exception expected");
        } catch (IllegalArgumentException ignored) {
        }

        // when we set something we expect to get the same thing back
        float[] one = new float[]{1.f, 2.f, 3.f, 4.f, 5.f, 6.f, 7.f};
        float[] two = new float[]{8.f, 9.f, 10.f, 11.f, 12.f, 13.f, 14.f};

        coeffs.set_D_Coeffs(one);
        assertEquals(one, coeffs.get_D_Coeffs());

        coeffs.set_D_Coeffs(two);
        assertEquals(two, coeffs.get_D_Coeffs());
    }
}
