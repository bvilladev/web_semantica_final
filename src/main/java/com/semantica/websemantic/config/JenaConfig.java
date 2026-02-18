package com.semantica.websemantic.config;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JenaConfig {

    @Bean
    public Dataset jenaDataset() {
        // Crea un dataset en memoria.
        // Si quisieras persistencia (guardar en disco), usarías TDBFactory.createDataset("carpeta-datos");
        return DatasetFactory.createTxnMem();
    }
}