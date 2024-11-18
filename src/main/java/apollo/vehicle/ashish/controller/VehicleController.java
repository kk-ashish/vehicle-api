package apollo.vehicle.ashish.controller;

import apollo.vehicle.ashish.model.Vehicle;
import apollo.vehicle.ashish.service.VehicleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Vehicle API", description = "CRUD operations for vehicles")
@RestController
@RequestMapping("/api/vehicles")


public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        // Check if a vehicle with the same VIN already exists
        if (vehicleRepository.findById(vehicle.getVin()).isPresent()) {
            throw new IllegalStateException("Vehicle with VIN " + vehicle.getVin() + " already exists");
        }
        try {
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
        } catch (Exception ex) {

            if (ex instanceof DataIntegrityViolationException) {
                throw new DataIntegrityViolationException("Invalid data or null value");
            } else {
                throw ex;
            }
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<String> createVehicles(@RequestBody List<Vehicle> vehicles) {
        List<Vehicle> savedVehicles = new ArrayList<>();
        List<Map<String, String>> errors = new ArrayList<>();

        int index = 0;
        for (Vehicle vehicle : vehicles) {
            index++;
            if (vehicleRepository.findById(vehicle.getVin()).isPresent()) {
                throw new IllegalStateException("Saved " + (index - 1) + " Vehicle(s). Vehicle of " + index + " with VIN " + vehicle.getVin() + " already exists");
            }
            try {
                Vehicle savedVehicle = vehicleRepository.save(vehicle);

            } catch (Exception ex) {

                if (ex instanceof DataIntegrityViolationException) {
                    throw new DataIntegrityViolationException("Saved " + (index - 1) + " Vehicle(s). Vehicle of " + index + "  has invalid data or null value");
                } else {
                    throw ex;
                }
            }
        }
        return new ResponseEntity<>("Saved " + index + " Vehicle(s)", HttpStatus.CREATED);
    }


    // Get all Vehicles
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    // Get a single Vehicle by VIN
    @GetMapping("/{vin}")
    public ResponseEntity<Vehicle> getVehicleByVin(@PathVariable String vin) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(vin);
        return vehicle.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a Vehicle by VIN
    @PutMapping("/{vin}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable String vin, @RequestBody Vehicle updatedVehicle) {
        return vehicleRepository.findById(vin).map(existingVehicle -> {
            existingVehicle.setManufacturerName(updatedVehicle.getManufacturerName());
            existingVehicle.setDescription(updatedVehicle.getDescription());
            existingVehicle.setHorsePower(updatedVehicle.getHorsePower());
            existingVehicle.setModelName(updatedVehicle.getModelName());
            existingVehicle.setModelYear(updatedVehicle.getModelYear());
            existingVehicle.setPurchasePrice(updatedVehicle.getPurchasePrice());
            existingVehicle.setFuelType(updatedVehicle.getFuelType());
            Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
            return new ResponseEntity<>(savedVehicle, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete a Vehicle by VIN
    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String vin) {
        if (vehicleRepository.existsById(vin)) {
            vehicleRepository.deleteById(vin);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}