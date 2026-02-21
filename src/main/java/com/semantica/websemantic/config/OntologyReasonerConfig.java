package com.semantica.websemantic.config;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OntologyReasonerConfig {

    private final String NS = "http://localhost:8081/ontology#";

    @Bean
    public OntModel ontologyModel(Dataset dataset) {
        // 1. Envolvemos tu modelo base con un Reasoner (Motor de Inferencia OWL)
        // Usamos OWL_MEM_MICRO_RULE_INF: Soporta propiedades inversas, simétricas y jerarquías
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, dataset.getDefaultModel());

        // Iniciamos transacción para guardar las reglas en el grafo
        dataset.begin(ReadWrite.WRITE);
        try {
            // --- INFERENCIAS LÓGICAS (REGLAS) ---

            // REGLA 1: Inversa entre Mecánico y Mantenimiento
            // Si A es realizadoPor B -> B realizaMantenimiento A
            ObjectProperty realizadoPor = ontModel.createObjectProperty(NS + "realizadoPor");
            ObjectProperty realizaMantenimiento = ontModel.createObjectProperty(NS + "realizaMantenimiento");
            realizadoPor.addInverseOf(realizaMantenimiento);

            // REGLA 2: Inversa entre Vehículo y Mantenimiento
            // Si A es realizadoEn B -> B tieneMantenimiento A
            ObjectProperty realizadoEn = ontModel.createObjectProperty(NS + "realizadoEn");
            ObjectProperty tieneMantenimiento = ontModel.createObjectProperty(NS + "tieneMantenimiento");
            realizadoEn.addInverseOf(tieneMantenimiento);

            // REGLA 3: Inversa entre Mantenimiento y Servicio
            // Si A incluyeServicio B -> B esAplicadoEn A
            ObjectProperty incluyeServicio = ontModel.createObjectProperty(NS + "incluyeServicio");
            ObjectProperty aplicadoEn = ontModel.createObjectProperty(NS + "aplicadoEn");
            incluyeServicio.addInverseOf(aplicadoEn);

            dataset.commit();
        } finally {
            dataset.end();
        }

        // Retornamos el modelo inteligente para que Spring lo use en los controladores
        return ontModel;
    }
}