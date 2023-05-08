
import java.util.*;

class Car {
    private String carNo;
    private String checkInTime;
    private String checkOutTime;
    private String category;
    private int Parkingflor;
    private int ParkingSlot;
    private int charge;

    Car(String carNo, String checkInTime, String cate) {
        this.carNo = carNo;
        this.checkInTime = checkInTime;
        this.category = cate;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setParkingSlot(int parkingSlot) {
        ParkingSlot = parkingSlot;
    }

    public void setParkingflor(int parkingflor) {
        Parkingflor = parkingflor;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCarNo() {
        return carNo;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public int getParkingSlot() {
        return ParkingSlot;
    }

    public int getParkingflor() {
        return Parkingflor;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public int getCharge() {
        return charge;
    }

    public String getCategory() {
        return category;
    }
}

class Floor {
    private int totalSlots;
    private List<Slot> slots;

    Floor(int slot) {
        this.totalSlots = slot;
        this.slots = new ArrayList<>();
    }

    // Create and Set slots in this Floor with slotReservedStatus 0
    void setSlot() {
        for (int i = 0; i < totalSlots; i++) {
            Slot slot = new Slot(0);
            this.slots.add(slot);
        }
    }

    public List<Slot> getSlots() {
        return this.slots;
    }

    public Slot getSlot(int s) {
        return this.slots.get(s);
    }
}

class Slot {
    // SlotReservedStatus = 0 => Empty and Non Reserved slot
    // SlotReservedStatus = 1 => Empty and Reserved slot
    // SlotReservedStatus = 2 => Full and Non Reserved
    // SlotReservedStatus = 3 => Full and Reserved

    private int slotReservedStatus;

    Slot(int slotReservedStatus) {
        this.slotReservedStatus = slotReservedStatus;
    }

    public void setSlotReservedStatus(int slotReservedStatus) {
        this.slotReservedStatus = slotReservedStatus;
    }

    public int getSlotReservedStatus() {
        return slotReservedStatus;
    }

}

public class ParkingManagement {
    private List<Floor> floors;
    private List<String> carNOList;
    private List<Car> cars;
    private List<Car> allCars;
    private int totalFloor;
    private int totalSlots;

    ParkingManagement(int f, int s) {
        this.floors = new ArrayList<>();
        this.cars = new ArrayList<>();
        this.carNOList = new ArrayList<>();
        this.allCars = new ArrayList<>();
        this.totalFloor = f;
        this.totalSlots = s;
    }

    public void setFloors(Floor floor) {
        this.floors.add(floor);
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setCars(Floor flor) {
        this.floors.add(flor);
    }

    public List<Car> getCars() {
        return cars;
    }

    // CheckIn
    String checkIn(String carNo, String checkInTime, String category) {
        if (carNOList.contains(carNo)) {
            return "Car Already Parked!!";
        }
        for (int f = 0; f < totalFloor; f++) {
            Floor floor = this.floors.get(f);
            for (int s = 0; s < totalSlots; s++) {
                String thisSlot = (char) ('A' + f) + "-" + ((int) s + (int) 1);
                if (category.equals("NR")) {
                    if (floor.getSlot(s).getSlotReservedStatus() == 0) {
                        return checkInCar(carNo, checkInTime, category, 2, f, s, floor, thisSlot);
                    }
                } else if (category.equals("R")) {
                    if (floor.getSlot(s).getSlotReservedStatus() == 1) {
                        return checkInCar(carNo, checkInTime, category, 3, f, s, floor, thisSlot);
                    }
                }
            }
        }
        return "PARKING FULL";
    }

    String checkInCar(String carNo, String checkInTime, String category, int slotReservedStatus, int f, int s,
            Floor floor, String thisSlot) {
        Car car = new Car(carNo, checkInTime, category);
        car.setParkingSlot(s + 1);
        car.setParkingflor(f + 1);
        floor.getSlot(s).setSlotReservedStatus(slotReservedStatus);
        carNOList.add(carNo);
        cars.add(car);
        return thisSlot;
    }

    int checkOut(String carNo, String checkOutTime) {
        for (Car car : cars) {
            if (car.getCarNo().equals(carNo)) {
                int timeDurationInHour = FindTimeDuration(car.getCheckInTime(), checkOutTime);
                int charge = CalculateCharge(timeDurationInHour);
                car.setCharge(charge);
                car.setCheckOutTime(checkOutTime);
                allCars.add(car);

                if (floors.get(car.getParkingflor() - 1).getSlot(car.getParkingSlot() - 1)
                        .getSlotReservedStatus() == 2) {
                    floors.get(car.getParkingflor() - 1).getSlot(car.getParkingSlot() - 1).setSlotReservedStatus(0);
                } else if (floors.get(car.getParkingflor() - 1).getSlot(car.getParkingSlot() - 1)
                        .getSlotReservedStatus() == 3) {
                    floors.get(car.getParkingflor() - 1).getSlot(car.getParkingSlot() - 1).setSlotReservedStatus(1);
                }
                carNOList.remove(carNo);
                cars.remove(car);
                return charge;
            }
        }
        return -1;
    }

    void GenerateReport() {
        allCars.sort(
                Comparator.comparing((Car car) -> car.getParkingflor()).thenComparing((Car car) -> car.getParkingSlot())
                        .thenComparing((Car car) -> TimeFormat(car.getCheckInTime())));

        for (Car car : allCars) {
            String slot = (char) ('A' + car.getParkingflor() - 1) + "-" + (int) (car.getParkingSlot());
            System.out.println(slot + " , " + car.getCarNo() + " , " + car.getCheckInTime() + " , "
                    + car.getCheckOutTime() + " , " + car.getCharge() + " , " + car.getCategory());
        }
    }

    int CalculateCharge(int h) {
        if (h <= 2)
            return 50;
        else if (h >= 2 && h <= 4)
            return 80;
        else
            return 100;
    }

    int FindTimeDuration(String checkInTime, String checkOutTime) {

        String in = TimeFormat(checkInTime);
        String out = TimeFormat(checkOutTime);
        int hour = Integer.parseInt(out.substring(0, 2)) - Integer.parseInt(in.substring(0, 2));
        int minute = (Integer.parseInt(out.substring(3, 5)) - Integer.parseInt(in.substring(3, 5))) / 60;
        return hour + minute;
    }

    String TimeFormat(String t) {
        String time = "";
        if (t.substring(5).equals("pm")) {
            int hour;
            if (t.substring(0, 2).equals("12")) {
                hour = 12;
            } else {
                hour = Integer.parseInt(t.substring(0, 2)) + 12;
            }
            time = hour + t.substring(2, 4);
        } else {
            time = t.substring(0, 5);
        }
        return time;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int noOfFloor = Integer.parseInt(sc.nextLine());
        int noOfSlots = Integer.parseInt(sc.nextLine());

        ParkingManagement pms = new ParkingManagement(noOfFloor, noOfSlots);

        for (int i = 0; i < noOfFloor; i++) {
            Floor flor = new Floor(noOfSlots);
            flor.setSlot();
            pms.setFloors(flor);
        }
        // Reseved Slots
        String reservedSlot = sc.nextLine();
        String[] reservedArr = reservedSlot.split(" ");
        for (int i = 0; i < reservedArr.length; i++) {
            int floor = reservedArr[i].charAt(0) - 'A';
            int slot = reservedArr[i].charAt(2) - '0';
            pms.floors.get(floor).getSlot(slot - 1).setSlotReservedStatus(1);
        }

        while (true) {
            String inputCar = sc.nextLine();
            String[] arr = inputCar.split(" ");

            if (arr.length == 4) {
                System.out.println(pms.checkIn(arr[1], arr[2], arr[3]));
            } else if (arr.length == 3) {
                System.out.println(pms.checkOut(arr[1], arr[2]));
            } else if (arr.length == 2) {
                pms.GenerateReport();
            }
        }
    }
}
