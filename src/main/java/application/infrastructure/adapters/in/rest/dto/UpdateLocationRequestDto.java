package application.infrastructure.adapters.in.rest.dto;

public class UpdateLocationRequestDto {
    private String newLocation;

    public UpdateLocationRequestDto() {}

    public String getNewLocation() { return newLocation; }
    public void setNewLocation(String newLocation) { this.newLocation = newLocation; }
}
