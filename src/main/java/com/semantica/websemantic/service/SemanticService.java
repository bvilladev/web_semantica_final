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
    private final String NS = "http://localhost:8081/ontology#";

    public SemanticService(Dataset dataset) {
        this.dataset = dataset;
    }

    // --- GUARDAR VEHÍCULO ---
    public void procesarVehiculo(VehiculoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            // CORRECCIÓN: Usamos la placa desde el inicio
            Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getPlaca());

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
            // CORRECCIÓN: Usamos el nombre desde el inicio (quitando espacios por seguridad)
            String nombreURI = dto.getNombre().replaceAll(" ", "_");
            Resource mecanicoRes = model.createResource(NS + "mecanico/" + nombreURI);

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

            mantRes.addProperty(RDF.type, model.createResource(NS + "Mantenimiento"));

            // Conectar con el Vehículo existente (Usando placa)
            if(dto.getVehiculo() != null && dto.getVehiculo().getPlaca() != null) {
                Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getVehiculo().getPlaca());
                mantRes.addProperty(model.createProperty(NS + "realizadoEn"), vehiculoRes);
            }

            // Conectar con el Mecánico existente (Usando nombre sin espacios)
            if(dto.getMecanico() != null && dto.getMecanico().getNombre() != null) {
                String nombreURI = dto.getMecanico().getNombre().replaceAll(" ", "_");
                Resource mecanicoRes = model.createResource(NS + "mecanico/" + nombreURI);
                mantRes.addProperty(model.createProperty(NS + "realizadoPor"), mecanicoRes);
            }

            dataset.commit();
        } finally { dataset.end(); }
    }

    public Dataset getDataset() { return dataset; }
}