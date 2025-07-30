package com.example.libreria;

import com.example.libreria.model.DatosLibro;
import com.example.libreria.model.RespuestaGutendex;
import com.example.libreria.principal.Principal;
import com.example.libreria.service.ConsumoAPI;
import com.example.libreria.service.ConvierteDatos;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibreriaApplication implements CommandLineRunner {
    private final Principal principal;

    public LibreriaApplication(Principal principal) {
        this.principal = principal;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibreriaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        principal.muestraElMenu();
    }
}
