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

package org.esa.s3tbx.dataio.landsat;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * The interface <code>LandsatTMBand</code> is used as a template for an implementation for a Landsat TM Band data object
 *
 * @author Christian Berwanger (ai0263@umwelt-campus.de)
 */
public interface LandsatTMBand {

    /**
     * @return the source (File or Zipentry)
     */
    public Object getInputSource();

    /**
     * @return the name of the LANDSAT TM band
     */
    public String getBandName();

    /**
     * @return short description of the physical function of the band
     */
    public String getBandDescription();

    /**
     * @return the index number of the band in the LANDSAT TM product
     */
    public int getIndex();

    /**
     * @return the resolution of the band in meter
     */
    public int getResolution();

    /**
     * @return the bandwidth of the band
     */
    public float getBandwidth();

    /**
     * @return the wavelength captured in the band
     */
    public float getWavelength();

    /**
     * @return the real maximal radiance value of the band
     */
    public double getMaxRadiance();

    /**
     * @return the real minimal radiance value of the band
     */
    public double getMinRadiance();

    /**
     * @return the nominal max radiance value of the band
     */
    public double getFormerNomMaxRadiance();

    /**
     * @return the nominal min radiance value of the band
     */
    public double getFormerNomMinRadiance();

    /**
     * @return the gain value of the band
     */
    public double getGain();

    /**
     * @return the bias value of the band
     */
    public double getBias();

    /**
     * @return the solar flux of the band (TM Solar Exoatmosheric spectral irradiances)
     */
    public float getSolarFlux();

    /**
     * @return creates inputstream of the image
     * @throws IOException
     */
    public ImageInputStream createStream() throws IOException;


    /**
     * @return the old nominal gain value
     */
    public double getFormerNominalGain();

    /**
     * @return the new nominal gain value
     */
    public double getNewerNomMaxRadiance();

    /**
     * @return the new nominal minimum radiance value
     */
    public double getNewerNomMinRadiance();

    /**
     * @return new nominal gain value
     */
    public double getNewerNomGain();

    /**
     * @return <code>true</code> if the band is a thermal band if not the function returns <code>false</code>
     */
    boolean isThermal();
}
