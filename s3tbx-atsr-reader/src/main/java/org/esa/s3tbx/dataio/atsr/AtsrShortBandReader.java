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
package org.esa.s3tbx.dataio.atsr;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.ProductData;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

class AtsrShortBandReader extends AtsrBandReader {

    static final int _pixelSize = 2;

    /**
     * Creates the object with given band name, file offset conversion multiplier and file stream.
     */
    AtsrShortBandReader(String bandName, int offset, float mult, ImageInputStream stream) {
        super(bandName, offset, mult, stream);
    }

    @Override
    void readBandData(int sourceOffsetX, int sourceOffsetY, int sourceWidth, int sourceHeight, int sourceStepX,
                      int sourceStepY, int destOffsetX, int destOffsetY, int destWidth, int destHeight,
                      ProductData destBuffer, ProgressMonitor pm) throws IOException {
        setStreamPos(sourceOffsetX, sourceOffsetY, _pixelSize);

        float[] targetData = (float[]) destBuffer.getElems();
        short[] line = new short[sourceWidth];
        int targetIdx = 0;

        pm.beginTask("Reading band '" + getBandName() + "'...", sourceHeight);
        // loop over lines
        try {
            for (int y = 0; y < sourceHeight; y += sourceStepY) {
                if (pm.isCanceled()) {
                    break;
                }
                _stream.readFully(line, 0, line.length);

                // convert line - and eventually subsample
                for (int x = 0; x < sourceWidth; x += sourceStepX) {
                    targetData[targetIdx] = Math.abs(line[x]) * _multiplier;
                    ++targetIdx;
                }
                updateStreamPos(AtsrConstants.ATSR_SCENE_RASTER_WIDTH - sourceWidth, _pixelSize);

                pm.worked(1);
            }
        } finally {
            pm.done();
        }

    }
}
