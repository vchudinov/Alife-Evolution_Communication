package sivi.neat.jneat.evolution;

import java.util.*;

/**
 * 
 * 
 * 
 */
public class order_species implements java.util.Comparator {
	/**
	 * order_species constructor comment.
	 */
	public order_species() {
		// super();
	}

	/**
   */
	public int compare(Object o1, Object o2) {

		Species _sx = (Species) o1;
		Species _sy = (Species) o2;

		Organism _ox = (Organism) _sx.organismsInSpecies.firstElement();
		Organism _oy = (Organism) _sy.organismsInSpecies.firstElement();

		if (_ox.orig_fitness < _oy.orig_fitness)
			return +1;
		if (_ox.orig_fitness > _oy.orig_fitness)
			return -1;
		return 0;

	}
}