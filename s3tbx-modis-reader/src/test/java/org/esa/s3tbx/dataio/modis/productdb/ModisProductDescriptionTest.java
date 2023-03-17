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
package org.esa.s3tbx.dataio.modis.productdb;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModisProductDescriptionTest {

    private ModisProductDescription prod;

    @Before
    public void setUp() {
        prod = new ModisProductDescription();
    }

    @Test
    public void testDefaultConstruction() {
        assertEquals(0, prod.getBandNames().length);
        assertNull(prod.getBandDescription("no_band"));
        assertEquals(false, prod.mustFlipTopDown());
        assertArrayEquals(null, prod.getGeolocationDatasetNames());
        assertEquals(null, prod.getExternalGeolocationPattern());
        assertEquals(false, prod.hasExternalGeolocation());
        assertEquals(0, prod.getTiePointNames().length);
    }

    /**
     * Tests the correct functionality of the band accessor methods
     */
    @Test
    public void testAddGetBand() {
        String expB_1_Name = "band_1";
        String expB_1_spectral = "true";
        String expB_1_ScaleMethod = "scale_method_1";
        String expB_1_Scale = "scale_1";
        String expB_1_Offset = "offset_1";
        String expB_1_Unit = "unit_1";
        String expB_1_BandName = "band_name_1";
        String expB_1_DescName = "desc_name_1";
        String expB_1_SpectralWL = "680.5";
        String expB_1_SpectralBW = "56.7";
        String expB_1_SpectralBI = "4";

        String expB_2_Name = "band_2";
        String expB_2_spectral = "false";
        String expB_2_ScaleMethod = "scale_method_2";
        String expB_2_Scale = "scale_2";
        String expB_2_Offset = "offset_2";
        String expB_2_Unit = "unit_2";
        String expB_2_BandName = "band_name_2";
        String expB_2_DescName = "desc_name_2";

        // add two bands
        prod.addBand(expB_1_Name, expB_1_spectral, expB_1_ScaleMethod, expB_1_Scale,
                      expB_1_Offset, expB_1_Unit, expB_1_BandName, expB_1_DescName,
                      expB_1_SpectralWL, expB_1_SpectralBW, expB_1_SpectralBI);
        prod.addBand(expB_2_Name, expB_2_spectral, expB_2_ScaleMethod, expB_2_Scale,
                      expB_2_Offset, expB_2_Unit, expB_2_BandName, expB_2_DescName);

        // retrieve description object for band 1 - and check
        ModisBandDescription band = prod.getBandDescription(expB_1_Name);
        assertNotNull(band);
        assertEquals(expB_1_Name, band.getName());
        assertEquals(expB_1_spectral, "" + band.isSpectral());
        assertEquals(expB_1_ScaleMethod, band.getScalingMethod());
        assertEquals(expB_1_Scale, band.getScaleAttribName());
        assertEquals(expB_1_Offset, band.getOffsetAttribName());
        assertEquals(expB_1_Unit, band.getUnitAttribName());
        assertEquals(expB_1_BandName, band.getBandAttribName());
        assertEquals(true, band.hasSpectralInfo());
        final ModisSpectralInfo spectralInfo = band.getSpecInfo();
        assertNotNull(spectralInfo);
        assertEquals(expB_1_SpectralWL, "" + spectralInfo.getSpectralWavelength());
        assertEquals(expB_1_SpectralBW, "" + spectralInfo.getSpectralBandwidth());
        assertEquals(expB_1_SpectralBI, "" + spectralInfo.getSpectralBandIndex());

        // retrieve description object for band 2 - and check
        band = prod.getBandDescription(expB_2_Name);
        assertNotNull(band);
        assertEquals(expB_2_Name, band.getName());
        assertEquals(expB_2_spectral, "" + band.isSpectral());
        assertEquals(expB_2_ScaleMethod, band.getScalingMethod());
        assertEquals(expB_2_Scale, band.getScaleAttribName());
        assertEquals(expB_2_Offset, band.getOffsetAttribName());
        assertEquals(expB_2_Unit, band.getUnitAttribName());
        assertEquals(expB_2_BandName, band.getBandAttribName());

        // check that the band names are read out correctly
        String[] bandNames = prod.getBandNames();
        assertNotNull(bandNames);
        assertEquals(2, bandNames.length);
        assertEquals(expB_1_Name, bandNames[0]);
        assertEquals(expB_2_Name, bandNames[1]);

        // check some invalid band names
        assertNull(prod.getBandDescription("no_band"));
        assertNull(prod.getBandDescription("nonsense"));
    }

    /**
     * Tests the correct functionality of the geolocation accessors.
     */
    @Test
    public void testGeolocationAccess() {
        // none shall be set initially
        assertNull(prod.getGeolocationDatasetNames());

        // set a tie point geolocation and check
        String expLat = "latitude";
        String expLon = "longitude";
        String[] geoLoc;

        prod.setGeolocationDatasetNames(expLat, expLon);
        geoLoc = prod.getGeolocationDatasetNames();
        assertNotNull(geoLoc);
        assertEquals(2, geoLoc.length);
        assertEquals(expLat, geoLoc[0]);
        assertEquals(expLon, geoLoc[1]);
    }

    /**
     * Tests the correct functionality of the tie point accessors
     */
    @Test
    public void testTiePointAccess() {
        // initially empty
        assertEquals(0, prod.getTiePointNames().length);

        String[] tp = new String[]{"tie_point_1", "tie_point_2", "tie_point_3"};
        String[] sc = new String[]{"scale_1", "scale_2", "scale_3"};
        String[] of = new String[]{"off_1", "off_2", "off_3"};
        String[] un = new String[]{"unit_1", "unit_2", "unit_3"};

        for (int n = 0; n < 3; n++) {
            prod.addTiePointGrid(new ModisTiePointDescription(tp[n], sc[n], of[n], un[n]));
        }

        String[] tpNames = prod.getTiePointNames();
        assertNotNull(tpNames);
        assertEquals(3, tpNames.length);

        for (int n = 0; n < 3; n++) {
            assertEquals(tp[n], tpNames[n]);
        }

        // loop over descriptions
        ModisTiePointDescription desc;
        for (int n = 0; n < 3; n++) {
            desc = prod.getTiePointDescription(tpNames[n]);
            assertNotNull(desc);

            assertEquals(tp[n], desc.getName());
            assertEquals(sc[n], desc.getScaleAttribName());
            assertEquals(of[n], desc.getOffsetAttribName());
            assertEquals(un[n], desc.getUnitAttribName());
        }
    }

    /**
     * Tests the correct functionality of the flip accessor methods
     */
    @Test
    public void testFlipAccessors() {
        // initially false
        assertEquals(false, prod.mustFlipTopDown());

        // set true and check
        prod.setTopDownFlip(true);
        assertEquals(true, prod.mustFlipTopDown());

        // set false and check again
        prod.setTopDownFlip(false);
        assertEquals(false, prod.mustFlipTopDown());
    }

    /**
     * Checks the correct functionality of the geocoding accessors
     */
    @Test
    public void testGeocodingAccessors() {
        // intially everything is null and false
        String[] latlon;

        latlon = prod.getGeolocationDatasetNames();
        assertNull(latlon);
        assertEquals(false, prod.hasExternalGeolocation());
        assertNull(prod.getExternalGeolocationPattern());

        // set lat/lon tiepoint geocoding
        String expLat = "latitude";
        String expLon = "lomgitude";
        prod.setGeolocationDatasetNames(expLat, expLon);

        assertEquals(false, prod.hasExternalGeolocation());
        latlon = prod.getGeolocationDatasetNames();
        assertNotNull(latlon);
        assertEquals(2, latlon.length);
        assertEquals(expLat, latlon[0]);
        assertEquals(expLon, latlon[1]);

        // set external geocoding
        String expExter = "external";
        prod.setExternalGeolocationPattern(expExter);
        assertEquals(true, prod.hasExternalGeolocation());
        assertEquals(expExter, prod.getExternalGeolocationPattern());

        prod.setExternalGeolocationPattern(null);
        assertEquals(false, prod.hasExternalGeolocation());
        assertEquals(null, prod.getExternalGeolocationPattern());
    }

}
