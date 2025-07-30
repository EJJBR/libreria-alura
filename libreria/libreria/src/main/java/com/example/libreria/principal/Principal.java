package com.example.libreria.principal;

import com.example.libreria.model.Autor;
import com.example.libreria.model.DatosLibro;
import com.example.libreria.model.Libro;
import com.example.libreria.model.RespuestaGutendex;
import com.example.libreria.repository.AutorRepository;
import com.example.libreria.repository.LibroRepository;
import com.example.libreria.service.ConsumoAPI;
import com.example.libreria.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;
@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books?search=";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;

    public void muestraElMenu() {
        String opcion = "";
        while (!opcion.equals("0")) {
            var menu = """
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- Listar autores registrados
                    4- Listar vivos en un determinado año
                    5- Listar libros por idioma
                    
                    0- Salir
                    Elija una opcion:
                    """;
            System.out.println(menu);
            opcion = teclado.nextLine();
            switch (opcion) {
                case "1":
                    buscarLibroWeb();
                    break;
                case "2":
                    listarLibrosRegistrados();
                    break;
                case "3":
                    listarAutoresRegistrados();
                    break;
                case "4":
                    listarAutpresVivosEnUnDeterminadoAño();
                    break;
                case "5":
                    listarLibrosPorIdioma();
                    break;
                case "0":
                    System.out.println("Finalizando programa....");
                    break;
                default:
                    System.out.println("Seleccione alguna de las opciones:");
            }
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Seleccione el idioma:");
        System.out.println("1 - Inglés (en)");
        System.out.println("2 - Español (es)");
        System.out.println("3 - Francés (fr)");
        System.out.println("4 - Portugués (pt)");
        System.out.println("5 - Finlandés (fi)");
        String opcion = teclado.nextLine();

        String idioma = switch (opcion) {
            case "1" -> "en";
            case "2" -> "es";
            case "3" -> "fr";
            case "4" -> "pt";
            case "5" -> "fi";
            default -> "";
        };

        if (idioma.isEmpty()) {
            System.out.println("Opción no válida");
            return;
        }

        var libros = libroRepository.findAll();

        var librosFiltrados = libros.stream()
                .filter(l -> l.getIdiomas().contains(idioma))
                .toList();

        if (librosFiltrados.isEmpty()) {
            System.out.println("No hay libros registrados en el idioma: " + idioma);
        } else {
            System.out.println("Libros en idioma " + idioma + ":");
            for (Libro libro : librosFiltrados) {
                System.out.println("----- LIBRO -----");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autores:");
                libro.getAutores().forEach(a -> System.out.println("  - " + a.getNombre()));
                System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
                System.out.println("-----------------");
            }
        }
    }


    private void listarAutpresVivosEnUnDeterminadoAño() {
        System.out.println("Ingrese el año a consultar: ");
        int year = Integer.parseInt(teclado.nextLine());

        var autores = autorRepository.findAll();

        var autoresVivos = autores.stream()
                .filter(a -> a.getBirthYear() != null && a.getBirthYear() <= year)
                .filter(a -> a.getDeathYear() == null || a.getDeathYear() > year)
                .toList();

        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + year);
        } else {
            System.out.println("Autores vivos en el año " + year + ":");
            for (Autor autor : autoresVivos) {
                System.out.println("Autor: " + autor.getNombre() +
                        " (" + autor.getBirthYear() +
                        " - " + (autor.getDeathYear() != null ? autor.getDeathYear() : "actualmente vivo") + ")");
                System.out.println("Libros publicados:");
                for (Libro libro : autor.getLibros()) {
                    System.out.println("   * " + libro.getTitulo());
                }
                System.out.println("-----------------------------");
            }
        }
    }


    private void listarAutoresRegistrados() {
        System.out.println("Mostrando autores registrados en la BD...");

        var autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
            return;
        }

        for (Autor autor : autores) {
            System.out.println("----- AUTOR -----");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Año de nacimiento: " +
                    (autor.getBirthYear() != null ? autor.getBirthYear() : "Desconocido"));
            System.out.println("Año de fallecimiento: " +
                    (autor.getDeathYear() != null ? autor.getDeathYear() : "Desconocido"));

            if (autor.getLibros() != null && !autor.getLibros().isEmpty()) {
                System.out.println("Libros publicados:");
                for (Libro libro : autor.getLibros()) {
                    System.out.println("  - " + libro.getTitulo());
                }
            } else {
                System.out.println("Libros publicados: (ninguno)");
            }

            System.out.println("-----------------");
        }
    }


    private void listarLibrosRegistrados() {
        System.out.println("Mostrando libros de la BD...");

        var libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        for (var libro : libros) {
            // Obtenemos los autores como una cadena
            String autores = libro.getAutores().isEmpty()
                    ? "Desconocido"
                    : libro.getAutores().stream()
                    .map(a -> a.getNombre())
                    .reduce((a1, a2) -> a1 + ", " + a2)
                    .orElse("Desconocido");

            String idiomas = libro.getIdiomas().isEmpty()
                    ? "-"
                    : String.join(", ", libro.getIdiomas());

            System.out.println("----- LIBRO -----");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor(es): " + autores);
            System.out.println("Idioma(s): " + idiomas);
            System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
            System.out.println("-----------------");
            System.out.println();
        }
    }

    private void buscarLibroWeb() {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        var tituloLibro = teclado.nextLine();

        var json = consumoAPI.obtenerDatos(URL_BASE + tituloLibro.replace(" ", "+"));
        RespuestaGutendex respuesta = conversor.obtenerDatos(json, RespuestaGutendex.class);

        if (respuesta.resultados() != null && !respuesta.resultados().isEmpty()) {
            for (DatosLibro libro : respuesta.resultados()) {
                String autorNombre = libro.autores().isEmpty() ? "Desconocido" : libro.autores().get(0).nombre();
                String idioma = libro.idiomas().isEmpty() ? "-" : libro.idiomas().get(0);

                System.out.println("----- LIBRO -----");
                System.out.println("Título: " + libro.titulo());
                System.out.println("Autor: " + autorNombre);
                System.out.println("Idioma: " + idioma);
                System.out.println("Número de descargas: " + libro.numeroDeDescargas());
                System.out.println("-----------------");
                System.out.println();

                // Guardar en BD
                Libro libroEntidad = new Libro(libro.titulo(), libro.numeroDeDescargas());

                // Añadir idiomas
                libroEntidad.getIdiomas().addAll(libro.idiomas());

                // Crear autor (por ahora solo el primero)
                if (!libro.autores().isEmpty()) {
                    var autorDto = libro.autores().get(0);
                    Autor autorEntidad = new Autor(
                            autorDto.nombre(),
                            autorDto.birthYear(),
                            autorDto.deathYear()
                    );
                    libroEntidad.getAutores().add(autorEntidad);
                }

                libroRepository.save(libroEntidad);
            }
        } else {
            System.out.println("Título no encontrado.");
        }
    }
}
