package apollo.vehicle.ashish.controller;
import apollo.vehicle.ashish.exception.GlobalExceptionHandler;
import apollo.vehicle.ashish.model.Vehicle;
import apollo.vehicle.ashish.service.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@Import(GlobalExceptionHandler.class)
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Test data setup
    private Vehicle createTestVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin("1HGCM82633A123456");
        vehicle.setManufacturerName("Honda");
        vehicle.setDescription("Reliable sedan");
        vehicle.setHorsePower(180);
        vehicle.setModelName("Accord");
        vehicle.setModelYear(2020);
        vehicle.setPurchasePrice(new BigDecimal("22000.00"));
        vehicle.setFuelType("Gasoline");
        return vehicle;
    }

    // 1. Test creating a vehicle successfully
    @Test
    public void testCreateVehicle_Success() throws Exception {
        Vehicle vehicle = createTestVehicle();

        when(vehicleRepository.findById(vehicle.getVin())).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vin", is(vehicle.getVin())))
                .andExpect(jsonPath("$.manufacturerName", is(vehicle.getManufacturerName())));
    }

    // 2. Test creating a vehicle with existing VIN
    @Test
    public void testCreateVehicle_VinExists() throws Exception {
        Vehicle vehicle = createTestVehicle();

        when(vehicleRepository.findById(vehicle.getVin())).thenReturn(Optional.of(vehicle));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Vehicle with VIN " + vehicle.getVin() + " already exists")));
    }

    // 3. Test creating a vehicle with null values (should throw VehicleNullException)
    @Test
    public void testCreateVehicle_NullValues() throws Exception {
        Vehicle vehicle = createTestVehicle();
        vehicle.setHorsePower(null); // Set a required field to null

        when(vehicleRepository.findById(vehicle.getVin())).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isUnprocessableEntity()

                );
    }

    // 4. Test getting all vehicles
    @Test
    public void testGetAllVehicles() throws Exception {
        Vehicle vehicle1 = createTestVehicle();
        Vehicle vehicle2 = createTestVehicle();
        vehicle2.setVin("1HGCM82633A654321");
        vehicle2.setModelName("Civic");

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        mockMvc.perform(get("/api/vehicles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vin", is(vehicle1.getVin())))
                .andExpect(jsonPath("$[1].vin", is(vehicle2.getVin())));
    }

    // 5. Test getting a vehicle by VIN (vehicle exists)
    @Test
    public void testGetVehicleByVin_Found() throws Exception {
        Vehicle vehicle = createTestVehicle();

        when(vehicleRepository.findById(vehicle.getVin())).thenReturn(Optional.of(vehicle));

        mockMvc.perform(get("/api/vehicles/{vin}", vehicle.getVin())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin", is(vehicle.getVin())))
                .andExpect(jsonPath("$.manufacturerName", is(vehicle.getManufacturerName())));
    }

    // 6. Test getting a vehicle by VIN (vehicle does not exist)
    @Test
    public void testGetVehicleByVin_NotFound() throws Exception {
        String vin = "NONEXISTENTVIN";

        when(vehicleRepository.findById(vin)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/vehicles/{vin}", vin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // 7. Test updating a vehicle successfully
    @Test
    public void testUpdateVehicle_Success() throws Exception {
        Vehicle existingVehicle = createTestVehicle();
        Vehicle updatedVehicle = createTestVehicle();
        updatedVehicle.setManufacturerName("Toyota");

        when(vehicleRepository.findById(existingVehicle.getVin())).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);

        mockMvc.perform(put("/api/vehicles/{vin}", existingVehicle.getVin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.manufacturerName", is("Toyota")));
    }

    // 8. Test updating a vehicle that does not exist
    @Test
    public void testUpdateVehicle_NotFound() throws Exception {
        Vehicle updatedVehicle = createTestVehicle();

        when(vehicleRepository.findById(updatedVehicle.getVin())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/vehicles/{vin}", updatedVehicle.getVin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVehicle)))
                .andExpect(status().isNotFound());
    }

    // 9. Test deleting a vehicle successfully
    @Test
    public void testDeleteVehicle_Success() throws Exception {
        String vin = "1HGCM82633A123456";

        when(vehicleRepository.existsById(vin)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(vin);

        mockMvc.perform(delete("/api/vehicles/{vin}", vin))
                .andExpect(status().isNoContent());
    }

    // 10. Test deleting a vehicle that does not exist
    @Test
    public void testDeleteVehicle_NotFound() throws Exception {
        String vin = "NONEXISTENTVIN";

        when(vehicleRepository.existsById(vin)).thenReturn(false);

        mockMvc.perform(delete("/api/vehicles/{vin}", vin))
                .andExpect(status().isNotFound());
    }
}
