package com.semantica.websemantic.service;


import com.semantica.websemantic.dto.MantenimientoDTO;
import com.semantica.websemantic.dto.MecanicoDTO;
import com.semantica.websemantic.dto.VehiculoDTO;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;

@Service
public class SemanticService {

    private final Dataset dataset;
    // Definimos tu ontología base
    private final String NS = "http://localhost:8081/ontology#";

    public SemanticService(Dataset dataset) {
        this.dataset = dataset;
    }

    // --- GUARDAR VEHÍCULO ---
    public void procesarVehiculo(VehiculoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            // URI única: http://.../vehiculo/1
            Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getId());

            // Definir Tipo y Propiedades
            vehiculoRes.addProperty(RDF.type, model.createResource(NS + "Vehiculo"));
            if(dto.getPlaca() != null)
                vehiculoRes.addProperty(model.createProperty(NS + "placa"), dto.getPlaca());
            if(dto.getMarca() != null)
                vehiculoRes.addProperty(model.createProperty(NS + "marca"), dto.getMarca());

            dataset.commit();
        } finally { dataset.end(); }
    }

    // --- GUARDAR MECÁNICO ---
    public void procesarMecanico(MecanicoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            Resource mecanicoRes = model.createResource(NS + "mecanico/" + dto.getId());

            mecanicoRes.addProperty(RDF.type, model.createResource(NS + "Mecanico"));
            if(dto.getNombre() != null)
                mecanicoRes.addProperty(model.createProperty(NS + "nombre"), dto.getNombre());
            if(dto.getEspecialidad() != null)
                mecanicoRes.addProperty(model.createProperty(NS + "especialidad"), dto.getEspecialidad());

            dataset.commit();
        } finally { dataset.end(); }
    }

    // --- GUARDAR MANTENIMIENTO (LA CONEXIÓN) ---
    public void procesarMantenimiento(MantenimientoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            Resource mantRes = model.createResource(NS + "mantenimiento/" + dto.getId());

            // Tipo
            mantRes.addProperty(RDF.type, model.createResource(NS + "Mantenimiento"));

            // CONEXIONES (Object Properties)
            // Conectar con el Vehículo existente
            Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getVehiculo().getId());
            mantRes.addProperty(model.createProperty(NS + "realizadoEn"), vehiculoRes);

            // Conectar con el Mecánico existente
            Resource mecanicoRes = model.createResource(NS + "mecanico/" + dto.getMecanico().getId());
            mantRes.addProperty(model.createProperty(NS + "realizadoPor"), mecanicoRes);

            dataset.commit();
        } finally { dataset.end(); }
    }

    // Aquí iría el método getDataset() que hicimos antes
    public Dataset getDataset() { return dataset; }
}