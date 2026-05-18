package utils;

import enums.RoomType;
import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roomId;
    private String name;
    private int capacity;
    private RoomType type;
    private boolean occupied;

    public Room(String roomId, String name, int capacity, RoomType type) {
        this.roomId = roomId;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.occupied = false;
    }

    public boolean isAvailable()        { return !occupied; }
    public void occupy()                { this.occupied = true; }
    public void free()                  { this.occupied = false; }

    public String getRoomId()           { return roomId; }
    public String getName()             { return name; }
    public int getCapacity()            { return capacity; }
    public RoomType getType()           { return type; }

    @Override
    public String toString() {
        return "Room{" + name + ", capacity=" + capacity + ", type=" + type + ", available=" + !occupied + "}";
    }
}
 