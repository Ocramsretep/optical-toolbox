package org.esa.s3tbx.insitu.server.mermaid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.esa.s3tbx.insitu.server.InsituObservation;
import org.esa.s3tbx.insitu.server.InsituResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;


/**
 * @author Marco Peters
 */
public class JsonDeserializationTest {

    private Gson gson;
    private static Locale defaultLocale;

    @BeforeClass
    public static void setUpClass() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    @AfterClass
    public static void tearDown() {
        Locale.setDefault(defaultLocale);
    }

    @Before
    public void setUp() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new UtcDateTypeAdapter());
        gson = gsonBuilder.create();
    }

    @Test
    public void testParseCampaignsResponse() {
        final InputStream jsonStream = JsonDeserializationTest.class.getResourceAsStream("CampaignsResponse.json");
        final MermaidResponse response = gson.fromJson(new InputStreamReader(jsonStream), MermaidResponse.class);
        assertNotNull(response);

        assertEquals(InsituResponse.STATUS_CODE.OK, response.getStatus());
        assertNull(response.getFailureReasons());
        final List<Campaign> campaigns = response.getDatasets();
        assertNotNull(campaigns);
        assertEquals(2, campaigns.size());
        final Campaign aeronetDescr = campaigns.get(0);
        assertEquals("AERONET", aeronetDescr.getName());
        assertEquals("Giuseppe Zibordi", aeronetDescr.getPi());
        assertNull(aeronetDescr.getWebsite());
        assertEquals("giuseppe.zibordi@jrc.it", aeronetDescr.getContact());
        assertTrue(aeronetDescr.getPolicy().startsWith("The AAOT AERONET-OC insitu dataset"));
        assertTrue(aeronetDescr.getDescription().startsWith("The network AERONET - Ocean Color"));
        final Campaign boussoleDescr = campaigns.get(1);
        assertEquals("BOUSSOLE", boussoleDescr.getName());
        assertEquals("David Antoine", boussoleDescr.getPi());
        assertEquals("http://www.obs-vlfr.fr/Boussole/", boussoleDescr.getWebsite());
        assertEquals("antoine@obs-vlfr.fr", boussoleDescr.getContact());
        assertTrue(boussoleDescr.getPolicy().startsWith("The BOUSSOLE insitu dataset"));
        assertTrue(boussoleDescr.getDescription().startsWith("The BOUSSOLE project provides"));

    }

    @Test
    public void testParseParametersResponse() {
        final InputStream jsonStream = JsonDeserializationTest.class.getResourceAsStream("ParametersResponse.json");
        final MermaidResponse response = gson.fromJson(new InputStreamReader(jsonStream), MermaidResponse.class);
        assertNotNull(response);

        assertEquals(InsituResponse.STATUS_CODE.OK, response.getStatus());
        assertNull(response.getFailureReasons());
        final List<MermaidParameter> parameters = response.getParameters();
        assertNotNull(parameters);
        assertEquals(23, parameters.size());
        final MermaidParameter parameter = parameters.get(4);
        assertEquals("es_510", parameter.getName());
        assertEquals("radiance", parameter.getType());
        assertEquals("mW.m-2.nm-1", parameter.getUnit());
        assertEquals("Sea-level solar illumination at 510nm", parameter.getDescription());
    }

    @Test
    public void testParseObservationsResponse() throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        final InputStream jsonStream = JsonDeserializationTest.class.getResourceAsStream("ObservationsResponse.json");
        final MermaidResponse response = gson.fromJson(new InputStreamReader(jsonStream), MermaidResponse.class);
        assertNotNull(response);

        assertEquals(InsituResponse.STATUS_CODE.OK, response.getStatus());
        assertNull(response.getFailureReasons());
        assertEquals(484, response.getObservationCount());
        final List<Campaign> campaignList = response.getDatasets();
        assertNotNull(campaignList);
        assertEquals(2, campaignList.size());

        final Campaign aeronetCampaign = campaignList.get(0);
        assertEquals("AERONET", aeronetCampaign.getName());
        final List<MermaidObservation> aeronetObservations = aeronetCampaign.getObservations();
        assertEquals(200, aeronetObservations.size());
        final InsituObservation aeronetObs = aeronetObservations.get(12);
        assertEquals("es_412", aeronetObs.getParam());
        assertEquals(45.314, aeronetObs.getLon(), 1.0e-6);
        assertEquals(12.508, aeronetObs.getLat(), 1.0e-6);
        assertEquals(1043.825711, aeronetObs.getValue(), 1.0e-6);

        assertEquals(dateFormat.parse("22-APR-2002 12:21:56"), aeronetObs.getDate());

        final Campaign boussoleCampaign = campaignList.get(1);
        assertEquals("BOUSSOLE", boussoleCampaign.getName());
        final List<MermaidObservation> boussoleObservations = boussoleCampaign.getObservations();
        assertEquals(284, boussoleObservations.size());
        final InsituObservation boussoleObs = boussoleObservations.get(0);
        assertEquals("es_412", boussoleObs.getParam());
        assertEquals(43.367, boussoleObs.getLon(), 1.0e-6);
        assertEquals(7.9, boussoleObs.getLat(), 1.0e-6);
        assertEquals(748.971558, boussoleObs.getValue(), 1.0e-6);
        assertEquals(dateFormat.parse("23-SEP-2003 10:15:35"), boussoleObs.getDate());
    }

    @Test
    public void testParseFailureResponse() throws Exception {
        final InputStream jsonStream = JsonDeserializationTest.class.getResourceAsStream("FailureResponse.json");
        final MermaidResponse response = gson.fromJson(new InputStreamReader(jsonStream), MermaidResponse.class);
        assertNotNull(response);

        assertEquals(InsituResponse.STATUS_CODE.NOK, response.getStatus());
        assertNotNull(response.getFailureReasons());
        assertEquals("Non-existent vars in URL for: lon_min & stop_date", response.getFailureReasons().get(0));
    }
}