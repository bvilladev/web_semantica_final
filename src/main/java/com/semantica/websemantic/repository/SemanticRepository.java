package com.semantica.websemantic.repository;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayOutputStream;

// La anotación @Repository le dice a Spring que esta clase maneja la base de datos
@Repository
public class SemanticRepository {

    private final OntModel ontModel;
    private final Dataset dataset;

    // Inyectamos los modelos que vienen de tu OntologyReasonerConfig
    public SemanticRepository(OntModel ontModel, Dataset dataset) {
        this.ontModel = ontModel;
        this.dataset = dataset;
    }

    // ====================================================================
    // 1. CÓDIGO EXTRAÍDO DE: SemanticController
    // Objetivo: Centralizar las consultas SPARQL aquí.
    // ====================================================================
    public String ejecutarConsultaSparql(String queryStr) throws Exception {
        try (QueryExecution qExec = QueryExecutionFactory.create(queryStr, ontModel)) {
            ResultSet results = qExec.execSelect();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(outputStream, results);
            return outputStream.toString();
        }
    }

    // ====================================================================
    // 2. CÓDIGO EXTRAÍDO DE: SemanticService
    // Objetivo: Manejar las transacciones de escritura (ReadWrite.WRITE)
    // de forma segura en una sola parte del proyecto.
    // ====================================================================
    public void guardarEnGrafo(Runnable operacionesJena) {
        dataset.begin(ReadWrite.WRITE);
        try {
            // Aquí se ejecutarán las tripletas que el Service genere
            operacionesJena.run();
            dataset.commit();
        } finally {
            dataset.end();
        }
    }

    // Método para exponer el modelo al Service cuando necesite crear Recursos
    public OntModel getOntModel() {
        return ontModel;
    }
}