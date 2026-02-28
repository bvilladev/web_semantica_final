package com.semantica.websemantic.config;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OntologyReasonerConfig {

    private final String NS = "http://localhost:8081/ontology#";
    // Incorporamos el vocabulario estándar mundial FOAF para personas
    private final String FOAF_NS = "http://xmlns.com/foaf/0.1/";


    @Bean
    public OntModel ontologyModel(Dataset dataset) {
        // 1. Envolvemos tu modelo base con un Reasoner (Motor de Inferencia OWL)
        // Usamos OWL_MEM_MICRO_RULE_INF: Soporta propiedades inversas, simétricas y jerarquías
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, dataset.getDefaultModel());


        // Iniciamos transacción para guardar las reglas en el grafo
        dataset.begin(ReadWrite.WRITE);
        try {

            // =================================================================
            // 1. REUTILIZACIÓN DE ONTOLOGÍAS EXTERNAS (LINKED DATA)
            // =================================================================
            Resource foafPerson = ontModel.createResource(FOAF_NS + "Person");
            foafPerson.addProperty(RDF.type, RDFS.Class);

            // =================================================================
            // 2. DEFINICIÓN DE SUPERCLASES (NIVEL ALTO)
            // =================================================================
            Resource clasePersona = ontModel.createResource(NS + "Persona");
            clasePersona.addProperty(RDF.type, RDFS.Class);
            clasePersona.addProperty(RDFS.subClassOf, foafPerson); // Conexión con el exterior

            Resource claseObjetoFisico = ontModel.createResource(NS + "ObjetoFisico");
            claseObjetoFisico.addProperty(RDF.type, RDFS.Class);

            Resource claseEvento = ontModel.createResource(NS + "Evento");
            claseEvento.addProperty(RDF.type, RDFS.Class);

            Resource claseProcedimiento = ontModel.createResource(NS + "Procedimiento");
            claseProcedimiento.addProperty(RDF.type, RDFS.Class);

            // =================================================================
            // 3. DEFINICIÓN DE TUS CLASES (SUBCLASES DEL DOMINIO)
            // =================================================================
            Resource claseMecanico = ontModel.createResource(NS + "Mecanico");
            claseMecanico.addProperty(RDF.type, RDFS.Class);
            claseMecanico.addProperty(RDFS.subClassOf, clasePersona);

            Resource claseVehiculo = ontModel.createResource(NS + "Vehiculo");
            claseVehiculo.addProperty(RDF.type, RDFS.Class);
            claseVehiculo.addProperty(RDFS.subClassOf, claseObjetoFisico);

            Resource claseMantenimiento = ontModel.createResource(NS + "Mantenimiento");
            claseMantenimiento.addProperty(RDF.type, RDFS.Class);
            claseMantenimiento.addProperty(RDFS.subClassOf, claseEvento);

            Resource claseServicio = ontModel.createResource(NS + "Servicio");
            claseServicio.addProperty(RDF.type, RDFS.Class);
            claseServicio.addProperty(RDFS.subClassOf, claseProcedimiento);

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