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

import org.esa.snap.core.util.Guardian;

import java.util.Vector;

/**
 * This class serves to hold a commplete set of sst retrieval coefficients.
 */
public final class SstCoefficientSet {

    private String _description;
    private final Vector _coeffs;

    /**
     * Creates the object with default values
     */
    public SstCoefficientSet() {
        _coeffs = new Vector();
        _description = "";
    }

    /**
     * Sets the description for this coefficient set
     */
    public final void setDescription(String desc) {
        Guardian.assertNotNull("description", desc);
        _description = desc;
    }

    /**
     * Retrieves the description of this coefficient set
     */
    public final String getDescription() {
        return _description;
    }

    /**
     * Adds coefficients to this set
     */
    public final void addCoefficients(SstCoefficients coeffs) {
        Guardian.assertNotNull("range", coeffs);
        _coeffs.add(coeffs);
    }

    /**
     * Retrieves the number of coefficients in this set
     */
    public final int getNumCoefficients() {
        return _coeffs.size();
    }

    /**
     * Retrieves the coefficients at the index passed in
     */
    public final SstCoefficients getCoefficientsAt(int index) {
        return (SstCoefficients) _coeffs.elementAt(index);
    }

}
