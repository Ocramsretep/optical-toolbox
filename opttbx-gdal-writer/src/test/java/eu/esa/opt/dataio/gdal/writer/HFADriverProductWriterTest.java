package eu.esa.opt.dataio.gdal.writer;

import eu.esa.opt.dataio.gdal.reader.plugins.HFADriverProductReaderPlugIn;
import eu.esa.opt.dataio.gdal.writer.plugins.HFADriverProductWriterPlugIn;

/**
 * @author Jean Coravu
 */
public class HFADriverProductWriterTest extends AbstractTestDriverProductWriter {

    public HFADriverProductWriterTest() {
        super("HFA", ".img", "Byte Int16 UInt16 Int32 UInt32 Float32 Float64 CFloat32 CFloat64", new HFADriverProductReaderPlugIn(), new HFADriverProductWriterPlugIn());
    }
}
