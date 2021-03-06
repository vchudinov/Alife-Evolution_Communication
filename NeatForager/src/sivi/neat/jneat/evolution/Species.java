/* Generated by Together */

package sivi.neat.jneat.evolution;

import java.util.*;
import java.text.*;

import sivi.neat.jNeatCommon.*;
import sivi.neat.jneat.Neat;
import sivi.neat.jneat.neuralNetwork.Genome;

public class Species extends Neat {
	/**
	 * id(-entification) of this species
	 */
	int id;

	/**
	 * The age of the Species
	 */
	int age;

	/**
	 * The average fitness of the Species
	 */
	double ave_fitness;

	/**
	 * Max fitness of the Species
	 */
	double max_fitness;

	/**
	 * The max it ever had
	 */
	double max_fitness_ever;

	/**
	 * how many child expected
	 */
	int expected_offspring;

	/**
	 * is new species ?
	 */
	boolean novel;

	/**
	 * has tested ?
	 */
	boolean checked;

	/**
	 * list of all organisms in the Species
	 */
	Vector<Organism> organismsInSpecies = new Vector<Organism>(1, 0);

	/**
	 * how many time from last updt? If this is too long ago, the Species will
	 * goes extinct.
	 */
	int age_of_last_improvement;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getAve_fitness() {
		return ave_fitness;
	}

	public void setAve_fitness(double ave_fitness) {
		this.ave_fitness = ave_fitness;
	}

	public double getMax_fitness() {
		return max_fitness;
	}

	public void setMax_fitness(double max_fitness) {
		this.max_fitness = max_fitness;
	}

	public double getMax_fitness_ever() {
		return max_fitness_ever;
	}

	public void setMax_fitness_ever(double max_fitness_ever) {
		this.max_fitness_ever = max_fitness_ever;
	}

	public int getExpected_offspring() {
		return expected_offspring;
	}

	public void setExpected_offspring(int expected_offspring) {
		this.expected_offspring = expected_offspring;
	}

	public boolean getNovel() {
		return novel;
	}

	public void setNovel(boolean novel) {
		this.novel = novel;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Vector<Organism> getOrganisms() {
		return organismsInSpecies;
	}

	public void setOrganisms(Vector<Organism> organisms) {
		this.organismsInSpecies = organisms;
	}

	public int getAge_of_last_improvement() {
		return age_of_last_improvement;
	}

	public void setAge_of_last_improvement(int age_of_last_improvement) {
		this.age_of_last_improvement = age_of_last_improvement;
	}

	/**
	 * costructor with inly ID of specie
	 */
	public Species(int i) {
		id = i;
		age = 1;
		ave_fitness = 0.0;
		expected_offspring = 0;
		novel = false;
		age_of_last_improvement = 0;
		max_fitness = 0;
		max_fitness_ever = 0;

	}

	/**
	 * add an organism to list of organisms in this specie
	 */

	public void add_Organism(Organism xorganism) {
		organismsInSpecies.add(xorganism);
	}

	/**
	 * Can change the fitness of the organisms in the Species to be higher for
	 * very new species (to protect them); Divides the fitness by the size of
	 * the Species, so that fitness is "shared" by the species At end mark the
	 * organisms can be eliminated from this specie
	 */
	public void adjust_fitness() {

		Organism _organism = null;
		int age_debt = 0;
		int j;
		int speciesSize = organismsInSpecies.size();

		age_debt = (age - age_of_last_improvement + 1) - Neat.p_dropoff_age;
		if (age_debt == 0) {
			age_debt = 1;
		}

		for (j = 0; j < speciesSize; j++) {
			_organism = (Organism) organismsInSpecies.elementAt(j);

			// Remember the original fitness before it gets modified
			_organism.orig_fitness = _organism.fitness;

			// Make fitness decrease after a stagnation point dropoff_age
			// Added an if to keep species pristine until the dropoff point
			if (age_debt >= 1) {
				_organism.fitness = _organism.fitness * 0.01;
			}

			// Give a fitness boost up to some young age (niching)
			// The age_significance parameter is a system parameter
			// if it is 1, then young species get no fitness boost
			if (age <= 10) {
				_organism.fitness = _organism.fitness * Neat.p_age_significance;
			}

			// Do not allow negative fitness
			if (_organism.fitness < 0.0) {
				_organism.fitness = 0.0001;
			}

			// Share fitness with the species
			_organism.fitness = _organism.fitness / speciesSize;

		}
	}

	/**
	 * Sorts the organisms in the species and marks those organisms for death
	 * that are after survival_thresh * pop_size
	 */
	public void markForDeath() {
		Iterator<Organism> itr_organism;
		Organism _organism = null;
		int num_parents = 0;
		int count = 0;
		int speciesSize = organismsInSpecies.size();
		Organism speciesChampion;

		// Sort organisms in species
		Comparator<Organism> cmp = new order_orgs();
		Collections.sort(organismsInSpecies, cmp);

		// Find champion of the species
		// (the first organism has the best fitness)
		speciesChampion = (Organism) organismsInSpecies.firstElement();
		speciesChampion.champion = true;

		// Update age_of_last_improvement here
		// (the first organism has the best fitness)

		if (speciesChampion.orig_fitness > max_fitness_ever) {
			age_of_last_improvement = age;
			max_fitness_ever = speciesChampion.orig_fitness;
		}

		// Decide how many get to reproduce based on survival_thresh*pop_size
		// Adding 1.0 ensures that at least one will survive
		// floor is the largest (closest to positive infinity) double value that
		// is not greater
		// than the argument and is equal to a mathematical integer

		num_parents = (int) Math
				.floor((Neat.p_survival_thresh * ((double) speciesSize)) + 1.0);

		// Go through the number of organisms that will be parents
		itr_organism = organismsInSpecies.iterator();
		count = 1;
		while (itr_organism.hasNext() && count <= num_parents) {
			_organism = ((Organism) itr_organism.next());
			count++;
		}

		// mark the rest of the organisms for elimination
		while (itr_organism.hasNext()) {
			_organism = ((Organism) itr_organism.next());
			_organism.eliminate = true;
		}
	}

	/**
	 * Read all organisms in this species and compute the summary of fitness; at
	 * and compute the average fitness (ave_fitness) with : ave_fitness =
	 * summary / (number of organisms) this is an average fitness for this
	 * species
	 */
	public void compute_average_fitness() {

		Iterator<Organism> itr_organism;
		itr_organism = organismsInSpecies.iterator();
		double total = 0.0;
		int size1 = organismsInSpecies.size();

		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());
			total += _organism.fitness;
		}

