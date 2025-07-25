package Entities;

import Statuses.Status;
import Weapons.Weapon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
public class Entity implements Serializable {
    public int row = 0;
    public int col = 0;
    public int heat = 1;
    public int hp = 1;
    public int movement = 1;
    ArrayList<Status> statuses = new ArrayList<>();
    ArrayList<Weapon> weapons = new ArrayList<>();
}