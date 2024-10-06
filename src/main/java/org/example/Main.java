package org.example;

import java.io.*;
import java.util.ArrayList;

/**
 * La clase Main procesa un archivo CSV de películas, lee los datos de las películas,
 * valida la información, y genera archivos HTML a partir de una plantilla.
 */
public class Main {

    /**
     * Método principal que inicia la ejecución del programa.
     *
     * @param args
     */
    public static void main(String[] args) {
        File archivoPelis = new File("peliculas.csv");
        File salida = new File("salida");

        // Crea el directorio de salida si no existe
        if (!salida.exists()) {
            salida.mkdir();
        }

        ArrayList<Pelicula> peliculas = new ArrayList<>();

        // Lee el archivo CSV de películas
        try (BufferedReader br = new BufferedReader(new FileReader(archivoPelis))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // Valida la cantidad de datos en la línea
                if (datos.length != 5) {
                    System.err.println("Error: Línea con datos inválidos: " + linea);
                    continue;
                }
                String id = datos[0].trim();
                String titulo = datos[1].trim();
                Integer ano;
                try {
                    ano = Integer.parseInt(datos[2].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error: Año inválido para la película: " + titulo);
                    continue;
                }
                String director = datos[3].trim();
                String genero = datos[4].trim();

                // Verifica que todos los datos requeridos estén presentes
                if (!id.isEmpty() && !titulo.isEmpty() && ano != null && !director.isEmpty() && !genero.isEmpty()) {
                    peliculas.add(new Pelicula(id, titulo, ano, director, genero));
                } else {
                    System.err.println("Error: Faltan datos para la película: " + linea);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String resultado = "";

        // Lee la plantilla HTML
        try (BufferedReader br = new BufferedReader(new FileReader("template.html"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                resultado += linea + "\n";
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Genera un archivo HTML para cada película
        for (Pelicula p : peliculas) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("salida/" + p.getTitulo().replace(":", "") + " - " + p.getId() + ".html"))) {
                bw.write(
                        resultado
                                .replace("%%1%%", p.getId())
                                .replace("%%2%%", p.getTitulo())
                                .replace("%%3%%", p.getAno().toString())
                                .replace("%%4%%", p.getDirector())
                                .replace("%%5%%", p.getGenero())
                );

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
