package com.semantica.websemantic.config;

import com.semantica.websemantic.vocabulary.SemanticVocab;
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

    @Bean
    public OntModel ontologyModel(Dataset dataset) {
        // ¡ESTA ES LA LÍNEA QUE TE FALTABA O SE BORRÓ!
        // Aquí es donde "nace" la variable ontModel
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, dataset.getDefaultModel());

        dataset.begin(ReadWrite.WRITE);
        try {
            // =================================================================
            // 1. REUTILIZACIÓN DE ONTOLOGÍAS EXTERNAS (FOAF)
            // =================================================================
            // Usamos SemanticVocab.FOAF en lugar del texto quemado
            Resource foafPerson = ontModel.createResource(SemanticVocab.FOAF + "Person");
            foafPerson.addProperty(RDF.type, RDFS.Class);

            // =================================================================
            // 2. DEFINICIÓN DE SUPERCLASES
            // =================================================================
            // Usamos SemanticVocab.NS en lugar de la variable local NS
            Resource clasePersona = ontModel.createResource(SemanticVocab.NS + "Persona");
            clasePersona.addProperty(RDF.type, RDFS.Class);
            clasePersona.addProperty(RDFS.subClassOf, foafPerson);

            Resource claseObjetoFisico = ontModel.createResource(SemanticVocab.NS + "ObjetoFisico");
            claseObjetoFisico.addProperty(RDF.type, RDFS.Class);

            Resource claseEvento = ontModel.createResource(SemanticVocab.NS + "Evento");
            claseEvento.addProperty(RDF.type, RDFS.Class);

            Resource claseProcedimiento = ontModel.createResource(SemanticVocab.NS + "Procedimiento");
            claseProcedimiento.addProperty(RDF.type, RDFS.Class);

            // =================================================================
            // 3. DEFINICIÓN DE TUS CLASES (SUBCLASES)
            // =================================================================
            Resource claseMecanico = ontModel.createResource(SemanticVocab.NS + "Mecanico");
            claseMecanico.addProperty(RDF.type, RDFS.Class);
            claseMecanico.addProperty(RDFS.subClassOf, clasePersona);

            Resource claseVehiculo = ontModel.createResource(SemanticVocab.NS + "Vehiculo");
            claseVehiculo.addProperty(RDF.type, RDFS.Class);
            claseVehiculo.addProperty(RDFS.subClassOf, claseObjetoFisico);

            Resource claseMantenimiento = ontModel.createResource(SemanticVocab.NS + "Mantenimiento");
            claseMantenimiento.addProperty(RDF.type, RDFS.Class);
            claseMantenimiento.addProperty(RDFS.subClassOf, claseEvento);

            Resource claseServicio = ontModel.createResource(SemanticVocab.NS + "Servicio");
            claseServicio.addProperty(RDF.type, RDFS.Class);
            claseServicio.addProperty(RDFS.subClassOf, claseProcedimiento);

            // =================================================================
            // 4. PROPIEDADES, DOMINIOS, RANGOS E INFERENCIAS INVERSAS
            // =================================================================
            ObjectProperty realizadoPor = ontModel.createObjectProperty(SemanticVocab.NS + "realizadoPor");
            realizadoPor.addProperty(RDFS.domain, claseMantenimiento);
            realizadoPor.addProperty(RDFS.range, claseMecanico);

            ObjectProperty realizaMantenimiento = ontModel.createObjectProperty(SemanticVocab.NS + "realizaMantenimiento");
            realizadoPor.addInverseOf(realizaMantenimiento);

            ObjectProperty realizadoEn = ontModel.createObjectProperty(SemanticVocab.NS + "realizadoEn");
            realizadoEn.addProperty(RDFS.domain, claseMantenimiento);
            realizadoEn.addProperty(RDFS.range, claseVehiculo);

            ObjectProperty tieneMantenimiento = ontModel.createObjectProperty(SemanticVocab.NS + "tieneMantenimiento");
            realizadoEn.addInverseOf(tieneMantenimiento);

            ObjectProperty incluyeServicio = ontModel.createObjectProperty(SemanticVocab.NS + "incluyeServicio");
            incluyeServicio.addProperty(RDFS.domain, claseMantenimiento);
            incluyeServicio.addProperty(RDFS.range, claseServicio);

            ObjectProperty aplicadoEn = ontModel.createObjectProperty(SemanticVocab.NS + "aplicadoEn");
            incluyeServicio.addInverseOf(aplicadoEn);

            dataset.commit();
        } finally {
            dataset.end();
        }

        return ontModel;
    }
}