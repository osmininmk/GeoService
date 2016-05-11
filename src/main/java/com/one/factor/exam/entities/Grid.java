package com.one.factor.exam.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "grid")
public class Grid implements Serializable {

    @Id
    @Column(name = "tile_x", nullable = false)
    private Integer tileX;

    @Id
    @Column(name = "tile_y", nullable = false)
    private Integer tileY;

    @Column(name = "distance_error", nullable = false)
    private int distanceError;

    public int getTileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public int getDistanceError() {
        return distanceError;
    }

    public void setDistanceError(int distanceError) {
        this.distanceError = distanceError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grid grid = (Grid) o;

        if (tileX != null ? !tileX.equals(grid.tileX) : grid.tileX != null) return false;
        return tileY != null ? tileY.equals(grid.tileY) : grid.tileY == null;

    }

    @Override
    public int hashCode() {
        int result = tileX != null ? tileX.hashCode() : 0;
        result = 31 * result + (tileY != null ? tileY.hashCode() : 0);
        return result;
    }
}
