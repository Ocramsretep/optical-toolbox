package eu.esa.opt.dataio.gdal.writer.plugins;

/**
 * @author Jean Coravu
 */
public class SAGADriverProductWriterPlugInTest extends AbstractTestDriverProductWriterPlugIn {

    public SAGADriverProductWriterPlugInTest() {
        super("SAGA", new SAGADriverProductWriterPlugIn());
    }
}
