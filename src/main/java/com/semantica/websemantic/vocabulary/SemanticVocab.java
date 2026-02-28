package com.semantica.websemantic.vocabulary;

public class SemanticVocab {

    // 1. TU ONTOLOGÍA LOCAL (El esquema de tu taller)
    public static final String NS = "http://localhost:8081/ontology#";

    // 2. VOCABULARIO FOAF (Friend of a Friend - Para personas)
    public static final String FOAF = "http://xmlns.com/foaf/0.1/";

    // 3. VOCABULARIO DBPEDIA (Linked Data - Para marcas externas)
    public static final String DBPEDIA_RES = "http://es.dbpedia.org/resource/";

    // Nota: No necesitas instanciar esta clase, por eso usamos static final
    private SemanticVocab() {
        // Constructor privado para evitar que alguien intente hacer un 'new SemanticVocab()'
    }
}