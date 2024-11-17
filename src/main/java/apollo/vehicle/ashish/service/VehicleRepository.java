package apollo.vehicle.ashish.service;


import apollo.vehicle.ashish.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
