package com.semantica.websemantic.service;

import com.semantica.websemantic.repository.SemanticRepository;
import org.apache.jena.ontology.OntModel;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class RdfDocumentService {

    private final SemanticRepository repository;

    // Inyectamos el repositorio que creamos en el paso anterior
    public RdfDocumentService(SemanticRepository repository) {
        this.repository = repository;
    }

    /**
     * Genera un documento RDF a partir del grafo actual.
     * @param formato Puede ser "TURTLE", "RDF/XML", "N-TRIPLES" o "JSON-LD"
     * @return El documento RDF en formato de texto.
     */
    public String generarDocumentoRDF(String formato) {
        // 1. Obtenemos el modelo inteligente completo
        OntModel model = repository.getOntModel();

        // 2. Preparamos un "escritor" para guardar el texto
        StringWriter writer = new StringWriter();

        // 3. Jena hace la magia: convierte las tripletas en un documento RDF
        model.write(writer, formato);

        // 4. Retornamos el documento
        return writer.toString();
    }
}