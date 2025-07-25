package Entities;
import Weapons.AutoRifle;
public class Lancer1 extends Entity {
    Lancer1(int row, int col) {
        this.row = row;
        this.col = col;
        this.hp = 6;
        this.heat = 4;
        this.movement = 4;
        //this.statuses = new ArrayList<>();
        this.weapons.add(new AutoRifle());
    }
}
