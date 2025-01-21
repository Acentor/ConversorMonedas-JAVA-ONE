package com.ejemplo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.lang.reflect.Type;

public class ConversorMonedas {
    private static final String CLAVE_API = "18e2a922744807f1a3638bc2";
    private static final String URL_BASE = "https://v6.exchangerate-api.com/v6/";
    private static final HttpClient clienteHttp = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static Map<String, Double> tasasConversion = new TreeMap<>();

    public static void main(String[] args) {
        Scanner escaner = new Scanner(System.in);
        
        while (true) {
            try {
                System.out.println("\n=== Conversor de Monedas ===");
                System.out.println("1. Realizar una conversión");
                System.out.println("2. Ver tasas disponibles");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");
                int opcion = escaner.nextInt();
                escaner.nextLine(); 
                switch (opcion) {
                    case 1:
                        realizarConversion(escaner);
                        break;
                    case 2:
                        mostrarTasasDisponibles();
                        break;
                    case 3:
                        System.out.println("¡Gracias por usar el conversor!");
                        return;
                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");}} catch (Exception error) {
                System.out.println("Error: " + error.getMessage());
                escaner.nextLine();}}}

    private static void realizarConversion(Scanner escaner) throws Exception {
        System.out.print("Ingrese la moneda base (ej. USD): ");
        String monedaBase = escaner.nextLine().toUpperCase();
        actualizarTasasCambio(monedaBase);
        System.out.print("Ingrese la moneda objetivo (ej. EUR): ");
        String monedaObjetivo = escaner.nextLine().toUpperCase();
        
        if (!tasasConversion.containsKey(monedaObjetivo)) {
            System.out.println("Moneda objetivo no disponible.");
            return;}
        System.out.print("Ingrese la cantidad a convertir: ");
        double cantidad = escaner.nextDouble();
        double tasa = tasasConversion.get(monedaObjetivo);
        double resultado = cantidad * tasa;
        System.out.printf("%.2f %s = %.2f %s%n", 
            cantidad, monedaBase, resultado, monedaObjetivo);}

    private static void mostrarTasasDisponibles() {
        if (tasasConversion.isEmpty()) {
            try {actualizarTasasCambio("USD");
            } catch (Exception error) {
                System.out.println("Error al obtener las tasas de cambio: " + error.getMessage());
                return;}}
        
        System.out.println("\nMonedas disponibles:");
        for (String moneda : tasasConversion.keySet()) {
            System.out.println("- " + moneda);}}

    private static void actualizarTasasCambio(String monedaBase) throws Exception {
        String url = URL_BASE + CLAVE_API + "/latest/" + monedaBase;
        HttpRequest peticion = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();
        HttpResponse<String> respuesta = clienteHttp.send(peticion, 
            HttpResponse.BodyHandlers.ofString());
            
        if (respuesta.statusCode() != 200) {
            throw new Exception("Error al obtener las tasas de cambio");}
        
        JsonObject jsonRespuesta = JsonParser.parseString(respuesta.body())
            .getAsJsonObject();
            
        if (!jsonRespuesta.get("result").getAsString().equals("success")) {
            throw new Exception("Error en la respuesta de la API");}
        
        JsonObject tasas = jsonRespuesta.getAsJsonObject("conversion_rates");
        Type tipoMap = new TypeToken<Map<String, Double>>(){}.getType();
        tasasConversion = gson.fromJson(tasas, tipoMap);}}