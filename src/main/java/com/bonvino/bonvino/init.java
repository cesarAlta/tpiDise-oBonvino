package com.bonvino.bonvino;

import com.bonvino.bonvino.Models.*;
import com.bonvino.bonvino.Repositories.IBodegaRepository;
import com.bonvino.bonvino.Repositories.IEnofiloRepository;
import com.bonvino.bonvino.Repositories.IMaridajeRepository;
import com.bonvino.bonvino.Repositories.IVinoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Transactional
public class init implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    IVinoRepository iVinoRepository;
    @Autowired
    IEnofiloRepository iEnofiloRepository;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<Bodega> bodegas = List.of(Bodega.builder()
                .coordenadasUbicacion(new String[]{"-31.435019", "-64.183492"})
                .descripcion("Descripcion - Bodega Luigi Bosca")
                .historia("Historia -  Bodega Luigi Bosca")
                .nombre("Bodega Luigi Bosca")
                .periodoActializacion(2)
                .ultimaActualizacion(LocalDate.now().minusMonths(3))
                .build(), Bodega.builder()
                .coordenadasUbicacion(new String[]{"-33.047238", "-71.612686"})
                .descripcion("Descripcion - Viña Montes")
                .historia("Historia - Viña Montes")
                .nombre("Viña Montes")
                .periodoActializacion(1)
                .ultimaActualizacion(LocalDate.now().minusMonths(1))
                .build(), Bodega.builder()
                .coordenadasUbicacion(new String[]{"42.340346", "-3.698567"})
                .descripcion("Descripcion - Bodega Marques de Riscal")
                .historia("Historia - Bodega Marques de Riscal")
                .nombre("Bodega Marques de Riscal")
                .periodoActializacion(3)
                .ultimaActualizacion(LocalDate.now().minusMonths(4))
                .build(), Bodega.builder()
                .coordenadasUbicacion(new String[]{"49.425409", "8.087328"})
                .descripcion("Descripcion - Weingut Dr. Loosen")
                .historia("Historia - Weingut Dr. Loosen")
                .nombre("Weingut Dr. Loosen")
                .periodoActializacion(3)
                .ultimaActualizacion(LocalDate.now().minusMonths(4))
                .build());

        List<Vino> vinos = Arrays.asList(
                new Vino(2017, "Malbec Reserva", BigDecimal.valueOf(5000),
                        bodegas.get(0),
                        List.of(
                                new Maridaje("Queso Brie", "El queso brie suave y cremoso combina bien con vinos blancos como Chardonnay."),
                                new Maridaje("Salmón Ahumado", "El salmón ahumado se marida perfectamente con un vino blanco seco como Sauvignon Blanc."),
                                new Maridaje("Churrasco", "El churrasco se acompaña mejor con un vino tinto robusto como Malbec.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Malbec Reserva malbec", 80D, new TipoUva("Malbec", "Malbec")),
                                new Varietal("Descripcion Varietal - Malbec Reserva cabernet", 20D, new TipoUva("Cabernet", "Cabernet"))
                        )),
                new Vino(2018, "Cabernet Sauvignon", BigDecimal.valueOf(4500),
                        bodegas.get(0),

                        List.of(
                                new Maridaje("Queso Azul", "El queso azul se combina bien con vinos tintos intensos como el Cabernet Sauvignon."),
                                new Maridaje("Filete Mignon", "El filete mignon se marida perfectamente con un vino tinto robusto como el Cabernet Sauvignon."),
                                new Maridaje("Chocolate Oscuro", "El chocolate oscuro complementa un vino tinto intenso como el Cabernet Sauvignon.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Cabernet Sauvignon", 90D, new TipoUva("Cabernet Sauvignon", "Cabernet Sauvignon")),
                                new Varietal("Descripcion Varietal - Merlot", 10D, new TipoUva("Merlot", "Merlot"))
                        )),
                new Vino(2016, "Pinot Noir", BigDecimal.valueOf(6000),
                        bodegas.get(0),

                        List.of(
                                new Maridaje("Pato Asado", "El pato asado se marida perfectamente con un vino tinto ligero como el Pinot Noir."),
                                new Maridaje("Queso Gruyere", "El queso Gruyere combina bien con vinos tintos suaves como el Pinot Noir."),
                                new Maridaje("Setas a la Parrilla", "Las setas a la parrilla se acompañan bien con un vino tinto suave como el Pinot Noir.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Pinot Noir", 100D, new TipoUva("Pinot Noir", "Pinot Noir"))
                        )),
                new Vino(2019, "Chardonnay", BigDecimal.valueOf(3500),
                        bodegas.get(0),

                        List.of(
                                new Maridaje("Camarones", "Los camarones se combinan perfectamente con un vino blanco fresco como el Chardonnay."),
                                new Maridaje("Pollo a la Parrilla", "El pollo a la parrilla se marida bien con un vino blanco robusto como el Chardonnay."),
                                new Maridaje("Pasta Alfredo", "La pasta Alfredo se complementa con un vino blanco cremoso como el Chardonnay.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Chardonnay", 100D, new TipoUva("Chardonnay", "Chardonnay"))
                        )),
                new Vino(2020, "Sauvignon Blanc", BigDecimal.valueOf(3200),
                        bodegas.get(1),

                        List.of(
                                new Maridaje("Ostras", "Las ostras frescas se combinan perfectamente con un vino blanco seco como el Sauvignon Blanc."),
                                new Maridaje("Ensalada César", "La ensalada César se marida bien con un vino blanco ligero como el Sauvignon Blanc."),
                                new Maridaje("Pescado Blanco", "El pescado blanco se acompaña mejor con un vino blanco fresco como el Sauvignon Blanc.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Sauvignon Blanc", 100D, new TipoUva("Sauvignon Blanc", "Sauvignon Blanc"))
                        )),
                new Vino(2015, "Syrah", BigDecimal.valueOf(5500),
                        bodegas.get(1),

                        List.of(
                                new Maridaje("Carne de Venado", "La carne de venado se combina bien con un vino tinto robusto como el Syrah."),
                                new Maridaje("Costillas de Cerdo", "Las costillas de cerdo se maridan bien con un vino tinto intenso como el Syrah."),
                                new Maridaje("Queso Cheddar", "El queso Cheddar se acompaña bien con un vino tinto fuerte como el Syrah.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Syrah", 95D, new TipoUva("Syrah", "Syrah")),
                                new Varietal("Descripcion Varietal - Viognier", 5D, new TipoUva("Viognier", "Viognier"))
                        )),
                new Vino(2018, "Merlot", BigDecimal.valueOf(4000),
                        bodegas.get(1),

                        List.of(
                                new Maridaje("Pasta Boloñesa", "La pasta boloñesa se marida bien con un vino tinto suave como el Merlot."),
                                new Maridaje("Queso Gouda", "El queso Gouda se combina bien con vinos tintos suaves como el Merlot."),
                                new Maridaje("Pollo Asado", "El pollo asado se acompaña bien con un vino tinto ligero como el Merlot.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Merlot", 100D, new TipoUva("Merlot", "Merlot"))
                        )),
                new Vino(2017, "Tempranillo", BigDecimal.valueOf(4700),
                        bodegas.get(2),
                        List.of(
                                new Maridaje("Paella", "La paella se marida bien con un vino tinto como el Tempranillo."),
                                new Maridaje("Queso Manchego", "El queso manchego se combina bien con vinos tintos como el Tempranillo."),
                                new Maridaje("Jamón Ibérico", "El jamón ibérico se acompaña perfectamente con un vino tinto robusto como el Tempranillo.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Tempranillo", 90D, new TipoUva("Tempranillo", "Tempranillo")),
                                new Varietal("Descripcion Varietal - Graciano", 10D, new TipoUva("Graciano", "Graciano"))
                        )),
                new Vino(2021, "Riesling", BigDecimal.valueOf(3800),
                        bodegas.get(3),
                        List.of(
                                new Maridaje("Mariscos", "Los mariscos se combinan perfectamente con un vino blanco fresco como el Riesling."),
                                new Maridaje("Ensalada de Frutas", "La ensalada de frutas se marida bien con un vino blanco dulce como el Riesling."),
                                new Maridaje("Sushi", "El sushi se acompaña mejor con un vino blanco ligero como el Riesling.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Riesling", 100D, new TipoUva("Riesling", "Riesling"))
                        )),
                new Vino(2019, "Zinfandel", BigDecimal.valueOf(5300),
                        bodegas.get(3),

                        List.of(
                                new Maridaje("Costillas BBQ", "Las costillas BBQ se combinan bien con un vino tinto robusto como el Zinfandel."),
                                new Maridaje("Pizza", "La pizza se marida bien con un vino tinto afrutado como el Zinfandel."),
                                new Maridaje("Hamburguesas", "Las hamburguesas se acompañan mejor con un vino tinto fuerte como el Zinfandel.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Zinfandel", 95D, new TipoUva("Zinfandel", "Zinfandel")),
                                new Varietal("Descripcion Varietal - Petite Sirah", 5D, new TipoUva("Petite Sirah", "Petite Sirah"))
                        )),
                new Vino(2022, "Rosé", BigDecimal.valueOf(3000),
                        bodegas.get(3),

                        List.of(
                                new Maridaje("Ensalada Caprese", "La ensalada Caprese se marida bien con un vino rosado fresco como el Rosé."),
                                new Maridaje("Tarta de Frutas", "La tarta de frutas se combina bien con un vino rosado dulce como el Rosé."),
                                new Maridaje("Pollo al Limón", "El pollo al limón se acompaña mejor con un vino rosado ligero como el Rosé.")
                        ),
                        List.of(
                                new Varietal("Descripcion Varietal - Grenache Rosé", 70D, new TipoUva("Grenache", "Grenache")),
                                new Varietal("Descripcion Varietal - Syrah Rosé", 30D, new TipoUva("Syrah", "Syrah"))
                        ))
        );

        vinos.forEach(v -> {
            v.setFechaActualizacion(LocalDateTime.now().minusMonths(1));
            v.setImagenEtiqueta("/img/EtiquetaBasica.png");
            v.setNotaDeCataBodega("Nota de cata para " + v.getNombre());
        });

        iVinoRepository.saveAll(vinos);

        if (iEnofiloRepository.findAll().isEmpty()) {
            Enofilo e1 = Enofilo.builder()
                    .nombre("Gabriel")
                    .apellido("Batistuta")
                    .usuario(new Usuario("1234", "gabrielB", true))
                    .siguiendoList(
                            List.of(
                                    new Siguiendo(LocalDateTime.now().minusMonths(3), bodegas.get(3)),
                                    new Siguiendo(LocalDateTime.now().minusMonths(3), bodegas.get(2)),
                                    new Siguiendo(LocalDateTime.now().minusMonths(3), bodegas.get(1)))
                    )
                    .build();
            Enofilo e2 = Enofilo.builder()
                    .nombre("Angel")
                    .apellido("Labruna")
                    .usuario(new Usuario("1234", "AngelL", false))
                    .siguiendoList(
                            List.of(new Siguiendo(LocalDateTime.now().minusMonths(9), bodegas.get(1)),
                                    new Siguiendo(LocalDateTime.now().minusMonths(3), bodegas.get(2)))
                    )
                    .build();
            Enofilo e3 = Enofilo.builder()
                    .nombre("Emanuel")
                    .apellido("Ginobili")
                    .usuario(new Usuario("1234", "emaGino", true))
                    .siguiendoList(
                            List.of(new Siguiendo(LocalDateTime.now().minusMonths(8), bodegas.get(1)))).build();
            Enofilo e4 = Enofilo.builder()
                    .nombre("Facundo")
                    .apellido("Campazzo")
                    .usuario(new Usuario("1234", "FacuCamp", true))
                    .siguiendoList(
                            List.of(new Siguiendo(LocalDateTime.now().minusMonths(7), bodegas.get(1)))
                    )
                    .build();
            Enofilo e5 = Enofilo.builder()
                    .nombre("Lionel")
                    .apellido("Messi")
                    .usuario(new Usuario("1234", "LioMessi", true))
                    .siguiendoList(
                            List.of(new Siguiendo(LocalDateTime.now().minusMonths(7), bodegas.get(1)))
                    )
                    .build();

            iEnofiloRepository.saveAll(List.of(e1,e2,e3,e4,e5));
        }



    }

    private LocalDateTime fechaRandom(Random rand) {
        LocalDateTime now = LocalDateTime.now();
        return now.minusMonths(rand.nextInt(3) + 1);
    }

}
