package eu.esa.opt.dataio.landsat.geotiff;

import eu.esa.opt.dataio.landsat.geotiff.c1.CollectionOLILandsatQA;
import org.esa.snap.core.datamodel.Mask;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.assertEquals;

/**
 * Created by obarrile on 09/01/2019.
 */
public class CollectionOLILandsatQATest {
    @Test
    public void testCreateMasks() {
        CollectionOLILandsatQA landsatQA = new CollectionOLILandsatQA();
        java.util.List<Mask> masks = landsatQA.createMasks(new Dimension(1, 1));
        assertEquals(masks.size(),19);
    }

}
