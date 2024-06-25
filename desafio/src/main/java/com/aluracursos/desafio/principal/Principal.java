package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibros;
import com.aluracursos.desafio.service.ConsumoApi;
import com.aluracursos.desafio.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    public void muestraElMenu(){
        var json = consumoApi.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);
        System.out.println(datos);


        //top 10 libros descargados
        System.out.println("top 10 libros descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        //busqueda de libros por nombres
        System.out.println("Ingrese el nombre del libro que desea buscar");
        var titutloLibro = teclado.nextLine();
        json = consumoApi.obtenerDatos(URL_BASE+"?search= " + titutloLibro.replace(" ","+"));
        var datosBusqueda  = convierteDatos.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(titutloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            System.out.println("Libro encontrado");
            System.out.println(libroBuscado.get());
        }else {
            System.out.println("libro no encontrado");
        }

        //estadisticas

        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas() >0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad maximas de descargas: "+ est.getMax());
        System.out.println("cantidad minima de descargas: "+ est.getMin());
        System.out.println("cantidad de registros evaluados para alculaar la estadicsticas : "+ est.getCount());
    }
}
