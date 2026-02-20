package com.semantica.websemantic.controller;


import com.semantica.websemantic.service.SemanticService;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/semantica") // Prefijo base
public class SemanticController {

    private final SemanticService semanticService;

    public SemanticController(SemanticService semanticService) {
        this.semanticService = semanticService;
    }

    // SOLO métodos de lectura/consulta

    @PostMapping("/sparql")
    public ResponseEntity<String> ejecutarSparql(@RequestBody String queryStr) {
        Dataset ds = semanticService.getDataset();
        ds.begin(ReadWrite.READ);
        try (QueryExecution qExec = QueryExecutionFactory.create(queryStr, ds)) {
            // Ejecutamos la consulta SELECT
            ResultSet results = qExec.execSelect();

            // Convertimos el resultado a formato JSON
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(outputStream, results);

            return ResponseEntity.ok(outputStream.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la consulta SPARQL: " + e.getMessage());
        } finally {
            ds.end();
        }
    }

    @GetMapping("/grafo")
    public ResponseEntity<String> verGrafo() {
        // 1. Obtenemos el Dataset (la base de datos) del servicio
        Dataset ds = semanticService.getDataset();

        // 2. Iniciamos una transacción de LECTURA (Obligatorio en Jena)
        ds.begin(ReadWrite.READ);
        try {
            // 3. Obtenemos el modelo por defecto donde guardaste los vehículos
            Model model = ds.getDefaultModel();

            // 4. Preparamos un flujo de salida para escribir el texto
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // 5. Escribimos el grafo en formato TURTLE (el más legible)
            org.apache.jena.riot.RDFDataMgr.write(out, model, org.apache.jena.riot.Lang.TURTLE);

            // 6. Devolvemos el texto generado
            return ResponseEntity.ok(out.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al leer el grafo: " + e.getMessage());
        } finally {
            // 7. Cerramos la transacción siempre
            ds.end();
        }
    }
}