		ave_fitness = total / (double) size1;

	}

	/**
	 * Read all organisms in this specie and return the maximum fitness of all
	 * organisms.
	 */
	public void compute_max_fitness() {
		double max = 0.0;
		Iterator<Organism> itr_organism;
		itr_organism = organismsInSpecies.iterator();

		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());
			if (_organism.fitness > max)
				max = _organism.fitness;
		}
		max_fitness = max;
	}

	/**
	 * Compute the collective offspring the entire species (the sum of all
	 * organism's offspring) is assigned skim is fractional offspring left over
	 * from a previous species that was counted. These fractional parts are kept
	 * unil they add up to 1
	 */
	public double count_offspring(double skim) {
		Iterator<Organism> itr_organism;

		expected_offspring = 0;

		double expNumOffsprings = 0.0;
		double y1 = 1.0;
		double r1 = 0.0;
		double r2 = skim;
		int n1 = 0;
		int n2 = 0;

		itr_organism = organismsInSpecies.iterator();
		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());
			expNumOffsprings = _organism.expected_offspring;

			n1 = (int) (expNumOffsprings / y1);
			r1 = expNumOffsprings - ((int) (expNumOffsprings / y1) * y1);
			n2 = n2 + n1;
			r2 = r2 + r1;

			if (r2 >= 1.0) {
				n2 = n2 + 1;
				r2 = r2 - 1.0;
			}
		}

		expected_offspring = n2;
		return r2;
	}

	/**
	 * Called for printing in a file statistics information for this specie.
	 */
	public void print_to_filename(String xNameFile) {
		//
		// write to file genome in native format (for re-read)
		//
		IOseq xFile;

		xFile = new IOseq(xNameFile);
		xFile.IOseqOpenW(false);

		try {

			print_to_file(xFile);

		} catch (Throwable e) {
			System.err.println(e);
		}

		xFile.IOseqCloseW();

	}

	public void viewtext() {

		System.out.println("\n +SPECIES : ");
		System.out.print("  id < " + id + " >");
		System.out.print(" age=" + age);
		System.out.print(", ave_fitness=" + ave_fitness);
		System.out.print(", max_fitness=" + max_fitness);
		System.out.print(", max_fitness_ever =" + max_fitness_ever);
		System.out.print(", expected_offspring=" + expected_offspring);
		System.out
				.print(", age_of_last_improvement=" + age_of_last_improvement);
		System.out.print("\n  This Species has " + organismsInSpecies.size()
				+ " organisms :");
		System.out.print("\n ---------------------------------------");

		Iterator<Organism> itr_organism = organismsInSpecies.iterator();
		itr_organism = organismsInSpecies.iterator();

		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());
			_organism.viewtext();
		}

	}

	/**
	 * 
	 * costructor with identification and flag for signaling if its a new specie
	 * 
	 */

	public Species(int i, boolean n) {
		id = i;
		age = 1;
		ave_fitness = 0.0;
		expected_offspring = 0;
		novel = n;
		age_of_last_improvement = 0;
		max_fitness = 0;
		max_fitness_ever = 0;
	}

	/**
	 * Compute generations since last improvement
	 */
	public int last_improved() {
		return (age - age_of_last_improvement);
	}

	/**
	 * Eliminate the organism passed in parameter list, from a list of organisms
	 * of this specie
	 */
	public void remove_org(Organism org) {
		boolean rc = false;

		rc = organismsInSpecies.removeElement(org);
		if (!rc) {
			System.out
					.print("\n ALERT: Attempt to remove nonexistent Organism from Species");
		}
	}

	/**
	 * Reproduces the species
	 * 
	 * @param generation
	 * @param pop
	 * @param sorted_species
	 * @return
	 */
	public boolean reproduce(int generation, Population pop,
			Vector<Species> sorted_species) {

		boolean champ_done = false; // Flag the preservation of the champion

		// outside the species
		int count = 0;
		int poolsize = 0;
		int orgnum = 0;

		// The weight mutation power is species specific depending on its age
		double mut_power = Neat.p_weight_mut_power;

		Organism theChamp = null;
		Organism mom = null;
		Organism baby = null;
		Genome new_genome = null;

		// Test if reproduction is possible
		if ((expected_offspring > 0) && (organismsInSpecies.size() == 0)) {
			System.out
					.print("\n ERROR:  ATTEMPT TO REPRODUCE OUT OF EMPTY SPECIES");
			return false;
		}

		poolsize = organismsInSpecies.size() - 1;

		// the champion of the 'this' specie is the first element of the specie;
		theChamp = (Organism) organismsInSpecies.firstElement();

		// Create the designated number of offspring for the Species
		// one at a time
		for (count = 0; count < expected_offspring; count++) {
			if (expected_offspring > Neat.p_pop_size) {
				System.out.print("\n ALERT: EXPECTED OFFSPRING = "
						+ expected_offspring);
			}
			
			// If we have a super_champ (Population champion), finish off some
			// special clones
			if ((theChamp.super_champ_offspring) > 0) {
				//Create special clones of the super champ
				baby = reproduceSuperChamp(theChamp, count, mut_power, pop, generation);
			}else if ((!champ_done)&& (expected_offspring >= Neat.p_min_num_offspring_before_save)) {
				// Clone the species champion if the species is having enough offspring
				baby = cloneOrganism(theChamp, count, generation); 
				champ_done = true;
			} else if ((NeatRoutine.randfloat() < Neat.p_mutate_only_prob)|| poolsize == 1) {
				//Mutate a random parent in the species
				orgnum = NeatRoutine.randint(0, poolsize);
				mom = (Organism) organismsInSpecies.elementAt(orgnum);
				new_genome = mom.genome.duplicate(count);
				baby = mutateOrganism(new_genome, count, generation, poolsize, pop, mut_power);
			} else {
				baby = mate(poolsize, sorted_species, count, pop, generation, mut_power);
			}

			// Add the baby to its proper Species
			// If it doesn't fit a Species, create a new one
			Speciator s = new Speciator();
			Vector<Organism> babyV = new Vector<>();
			babyV.add(0, baby);
			pop.last_species=s.speciate(babyV, pop);

		} // end offspring cycle

		return true;
	}

	/**
	 * Makes a special reproduction of the superChamp (population champ)
	 * 
	 * @param theChamp
	 * @param count
	 * @param mut_power
	 * @param pop
	 * @param mut_struct_baby
	 */
	private Organism reproduceSuperChamp(Organism theChamp, int count,
			double mut_power, Population pop, int generation) {
		boolean mut_struct_baby = false;
		Organism mom = null;
		Organism baby = null;
		Genome new_genome = null;

		mom = theChamp;

		// Create a copy of the champs genome
		new_genome = mom.genome.duplicate(count);

		// If the number of babies that still needs to be based on the super
		// champion is above one,
		// a mutation will occur in the baby
		if ((theChamp.super_champ_offspring) > 1) {
			if ((NeatRoutine.randfloat() < .8)
					|| (Neat.p_mutate_add_link_prob == 0.0)) {
				new_genome.mutate_link_weight(mut_power, 1.0,
						NeatConstant.GAUSSIAN);
			} else {
				new_genome.mutate_add_link(pop, Neat.p_newlink_tries);
				mut_struct_baby = true;
			}
		}

		baby = new Organism(0.0, new_genome, generation);

		// The last baby made off the super champ is a clone
		if ((theChamp.super_champ_offspring) == 1) {
			if (theChamp.pop_champ) {
				// System.out.print("\n The new org baby's (champion) genome is : "+baby.genome.getGenome_id());
				baby.pop_champ_child = true;
				baby.high_fit = mom.orig_fitness;
			}
		}
		theChamp.super_champ_offspring--;
		baby.mut_struct_baby=mut_struct_baby;
		return baby;
	}
	
	private Organism cloneOrganism(Organism org, int count, int generation){
		Organism mom = org;
		Genome new_genome = mom.genome.duplicate(count);
		//Clone mom without any mutations
		Organism baby = new Organism(0.0, new_genome, generation); 
		
		return baby;
	}
	
	private Organism mutateOrganism(Genome genome, int count, int generation, int poolsize, Population pop, double mut_power){
		Genome new_genome = genome;
		boolean mut_struct_baby = false;
		// Do the mutation depending on probabilities of
		// various mutations
		if (NeatRoutine.randfloat() < Neat.p_mutate_add_node_prob) {
			//Add new node
			new_genome.mutate_add_node(pop);
			mut_struct_baby = true;
		} else if (NeatRoutine.randfloat() < Neat.p_mutate_add_link_prob) {
			//Add new link
			new_genome.mutate_add_link(pop, Neat.p_newlink_tries);
			mut_struct_baby = true;
		} else if (NeatRoutine.randfloat() < Neat.p_mutate_add_sensor_prob) {
			//Add new sensor
			new_genome.mutate_add_sensor(pop);
			mut_struct_baby = true;
		} else {
			// If we didn't do a structural mutation, we do the other
			// kinds
			if (NeatRoutine.randfloat() < Neat.p_mutate_random_trait_prob) {
				// mutate random trait
				new_genome.mutate_random_trait();
			}
			if (NeatRoutine.randfloat() < Neat.p_mutate_link_trait_prob) {
				// Mutate link trait
				new_genome.mutate_link_trait(1);
			}
			if (NeatRoutine.randfloat() < Neat.p_mutate_node_trait_prob) {
				//Mutate node trait
				new_genome.mutate_node_trait(1);
			}
			if (NeatRoutine.randfloat() < Neat.p_mutate_link_weights_prob) {
				// Mutate link weights
				new_genome.mutate_link_weight(mut_power, 1.0,
						NeatConstant.GAUSSIAN);
			}
			if (NeatRoutine.randfloat() < Neat.p_mutate_toggle_enable_prob) {
				// Disable a gene
				new_genome.mutate_toggle_enable(1);
			}

			if (NeatRoutine.randfloat() < Neat.p_mutate_gene_reenable_prob) {
				// Reenable a gene
				new_genome.mutate_gene_reenable();
			}
		} 
		
		Organism baby = new Organism(0, new_genome, generation);
		baby.mut_struct_baby = mut_struct_baby;
		return baby;
	}
	
	private Organism mate(int poolsize, Vector<Species> sorted_species, int count, Population pop, int generation, double mut_power){
		int orgnum = 0;
		Organism mom = null;
		Organism dad = null;
		Organism baby = null;
		Genome new_genome = null;
		boolean mate_baby = false;
		
		
		//Choose mom
		orgnum = NeatRoutine.randint(0, poolsize);
		mom = (Organism) organismsInSpecies.elementAt(orgnum);

		// Choose dad
		if (NeatRoutine.randfloat() > Neat.p_interspecies_mate_rate) {
			// Mate within Species
			orgnum = NeatRoutine.randint(0, poolsize);
			dad = (Organism) organismsInSpecies.elementAt(orgnum);
		} else {
			// Mate outside Species
			Species randspecies = this;

			// Select a random species
			int giveup = 0;
			int sp_ext = 0;
			// Give up if you cant find a different Species
			while ((randspecies == this) && (giveup < 5)) {
				// Choose a random species tending towards better
				// species
				double randmult = NeatRoutine.gaussrand() / 4;
				if (randmult > 1.0){
					randmult = 1.0;
				}
				// This tends to select better species
				int randspeciesnum = (int) Math.floor((randmult * (sorted_species.size() - 1.0)) + 0.5);
				for (sp_ext = 0; sp_ext < randspeciesnum; sp_ext++) {
					randspecies = (Species) sorted_species.elementAt(sp_ext);
				}
				++giveup;
			}

			dad = (Organism) randspecies.organismsInSpecies.firstElement();
		}
		
		//Perform crossover
		if (NeatRoutine.randfloat() < Neat.p_mate_multipoint_prob) {
			new_genome = mom.genome.mate_multipoint(dad.genome, count,
					mom.orig_fitness, dad.orig_fitness);
		} else if (NeatRoutine.randfloat() < (Neat.p_mate_multipoint_avg_prob / (Neat.p_mate_multipoint_avg_prob + Neat.p_mate_singlepoint_prob))) {
			// System.out.print("\n    mate multipoint_avg baby: ");
			new_genome = mom.genome.mate_multipoint_avg(dad.genome,
					count, mom.orig_fitness, dad.orig_fitness);
		} else {
			// System.out.print("\n    mate siglepoint baby: ");
			new_genome = mom.genome.mate_singlepoint(dad.genome, count);
		}

		mate_baby = true;

		// Determine whether to mutate the baby's Genome
		// This is done randomly or if the mom and dad are the same
		// organism

		if ((NeatRoutine.randfloat() > Neat.p_mate_only_prob)|| (dad.genome.getGenome_id() == mom.genome.getGenome_id())|| (dad.genome.compatibility(mom.genome) == 0.0)) {
			baby = mutateOrganism(new_genome, count, generation, poolsize, pop, mut_power);
		} else {
			// Create the baby without mutating first
			baby = new Organism(0.0, new_genome, generation);
		}
		baby.mate_baby=mate_baby;
		return baby;

	}

	/**
	 * Print to file all statistics information for this specie; are information
	 * for specie, organisms,winner if present and genome
	 */
	public void print_to_file(IOseq xFile) {

		String mask4 = " 000";
		DecimalFormat fmt4 = new DecimalFormat(mask4);

		String mask13 = " 0.000";
		DecimalFormat fmt13 = new DecimalFormat(mask13);

		// Print a comment on the Species info

		StringBuffer s2 = new StringBuffer("/* Species #");
		s2.append(fmt4.format(id));
		s2.append("         : (size=");
		s2.append(fmt4.format(organismsInSpecies.size()));
		s2.append(") (AvfFit=");
		s2.append(fmt13.format(ave_fitness));
		s2.append(") (Age=");
		s2.append(fmt13.format(age));
		s2.append(")  */");
		xFile.IOseqWrite(s2.toString());

		// System.out.print("\n" + s2);

		s2 = new StringBuffer(
				"/*-------------------------------------------------------------------*/");
		xFile.IOseqWrite(s2.toString());

		Iterator<Organism> itr_organism = organismsInSpecies.iterator();
		itr_organism = organismsInSpecies.iterator();

		while (itr_organism.hasNext()) {
			Organism _organism = ((Organism) itr_organism.next());

			s2 = new StringBuffer("/* Organism #");
			s2.append(fmt4.format(_organism.genome.getGenome_id()));
			s2.append(" Fitness: ");
			s2.append(fmt13.format(_organism.fitness));
			s2.append(" Error: ");
			s2.append(fmt13.format(_organism.error));
			s2.append("                      */");
			xFile.IOseqWrite(s2.toString());

			if (_organism.getWinner()) {
				s2 = new StringBuffer(
						"/*  $  This organism is WINNER with genome_id ");
				s2.append(fmt4.format(_organism.genome.getGenome_id()));
				s2.append(" Species #");
				s2.append(fmt4.format(id));
				s2.append(" $   */");
				xFile.IOseqWrite(s2.toString());
			}

			_organism.getGenome().print_to_file(xFile);

		}

		s2 = new StringBuffer(
				"/*-------------------------------------------------------------------*/");
		xFile.IOseqWrite(s2.toString());

	}
}