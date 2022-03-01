package org.team5148.rapidreact.autonomous;

import org.team5148.lib.Vector3;

public class AutoInput {
    public Vector3 move = new Vector3(0, 0, 0);
    public boolean isShooting = false;

    public AutoInput() {}

    public AutoInput(Vector3 move, boolean isShooting) {
        this.move = move;
        this.isShooting = isShooting;
    }
}
