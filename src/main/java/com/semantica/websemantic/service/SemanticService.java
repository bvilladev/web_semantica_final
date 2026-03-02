package com.semantica.websemantic.service;


import com.semantica.websemantic.entity.dto.MantenimientoDTO;
import com.semantica.websemantic.entity.dto.MecanicoDTO;
import com.semantica.websemantic.entity.dto.ServicioDTO;
import com.semantica.websemantic.entity.dto.VehiculoDTO;
import com.semantica.websemantic.repository.SemanticRepository;
import com.semantica.websemantic.vocabulary.SemanticVocab;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.springframework.stereotype.Service;



@Service
public class SemanticService {

    private final SemanticRepository repository;

    public SemanticService(SemanticRepository repository) {
        this.repository = repository;
    }

    // --- 1. PROCESAR VEHÍCULO ---
    public void procesarVehiculo(VehiculoDTO dto) {
        repository.guardarEnGrafo(() -> {
            OntModel model = repository.getOntModel();

            Resource vehiculoRes = model.createResource(SemanticVocab.NS + "vehiculo/" + dto.getPlaca());

            vehiculoRes.addProperty(RDF.type, model.createResource(SemanticVocab.NS + "Vehiculo"));

            // Atributos de la entidad Vehiculo
            if(dto.getPlaca() != null) vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "placa"), dto.getPlaca());
            if(dto.getMarca() != null) vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "marcaLiteral"), dto.getMarca());
            if(dto.getModelo() != null) vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "modelo"), dto.getModelo());
            if(dto.getColor() != null) vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "colorLiteral"), dto.getColor());
            if(dto.getNombrePropietario() != null) vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "nombrePropietario"), dto.getNombrePropietario());

            // ====================================================================
            // 2. LA MAGIA DE LINKED DATA: Enlazando con el exterior (DBpedia)
            // ====================================================================
            if(dto.getMarca() != null) {
                // Creamos un enlace real (URI) hacia DBpedia reemplazando espacios con guiones bajos
                String marcaFormateada = dto.getMarca().replace(" ", "_");

                // ¡AQUÍ USAMOS SemanticVocab.DBPEDIA_RES!
                Resource marcaExternaURI = model.createResource(SemanticVocab.DBPEDIA_RES + marcaFormateada);

                // C) Le decimos a la máquina que la marca del vehículo apunta a esa URI de Internet
                vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "marcaVinculada"), marcaExternaURI);

                String colorForm = dto.getColor().trim().replace(" ", "_");
                Resource colorUri = model.createResource(SemanticVocab.DBPEDIA_RES + colorForm);
                vehiculoRes.addProperty(model.createProperty(SemanticVocab.NS + "color"), colorUri);

            }

        });
    }
    // --- 2. PROCESAR MECÁNICO ---
    public void procesarMecanico(MecanicoDTO dto) {
        repository.guardarEnGrafo(() -> {
            OntModel model = repository.getOntModel();
            String nombreURI = dto.getNombre().replaceAll(" ", "_");
            Resource mecanicoRes = model.createResource(SemanticVocab.NS + "mecanico/" + nombreURI);

            mecanicoRes.addProperty(RDF.type, model.createResource(SemanticVocab.NS + "Mecanico"));

            // Atributos de la entidad Mecanico
            if(dto.getNombre() != null) mecanicoRes.addProperty(model.createProperty(SemanticVocab.NS + "nombre"), dto.getNombre());
            if(dto.getApellido() != null) mecanicoRes.addProperty(model.createProperty(SemanticVocab.NS + "apellido"), dto.getApellido());
            if(dto.getTelefono() != null) mecanicoRes.addProperty(model.createProperty(SemanticVocab.NS + "telefono"), dto.getTelefono());
            if(dto.getEmail() != null) mecanicoRes.addProperty(model.createProperty(SemanticVocab.NS + "email"), dto.getEmail());
            if(dto.getEspecialidad() != null) mecanicoRes.addProperty(model.createProperty(SemanticVocab.NS + "especialidad"), dto.getEspecialidad());

        });
    }

    // --- 3. PROCESAR SERVICIO (NUEVO) ---
    public void procesarServicio(ServicioDTO dto) {
        repository.guardarEnGrafo(() -> {
            OntModel model = repository.getOntModel();
            // Usamos el ID o un nombre formateado para la URI
            Resource servicioRes = model.createResource(SemanticVocab.NS + "servicio/" + dto.getId());

            servicioRes.addProperty(RDF.type, model.createResource(SemanticVocab.NS + "Servicio"));

            // Atributos de la entidad Servicio
            if(dto.getNombre() != null) servicioRes.addProperty(model.createProperty(SemanticVocab.NS + "nombre"), dto.getNombre());
            if(dto.getCosto() != null) servicioRes.addProperty(model.createProperty(SemanticVocab.NS + "costo"), String.valueOf(dto.getCosto()));
            if(dto.getDuracion() != null) servicioRes.addProperty(model.createProperty(SemanticVocab.NS + "duracion"), String.valueOf(dto.getDuracion()));
            if(dto.getObservacion() != null) servicioRes.addProperty(model.createProperty(SemanticVocab.NS + "observacion"), dto.getObservacion());

        });
    }
    // --- 4. PROCESAR MANTENIMIENTO (LA CONEXIÓN TOTAL) ---
    public void procesarMantenimiento(MantenimientoDTO dto) {
        repository.guardarEnGrafo(() -> {
            OntModel model = repository.getOntModel();
            Resource mantRes = model.createResource(SemanticVocab.NS + "mantenimiento/" + dto.getId());

            mantRes.addProperty(RDF.type, model.createResource(SemanticVocab.NS + "Mantenimiento"));

            // A. Atributos propios del Mantenimiento (Datos Literales)
            if(dto.getFecha() != null) mantRes.addProperty(model.createProperty(SemanticVocab.NS + "fecha"), dto.getFecha().toString());
            if(dto.getTipoMantenimiento() != null) mantRes.addProperty(model.createProperty(SemanticVocab.NS + "tipoMantenimiento"), dto.getTipoMantenimiento().toString());
            if(dto.getCostoTotal() != null) mantRes.addProperty(model.createProperty(SemanticVocab.NS + "costoTotal"), String.valueOf(dto.getCostoTotal()));
            if(dto.getObservaciones() != null) mantRes.addProperty(model.createProperty(SemanticVocab.NS + "observaciones"), dto.getObservaciones());

            // B. Conexión con Vehículo (Relación Objeto)
            if(dto.getVehiculo() != null && dto.getVehiculo().getPlaca() != null) {
                Resource vehiculoRes = model.createResource(SemanticVocab.NS + "vehiculo/" + dto.getVehiculo().getPlaca());
                mantRes.addProperty(model.createProperty(SemanticVocab.NS + "realizadoEn"), vehiculoRes);
            }

            // C. Conexión con Mecánico (Relación Objeto)
            if(dto.getMecanico() != null && dto.getMecanico().getNombre() != null) {
                String nombreURI = dto.getMecanico().getNombre().replaceAll(" ", "_");
                Resource mecanicoRes = model.createResource(SemanticVocab.NS + "mecanico/" + nombreURI);
                mantRes.addProperty(model.createProperty(SemanticVocab.NS + "realizadoPor"), mecanicoRes);
            }

            // D. Conexión Múltiple con Servicios (@ManyToMany)
            if(dto.getServicios() != null) {
                for(ServicioDTO serv : dto.getServicios()) {
                    Resource servRes = model.createResource(SemanticVocab.NS + "servicio/" + serv.getId());
                    // Creamos una tripleta por cada servicio que incluya este mantenimiento
                    mantRes.addProperty(model.createProperty(SemanticVocab.NS + "incluyeServicio"), servRes);
                }
            }
        });
    }
}
