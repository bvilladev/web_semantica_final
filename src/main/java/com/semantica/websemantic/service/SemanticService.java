package com.semantica.websemantic.service;


import com.semantica.websemantic.dto.MantenimientoDTO;
import com.semantica.websemantic.dto.MecanicoDTO;
import com.semantica.websemantic.dto.ServicioDTO;
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

    // --- 1. PROCESAR VEHÍCULO ---
    public void procesarVehiculo(VehiculoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getPlaca());

            vehiculoRes.addProperty(RDF.type, model.createResource(NS + "Vehiculo"));

            // Atributos de la entidad Vehiculo
            if(dto.getPlaca() != null) vehiculoRes.addProperty(model.createProperty(NS + "placa"), dto.getPlaca());
            if(dto.getMarca() != null) vehiculoRes.addProperty(model.createProperty(NS + "marca"), dto.getMarca());
            if(dto.getModelo() != null) vehiculoRes.addProperty(model.createProperty(NS + "modelo"), dto.getModelo());
            if(dto.getColor() != null) vehiculoRes.addProperty(model.createProperty(NS + "color"), dto.getColor());
            if(dto.getNombrePropietario() != null) vehiculoRes.addProperty(model.createProperty(NS + "nombrePropietario"), dto.getNombrePropietario());

            dataset.commit();
        } finally { dataset.end(); }
    }

    // --- 2. PROCESAR MECÁNICO ---
    public void procesarMecanico(MecanicoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            String nombreURI = dto.getNombre().replaceAll(" ", "_");
            Resource mecanicoRes = model.createResource(NS + "mecanico/" + nombreURI);

            mecanicoRes.addProperty(RDF.type, model.createResource(NS + "Mecanico"));

            // Atributos de la entidad Mecanico
            if(dto.getNombre() != null) mecanicoRes.addProperty(model.createProperty(NS + "nombre"), dto.getNombre());
            if(dto.getApellido() != null) mecanicoRes.addProperty(model.createProperty(NS + "apellido"), dto.getApellido());
            if(dto.getTelefono() != null) mecanicoRes.addProperty(model.createProperty(NS + "telefono"), dto.getTelefono());
            if(dto.getEmail() != null) mecanicoRes.addProperty(model.createProperty(NS + "email"), dto.getEmail());
            if(dto.getEspecialidad() != null) mecanicoRes.addProperty(model.createProperty(NS + "especialidad"), dto.getEspecialidad());

            dataset.commit();
        } finally { dataset.end(); }
    }

    // --- 3. PROCESAR SERVICIO (NUEVO) ---
    public void procesarServicio(ServicioDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            // Usamos el ID o un nombre formateado para la URI
            Resource servicioRes = model.createResource(NS + "servicio/" + dto.getId());

            servicioRes.addProperty(RDF.type, model.createResource(NS + "Servicio"));

            // Atributos de la entidad Servicio
            if(dto.getNombre() != null) servicioRes.addProperty(model.createProperty(NS + "nombre"), dto.getNombre());
            if(dto.getCosto() != null) servicioRes.addProperty(model.createProperty(NS + "costo"), String.valueOf(dto.getCosto()));
            if(dto.getDuracion() != null) servicioRes.addProperty(model.createProperty(NS + "duracion"), String.valueOf(dto.getDuracion()));
            if(dto.getObservacion() != null) servicioRes.addProperty(model.createProperty(NS + "observacion"), dto.getObservacion());

            dataset.commit();
        } finally { dataset.end(); }
    }

    // --- 4. PROCESAR MANTENIMIENTO (LA CONEXIÓN TOTAL) ---
    public void procesarMantenimiento(MantenimientoDTO dto) {
        dataset.begin(ReadWrite.WRITE);
        try {
            Model model = dataset.getDefaultModel();
            Resource mantRes = model.createResource(NS + "mantenimiento/" + dto.getId());

            mantRes.addProperty(RDF.type, model.createResource(NS + "Mantenimiento"));

            // A. Atributos propios del Mantenimiento (Datos Literales)
            if(dto.getFecha() != null) mantRes.addProperty(model.createProperty(NS + "fecha"), dto.getFecha().toString());
            if(dto.getTipoMantenimiento() != null) mantRes.addProperty(model.createProperty(NS + "tipoMantenimiento"), dto.getTipoMantenimiento().toString());
            if(dto.getCostoTotal() != null) mantRes.addProperty(model.createProperty(NS + "costoTotal"), String.valueOf(dto.getCostoTotal()));
            if(dto.getObservaciones() != null) mantRes.addProperty(model.createProperty(NS + "observaciones"), dto.getObservaciones());

            // B. Conexión con Vehículo (Relación Objeto)
            if(dto.getVehiculo() != null && dto.getVehiculo().getPlaca() != null) {
                Resource vehiculoRes = model.createResource(NS + "vehiculo/" + dto.getVehiculo().getPlaca());
                mantRes.addProperty(model.createProperty(NS + "realizadoEn"), vehiculoRes);
            }

            // C. Conexión con Mecánico (Relación Objeto)
            if(dto.getMecanico() != null && dto.getMecanico().getNombre() != null) {
                String nombreURI = dto.getMecanico().getNombre().replaceAll(" ", "_");
                Resource mecanicoRes = model.createResource(NS + "mecanico/" + nombreURI);
                mantRes.addProperty(model.createProperty(NS + "realizadoPor"), mecanicoRes);
            }

            // D. Conexión Múltiple con Servicios (@ManyToMany)
            if(dto.getServicios() != null) {
                for(ServicioDTO serv : dto.getServicios()) {
                    Resource servRes = model.createResource(NS + "servicio/" + serv.getId());
                    // Creamos una tripleta por cada servicio que incluya este mantenimiento
                    mantRes.addProperty(model.createProperty(NS + "incluyeServicio"), servRes);
                }
            }

            dataset.commit();
        } finally { dataset.end(); }
    }

    public Dataset getDataset() { return dataset; }
}
