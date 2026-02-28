package com.semantica.websemantic.controller;


import com.semantica.websemantic.repository.SemanticRepository;
import com.semantica.websemantic.service.RdfDocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/semantica")
public class SemanticController {

    private final SemanticRepository semanticRepository;
    private final RdfDocumentService rdfDocumentService; // NUEVA INYECCIÓN
    public SemanticController(SemanticRepository semanticRepository, RdfDocumentService rdfDocumentService) {
        this.semanticRepository = semanticRepository;
        this.rdfDocumentService = rdfDocumentService;
    }

    @PostMapping("/sparql")
    public ResponseEntity<String> ejecutarSparql(@RequestBody String queryStr) {
        try {
            // El controlador ahora está limpio y solo delega la tarea al Repositorio
            String jsonResult = semanticRepository.ejecutarConsultaSparql(queryStr);
            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // =================================================================
    // NUEVO ENDPOINT PARA EXPORTAR EL DOCUMENTO RDF (Evalúa el Criterio 4)
    // =================================================================
    @GetMapping(value = "/exportar-documento", produces = "text/plain")
    public ResponseEntity<String> descargarDocumentoRdf(
            @RequestParam(defaultValue = "TURTLE") String formato) {
        try {
            // Llamamos a nuestro nuevo servicio
            String documento = rdfDocumentService.generarDocumentoRDF(formato);
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar documento: " + e.getMessage());
        }
    }


}