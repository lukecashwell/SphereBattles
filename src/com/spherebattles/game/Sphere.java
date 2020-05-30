package com.spherebattles.game;

import org.bukkit.Location;
import org.bukkit.World;

public class Sphere {

	public int x,y,z,radius;
	
	public Sphere(int x, int y, int z, int radius) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
	}
	
	public Location getLocation(World world) {
		return new Location(world, x, y, z);
	} 
	
	public int getRadius() {
		return this.radius;
	}
	
	public boolean isNear(Sphere sphere) {
		int totalRadius = sphere.radius + this.radius;
		int squaredRadius = totalRadius*totalRadius;
		int dx = this.x - sphere.x;
		int dy = this.y - sphere.y;
		int dz = this.z - sphere.z;
		return squaredRadius > dx*dx + dy*dy + dz*dz;
	}
	
}
