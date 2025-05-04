/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package es.gva.edu.iesjuandegaray.valenbiciapiv2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

/**
 *
 * @author laura
 */
public class DatosJSon {

    private static String API_URL;
    private String datos = ""; //para mostrar en el jTextArea los datos de las estaciones

    private String[] values; //para añadir los datos de las estaciones Valenbici a la BDD
    private int numEst;

    public DatosJSon(int nE) {
        numEst = nE;
        datos = "";
        API_URL = "https://valencia.opendatasoft.com/api/explore/v2.1/catalog/datasets/valenbisi-disponibilitat-valenbisi-dsiponibilidad/records?f=json&location=39.46447,-0.39308&distance=10&limit=" + nE;

        values = new String[numEst];

        for (int i = 0; i < numEst; i++) {
            values[i] = "";
        }
    }

    public void mostrarDatos(int nE) {

        numEst = nE;
        datos = "";
        API_URL = "https://valencia.opendatasoft.com/api/explore/v2.1/catalog/datasets/valenbisi-disponibilitat-valenbisi-dsiponibilidad/records?f=json&location=39.46447,-0.39308&distance=10&limit=" + nE;

        values = new String[numEst];
        for (int i = 0; i < numEst; i++) {
            values[i] = "";
        }
        double lon, lat;

        if (API_URL.isEmpty()) {
            //System.err.println("La URL de la API no está especificada.");
            setDatos(getDatos().concat("La URL de la API no está especificada."));
            return;
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);

                // Intentamos procesar la respuesta como JSON
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    // Añade aquí el Código para recorrer el vector de objetos JSON, con los datos de las estaciones y preparar el vector de
                    // valores (atributo values de esta clase).

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject est = resultsArray.getJSONObject(i);

                        int estacion_id = est.getInt("number");
                        String direccion = est.getString("address");
                        int bicis_disponibles = est.getInt("available");
                        int anclajes_libres = est.getInt("free");
                        boolean estado_operativo = est.getString("open").equalsIgnoreCase("T");

                        JSONObject geolocalizacion = est.getJSONObject("geo_point_2d");
                        lon = geolocalizacion.getDouble("lon");
                        lat = geolocalizacion.getDouble("lat");

                        // Texto que aparecerá en jTextArea1
                        datos += "Id: " + estacion_id + " - Dirección: " + direccion + " - Bicis: " + bicis_disponibles + " - Anclajes disponibles: " + anclajes_libres + " - Operativa: " + estado_operativo + "\n";

                        // Prepara los valores para SQL (como cadena con los paréntesis)
                        values[i] = "(" + estacion_id + ", '" + direccion + "', " + bicis_disponibles + ", "
                                + anclajes_libres + ", " + estado_operativo + ", "
                                + "ST_PointFromText('POINT(" + lon + " " + lat + ")'))";
                    }

                } catch (org.json.JSONException e) {
                    // Si la respuesta no es un array JSON, imprimimos el mensaje de error
                    setDatos(getDatos().concat("Error al procesar los datos JSON: " + e.getMessage()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * @return the datos
     */
    public String getDatos() {
        return datos;
    }

    /**
     * @param datos the datos to set
     */
    public void setDatos(String datos) {
        this.datos = datos;
    }

    /**
     * @return the values
     */
    public String[] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(String[] values) {
        this.values = values;
    }

    /**
     * @return the numEst
     */
    public int getNumEst() {
        return numEst;
    }

    /**
     * @param numEst the numEst to set
     */
    public void setNumEst(int numEst) {
        this.numEst = numEst;
    }
}
