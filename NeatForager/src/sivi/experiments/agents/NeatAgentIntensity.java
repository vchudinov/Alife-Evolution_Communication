package sivi.experiments.agents;

import sivi.neat.jneat.evolution.Organism;
import sivi.simulator.simulation.scanners.MasterScanner.SCANNER_TYPE;
/**
 * The only difference between this class and NEATAgentRelative is the kind of scanner it uses
 * @author Simon
 *
 */
public class NeatAgentIntensity extends NeatAgentNoMemory{
	
	private static SCANNER_TYPE type = SCANNER_TYPE.INTENSITY;
	
	public NeatAgentIntensity(int id, ENTITYCOLOR color, int startEnergy,
			Organism org) {		
		super(id, color, startEnergy, org, type);
	}
}
